package electrolyte.greate.foundation.recipe;

import com.gregtechceu.gtceu.data.recipe.GTRecipeTypes;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import electrolyte.greate.content.kinetics.millstone.TieredMillingRecipe;
import electrolyte.greate.registry.GreateRecipeTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class GreateMillingRecipes {

    public record Resolved(int recipeTier, int circuitNumber, Ingredient input, int inputCount, int duration,
                           List<ProcessingOutput> results) {

        public boolean matches(ItemStack stack) {
            return !stack.isEmpty() && stack.getCount() >= inputCount && input.test(stack);
        }

        public List<ItemStack> roll(RandomSource random) {
            List<ItemStack> rolled = new ArrayList<>(results.size());
            for (ProcessingOutput output : results) {
                ItemStack stack = output.rollOutput(random);
                if (!stack.isEmpty()) {
                    rolled.add(stack);
                }
            }
            return rolled;
        }
    }

    public static boolean isTierAllowed(int recipeTier, int machineTier) {
        return recipeTier <= machineTier;
    }

    public static boolean circuitMatches(int recipeCircuitNumber, int machineCircuitNumber) {
        return recipeCircuitNumber == -1 || recipeCircuitNumber == machineCircuitNumber;
    }

    public static @Nullable Resolved find(Level level, ItemStack stack, int machineTier) {
        return find(level, stack, machineTier, -1);
    }

    public static @Nullable Resolved find(Level level, ItemStack stack, int machineTier, int machineCircuit) {
        if (stack.isEmpty() || level.isClientSide && level.getRecipeManager() == null) {
            return null;
        }
        RecipeManager recipeManager = level.getRecipeManager();

        Resolved best = null;

        for (RecipeHolder<TieredMillingRecipe> holder : recipeManager.getAllRecipesFor(GreateRecipeTypes.millingType())) {
            TieredMillingRecipe recipe = holder.value();
            if (recipe.getIngredients().isEmpty() || !recipe.getIngredients().get(0).test(stack)) {
                continue;
            }
            if (!circuitMatches(recipe.getCircuitNumber(), machineCircuit)) {
                continue;
            }
            Resolved candidate = new Resolved(recipe.getRecipeTier(), recipe.getCircuitNumber(),
                    recipe.getIngredients().get(0), 1, Math.max(1, recipe.getProcessingDuration()),
                    recipe.getRollableResults());
            best = lowerTier(best, candidate);
        }

        for (GreateGtRecipeBridge.Projection projection
                : GreateGtRecipeBridge.projections(recipeManager, GTRecipeTypes.MACERATOR_RECIPES)) {
            if (!projection.matches(stack) || stack.getCount() < projection.inputCount()
                    || projection.hasFluidInput()
                    || !circuitMatches(projection.circuitNumber(), machineCircuit)) {
                continue;
            }
            best = lowerTier(best, new Resolved(projection.recipeTier(), projection.circuitNumber(),
                    projection.input(), projection.inputCount(), projection.duration(),
                    projection.outputsAt(machineTier)));
        }

        for (RecipeHolder<MillingRecipe> holder : recipeManager.getAllRecipesFor(
                AllRecipeTypes.MILLING.<net.minecraft.world.item.crafting.RecipeInput, MillingRecipe>getType())) {
            MillingRecipe recipe = holder.value();
            if (recipe.getIngredients().isEmpty() || !recipe.getIngredients().get(0).test(stack)) {
                continue;
            }
            Resolved candidate = new Resolved(0, -1, recipe.getIngredients().get(0), 1,
                    Math.max(1, recipe.getProcessingDuration()), recipe.getRollableResults());
            best = lowerTier(best, candidate);
        }

        if (best == null) {
            return null;
        }

        return isTierAllowed(best.recipeTier(), machineTier) ? best : null;
    }

    private static @Nullable Resolved lowerTier(@Nullable Resolved current, Resolved candidate) {
        if (current == null || candidate.recipeTier() < current.recipeTier()) {
            return candidate;
        }
        return current;
    }

    public static int tierFromEU(long eut) {
        return GreateGtRecipeBridge.tierFromEU(eut);
    }

    private GreateMillingRecipes() {
    }
}
