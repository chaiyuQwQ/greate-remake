package electrolyte.greate.content.processing.recipe;

import com.google.common.base.Joiner;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.ArrayList;
import java.util.List;

public abstract class TieredProcessingRecipe<I extends RecipeInput, P extends TieredProcessingRecipeParams>
        extends ProcessingRecipe<I, P> {

    protected final int recipeTier;
    protected final int circuitNumber;

    public TieredProcessingRecipe(IRecipeTypeInfo typeInfo, P params) {
        super(typeInfo, params);
        this.recipeTier = params.recipeTier();
        this.circuitNumber = params.circuitNumber();
    }

    public int getRecipeTier() {
        return recipeTier;
    }

    public int getCircuitNumber() {
        return circuitNumber;
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        int ingredientCount = ingredients.size();
        int outputCount = results.size();

        if (ingredientCount > getMaxInputCount())
            errors.add("Recipe has more item inputs (" + ingredientCount + ") than supported ("
                    + getMaxInputCount() + ").");
        if (outputCount > getMaxOutputCount())
            errors.add("Recipe has more item outputs (" + outputCount + ") than supported ("
                    + getMaxOutputCount() + ").");

        ingredientCount = fluidIngredients.size();
        outputCount = fluidResults.size();
        if (ingredientCount > getMaxFluidInputCount())
            errors.add("Recipe has more fluid inputs (" + ingredientCount + ") than supported ("
                    + getMaxFluidInputCount() + ").");
        if (outputCount > getMaxFluidOutputCount())
            errors.add("Recipe has more fluid outputs (" + outputCount + ") than supported ("
                    + getMaxFluidOutputCount() + ").");

        if (processingDuration > 0 && !canSpecifyDuration())
            errors.add("Recipe specified a duration. Durations have no impact on this type of recipe.");
        if (requiredHeat != HeatCondition.NONE && !canRequireHeat())
            errors.add("Recipe specified a heat condition. Heat conditions have no impact on this type of recipe.");

        if (recipeTier < 0 || recipeTier > 9)
            errors.add("Recipe tier " + recipeTier + " is outside the supported range 0..9.");
        if (circuitNumber < -1 || circuitNumber > 32)
            errors.add("Circuit number " + circuitNumber + " is outside the supported range -1..32.");

        return errors;
    }

    public static <P extends TieredProcessingRecipeParams, R extends TieredProcessingRecipe<?, P>> MapCodec<R> codec(
            Factory<P, R> factory, MapCodec<P> paramsCodec) {
        return paramsCodec.xmap(factory::create, recipe -> recipe.getParams()).validate(recipe -> {
            var errors = recipe.validate();
            if (errors.isEmpty())
                return DataResult.success(recipe);
            errors.add(recipe.getClass().getSimpleName() + " failed validation:");
            return DataResult.error(() -> Joiner.on('\n').join(errors), recipe);
        });
    }

    public static <P extends TieredProcessingRecipeParams, R extends TieredProcessingRecipe<?, P>> StreamCodec<RegistryFriendlyByteBuf, R> streamCodec(
            Factory<P, R> factory, StreamCodec<RegistryFriendlyByteBuf, P> streamCodec) {
        return streamCodec.map(factory::create, TieredProcessingRecipe::getParams);
    }

    @FunctionalInterface
    public interface Factory<P extends TieredProcessingRecipeParams, R extends TieredProcessingRecipe<?, P>>
            extends ProcessingRecipe.Factory<P, R> {
        @Override
        R create(P params);
    }
}
