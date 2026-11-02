package electrolyte.greate.content.kinetics.mixer;

import com.google.common.base.Joiner;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import electrolyte.greate.content.processing.recipe.TieredProcessingRecipeParams;
import electrolyte.greate.registry.GreateRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;

public class TieredMixingRecipe extends BasinRecipe {

    private final int recipeTier;
    private final int circuitNumber;

    public TieredMixingRecipe(TieredProcessingRecipeParams params) {
        super(params);
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
    public RecipeType<?> getType() {
        return GreateRecipeTypes.mixingType();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GreateRecipeTypes.MIXING.getSerializer();
    }

    @Override
    public IRecipeTypeInfo getTypeInfo() {
        return GreateRecipeTypes.MIXING;
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>(super.validate());
        if (recipeTier < 0 || recipeTier > 9) {
            errors.add("Recipe tier " + recipeTier + " is outside the supported range 0..9.");
        }
        if (circuitNumber < -1 || circuitNumber > 32) {
            errors.add("Circuit number " + circuitNumber + " is outside the supported range -1..32.");
        }
        return errors;
    }

    public static class Serializer implements RecipeSerializer<TieredMixingRecipe> {

        private final MapCodec<TieredMixingRecipe> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, TieredMixingRecipe> streamCodec;

        @SuppressWarnings("unchecked")
        public Serializer() {
            this.codec = TieredProcessingRecipeParams.CODEC
                    .xmap(TieredMixingRecipe::new,
                            recipe -> (TieredProcessingRecipeParams) recipe.getParams())
                    .validate(recipe -> {
                        List<String> errors = recipe.validate();
                        if (errors.isEmpty()) {
                            return DataResult.success(recipe);
                        }
                        errors.add(recipe.getClass().getSimpleName() + " failed validation:");
                        return DataResult.error(() -> Joiner.on('\n').join(errors), recipe);
                    });
            this.streamCodec = TieredProcessingRecipeParams.STREAM_CODEC
                    .map(TieredMixingRecipe::new,
                            recipe -> (TieredProcessingRecipeParams) recipe.getParams());
        }

        @Override
        public MapCodec<TieredMixingRecipe> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TieredMixingRecipe> streamCodec() {
            return streamCodec;
        }
    }
}
