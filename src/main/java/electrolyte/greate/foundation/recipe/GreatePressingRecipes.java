package electrolyte.greate.foundation.recipe;

import com.gregtechceu.gtceu.data.recipe.GTRecipeTypes;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import electrolyte.greate.content.kinetics.press.TieredPressingRecipe;
import electrolyte.greate.content.processing.recipe.TieredProcessingRecipeParams;
import electrolyte.greate.registry.GreateRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class GreatePressingRecipes {

    private static final int MAX_OUTPUTS = 2;

    public static boolean canProcess(Level level, ItemStack stack, int machineTier) {
        return find(level, stack, machineTier) != null;
    }

    public static @Nullable RecipeHolder<PressingRecipe> find(Level level, ItemStack stack, int machineTier) {
        return find(level, stack, machineTier, -1);
    }

    public static @Nullable RecipeHolder<PressingRecipe> find(Level level, ItemStack stack, int machineTier,
                                                              int machineCircuit) {
        if (stack.isEmpty()) {
            return null;
        }
        RecipeManager recipeManager = level.getRecipeManager();

        RecipeHolder<PressingRecipe> best = null;
        int bestTier = Integer.MAX_VALUE;
        for (RecipeHolder<TieredPressingRecipe> holder : recipeManager.getAllRecipesFor(
                GreateRecipeTypes.pressingType())) {
            TieredPressingRecipe recipe = holder.value();
            if (!matches(recipe, stack) || !GreateMillingRecipes.circuitMatches(
                    recipe.getCircuitNumber(), machineCircuit)) {
                continue;
            }
            if (recipe.getRecipeTier() < bestTier) {
                bestTier = recipe.getRecipeTier();
                best = asPressingHolder(holder);
            }
        }
        if (best != null) {
            return bestTier <= machineTier ? best : null;
        }

        RecipeHolder<PressingRecipe> gt = findGt(
                recipeManager, GTRecipeTypes.BENDER_RECIPES, stack, machineTier, machineCircuit);
        gt = lowerTier(gt, findGt(
                recipeManager, GTRecipeTypes.COMPRESSOR_RECIPES, stack, machineTier, machineCircuit));
        if (gt != null) {
            int gtTier = ((TieredPressingRecipe) gt.value()).getRecipeTier();
            return gtTier <= machineTier ? gt : null;
        }

        for (RecipeHolder<PressingRecipe> holder : recipeManager.getAllRecipesFor(createPressingType())) {
            if (matches(holder.value(), stack)) {
                return holder;
            }
        }
        return null;
    }

    private static @Nullable RecipeHolder<PressingRecipe> findGt(
            RecipeManager recipeManager, com.gregtechceu.gtceu.api.recipe.GTRecipeType type,
            ItemStack stack, int machineTier, int machineCircuit) {
        GreateGtRecipeBridge.Projection bestProjection = null;
        for (GreateGtRecipeBridge.Projection projection : GreateGtRecipeBridge.projections(recipeManager, type)) {
            if (!projection.matches(stack)
                    || projection.inputCount() != 1
                    || projection.hasFluidInput()
                    || projection.outputs().size() > MAX_OUTPUTS
                    || !GreateMillingRecipes.circuitMatches(projection.circuitNumber(), machineCircuit)) {
                continue;
            }
            if (bestProjection == null || projection.recipeTier() < bestProjection.recipeTier()) {
                bestProjection = projection;
            }
        }
        return bestProjection == null ? null : synthesize(bestProjection, machineTier);
    }

    private static RecipeHolder<PressingRecipe> synthesize(GreateGtRecipeBridge.Projection projection,
                                                           int machineTier) {
        TieredProcessingRecipeParams params = TieredProcessingRecipeParams.of(
                projection.input(), projection.outputsAt(machineTier), projection.duration(),
                projection.recipeTier(), projection.circuitNumber());
        return new RecipeHolder<>(projection.id(), new TieredPressingRecipe(params));
    }

    private static @Nullable RecipeHolder<PressingRecipe> lowerTier(
            @Nullable RecipeHolder<PressingRecipe> current, @Nullable RecipeHolder<PressingRecipe> candidate) {
        if (candidate == null) {
            return current;
        }
        if (current == null) {
            return candidate;
        }
        if (!(current.value() instanceof TieredPressingRecipe currentTiered)) {
            return current;
        }
        if (!(candidate.value() instanceof TieredPressingRecipe candidateTiered)) {
            return current;
        }
        return candidateTiered.getRecipeTier() < currentTiered.getRecipeTier() ? candidate : current;
    }

    private static boolean matches(PressingRecipe recipe, ItemStack stack) {
        return !recipe.getIngredients().isEmpty() && recipe.getIngredients().get(0).test(stack);
    }

    @SuppressWarnings("unchecked")
    private static RecipeType<PressingRecipe> createPressingType() {
        return (RecipeType<PressingRecipe>) (RecipeType<?>) AllRecipeTypes.PRESSING.getType();
    }

    @SuppressWarnings("unchecked")
    private static RecipeHolder<PressingRecipe> asPressingHolder(RecipeHolder<TieredPressingRecipe> holder) {
        return (RecipeHolder<PressingRecipe>) (RecipeHolder<?>) holder;
    }

    private GreatePressingRecipes() {
    }
}
