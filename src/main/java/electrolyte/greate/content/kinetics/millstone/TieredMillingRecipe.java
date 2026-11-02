package electrolyte.greate.content.kinetics.millstone;

import electrolyte.greate.content.processing.recipe.TieredProcessingRecipeParams;
import electrolyte.greate.content.processing.recipe.TieredStandardProcessingRecipe;
import electrolyte.greate.registry.GreateRecipeTypes;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

public class TieredMillingRecipe extends TieredStandardProcessingRecipe<RecipeInput> {

    public TieredMillingRecipe(TieredProcessingRecipeParams params) {
        super(GreateRecipeTypes.MILLING, params);
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 4;
    }

    @Override
    public boolean canSpecifyDuration() {
        return true;
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        if (input.isEmpty())
            return false;
        return ingredients.get(0).test(input.getItem(0));
    }
}
