package electrolyte.greate.content.processing.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.codec.CreateCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

import com.mojang.datafixers.util.Either;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.gregtechceu.gtceu.api.GTValues.ULV;

@SuppressWarnings("unused")
public class TieredProcessingRecipeParams extends ProcessingRecipeParams {

    public static MapCodec<TieredProcessingRecipeParams> CODEC = tieredCodec(TieredProcessingRecipeParams::new);
    public static StreamCodec<RegistryFriendlyByteBuf, TieredProcessingRecipeParams> STREAM_CODEC =
            tieredStreamCodec(TieredProcessingRecipeParams::new);

    protected int recipeTier;

    protected int circuitNumber;

    protected TieredProcessingRecipeParams() {
        super();
        recipeTier = ULV;
        circuitNumber = -1;
    }

    public static TieredProcessingRecipeParams of(Ingredient input, List<ProcessingOutput> results,
                                                  int processingDuration, int recipeTier, int circuitNumber) {
        TieredProcessingRecipeParams params = new TieredProcessingRecipeParams();
        params.ingredients.add(input);
        params.results.addAll(results);
        params.processingDuration = Math.max(0, processingDuration);
        params.requiredHeat = HeatCondition.NONE;
        params.recipeTier = recipeTier;
        params.circuitNumber = circuitNumber;
        return params;
    }

    protected static <P extends TieredProcessingRecipeParams> MapCodec<P> tieredCodec(Supplier<P> factory) {
        return RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.either(CreateCodecs.FLAT_SIZED_FLUID_INGREDIENT_WITH_TYPE, Ingredient.CODEC).listOf()
                        .fieldOf("ingredients").forGetter(TieredProcessingRecipeParams::ingredientList),
                Codec.either(FluidStack.CODEC, ProcessingOutput.CODEC_NEW).listOf()
                        .fieldOf("results").forGetter(TieredProcessingRecipeParams::resultList),
                Codec.INT.optionalFieldOf("processing_time", 0)
                        .forGetter(TieredProcessingRecipeParams::processingDuration),
                HeatCondition.CODEC.optionalFieldOf("heat_requirement", HeatCondition.NONE)
                        .forGetter(TieredProcessingRecipeParams::requiredHeat),
                Codec.INT.optionalFieldOf("recipe_tier", ULV)
                        .forGetter(TieredProcessingRecipeParams::recipeTier),
                Codec.INT.optionalFieldOf("circuit_number", -1)
                        .forGetter(TieredProcessingRecipeParams::circuitNumber)
        ).apply(i, (ingredients, results, processingDuration, requiredHeat, recipeTier, circuitNumber) -> {
            P params = factory.get();
            ingredients.forEach(e -> e
                    .ifRight(params.ingredients::add)
                    .ifLeft(params.fluidIngredients::add));
            results.forEach(e -> e
                    .ifRight(params.results::add)
                    .ifLeft(params.fluidResults::add));
            params.processingDuration = processingDuration;
            params.requiredHeat = requiredHeat;
            params.recipeTier = recipeTier;
            params.circuitNumber = circuitNumber;
            return params;
        }));
    }

    protected static <P extends TieredProcessingRecipeParams> StreamCodec<RegistryFriendlyByteBuf, P> tieredStreamCodec(
            Supplier<P> factory) {
        return StreamCodec.of(
                (buffer, params) -> params.encode(buffer),
                buffer -> {
                    P params = factory.get();
                    params.decode(buffer);
                    return params;
                });
    }

    private static List<Either<SizedFluidIngredient, Ingredient>> ingredientList(TieredProcessingRecipeParams params) {
        List<Either<SizedFluidIngredient, Ingredient>> combined =
                new ArrayList<>(params.ingredients.size() + params.fluidIngredients.size());
        params.ingredients.forEach(ingredient -> combined.add(Either.right(ingredient)));
        params.fluidIngredients.forEach(ingredient -> combined.add(Either.left(ingredient)));
        return combined;
    }

    private static List<Either<FluidStack, ProcessingOutput>> resultList(TieredProcessingRecipeParams params) {
        List<Either<FluidStack, ProcessingOutput>> combined =
                new ArrayList<>(params.results.size() + params.fluidResults.size());
        params.results.forEach(result -> combined.add(Either.right(result)));
        params.fluidResults.forEach(result -> combined.add(Either.left(result)));
        return combined;
    }

    public final int recipeTier() {
        return recipeTier;
    }

    public final int circuitNumber() {
        return circuitNumber;
    }

    @Override
    protected void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        ByteBufCodecs.VAR_INT.encode(buffer, recipeTier);
        ByteBufCodecs.VAR_INT.encode(buffer, circuitNumber);
    }

    @Override
    protected void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        recipeTier = ByteBufCodecs.VAR_INT.decode(buffer);
        circuitNumber = ByteBufCodecs.VAR_INT.decode(buffer);
    }
}
