package electrolyte.greate.foundation.recipe;

import com.gregtechceu.gtceu.data.recipe.GTRecipeTypes;
import electrolyte.greate.content.kinetics.crusher.TieredCrushingRecipe;
import electrolyte.greate.content.processing.recipe.TieredProcessingRecipeParams;
import electrolyte.greate.registry.GreateRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class GreateCrushingRecipes {

    private static final int MAX_OUTPUTS = 7;

    public record Resolution(@Nullable RecipeHolder<TieredCrushingRecipe> recipe, boolean blocked) {

        static Resolution none() {
            return new Resolution(null, false);
        }

        static Resolution denied() {
            return new Resolution(null, true);
        }

        static Resolution of(RecipeHolder<TieredCrushingRecipe> recipe) {
            return new Resolution(recipe, false);
        }

        public boolean found() {
            return recipe != null;
        }
    }

    private static final Resolution NONE = Resolution.none();
    private static final Resolution DENIED = Resolution.denied();

    public static Resolution resolve(Level level, ItemStack stack, int machineTier) {
        if (level == null || stack.isEmpty()) {
            return NONE;
        }
        RecipeManager recipeManager = level.getRecipeManager();
        if (recipeManager == null) {
            return NONE;
        }

        RecipeHolder<TieredCrushingRecipe> best = null;
        int bestTier = Integer.MAX_VALUE;
        for (RecipeHolder<TieredCrushingRecipe> holder : recipeManager.getAllRecipesFor(
                GreateRecipeTypes.crushingType())) {
            TieredCrushingRecipe recipe = holder.value();
            if (recipe.getIngredients().isEmpty() || !recipe.getIngredients().get(0).test(stack)) {
                continue;
            }

            if (recipe.getCircuitNumber() != -1) {
                continue;
            }
            if (best == null || recipe.getRecipeTier() < bestTier) {
                best = holder;
                bestTier = recipe.getRecipeTier();
            }
        }
        if (best != null) {
            return bestTier <= machineTier ? Resolution.of(best) : DENIED;
        }

        GreateGtRecipeBridge.Projection bestProjection = null;
        for (GreateGtRecipeBridge.Projection projection
                : GreateGtRecipeBridge.projections(recipeManager, GTRecipeTypes.MACERATOR_RECIPES)) {
            if (!projection.matches(stack)
                    || projection.inputCount() != 1
                    || projection.hasFluidInput()
                    || projection.outputs().size() > MAX_OUTPUTS
                    || projection.circuitNumber() != -1) {
                continue;
            }
            if (bestProjection == null || projection.recipeTier() < bestProjection.recipeTier()) {
                bestProjection = projection;
            }
        }
        if (bestProjection == null) {
            return NONE;
        }
        return bestProjection.recipeTier() <= machineTier
                ? Resolution.of(synthesize(bestProjection, machineTier))
                : DENIED;
    }

    private static RecipeHolder<TieredCrushingRecipe> synthesize(GreateGtRecipeBridge.Projection projection,
                                                                 int machineTier) {
        TieredProcessingRecipeParams params = TieredProcessingRecipeParams.of(
                projection.input(), projection.outputsAt(machineTier), projection.duration(),
                projection.recipeTier(), projection.circuitNumber());
        return new RecipeHolder<>(projection.id(), new TieredCrushingRecipe(params));
    }

    private GreateCrushingRecipes() {
    }
}
