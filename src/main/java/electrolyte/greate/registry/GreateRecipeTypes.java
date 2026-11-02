package electrolyte.greate.registry;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import electrolyte.greate.Greate;
import electrolyte.greate.content.kinetics.crusher.TieredCrushingRecipe;
import electrolyte.greate.content.kinetics.millstone.TieredMillingRecipe;
import electrolyte.greate.content.kinetics.mixer.TieredMixingRecipe;
import electrolyte.greate.content.kinetics.press.TieredPressingRecipe;
import electrolyte.greate.content.kinetics.saw.TieredSawingRecipe;
import electrolyte.greate.content.processing.recipe.TieredStandardProcessingRecipe;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public enum GreateRecipeTypes implements IRecipeTypeInfo, StringRepresentable {

    MILLING(() -> new TieredStandardProcessingRecipe.Serializer<>(TieredMillingRecipe::new)),
    PRESSING(TieredPressingRecipe.Serializer::new),
    MIXING(TieredMixingRecipe.Serializer::new),
    CRUSHING(TieredCrushingRecipe.Serializer::new),
    SAWING(TieredSawingRecipe.Serializer::new);

    public final ResourceLocation id;
    private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> serializer;
    private final DeferredHolder<RecipeType<?>, RecipeType<?>> type;

    GreateRecipeTypes(Supplier<RecipeSerializer<?>> serializerFactory) {
        String string = Lang.asId(name());
        this.id = Greate.id(string);
        this.serializer = Registers.SERIALIZER_REGISTER.register(string, serializerFactory);
        this.type = Registers.TYPE_REGISTER.register(string, () -> RecipeType.simple(id));
    }

    public static void register(IEventBus modEventBus) {
        Registers.SERIALIZER_REGISTER.register(modEventBus);
        Registers.TYPE_REGISTER.register(modEventBus);
    }

    private static final class Registers {
        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER =
                DeferredRegister.create(Registries.RECIPE_SERIALIZER, Greate.MOD_ID);
        private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER =
                DeferredRegister.create(Registries.RECIPE_TYPE, Greate.MOD_ID);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializer.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) type.get();
    }

    @Override
    public String getSerializedName() {
        return id.toString();
    }

    @SuppressWarnings("unchecked")
    public static RecipeType<TieredMillingRecipe> millingType() {
        return (RecipeType<TieredMillingRecipe>) (RecipeType<?>) MILLING.getType();
    }

    @SuppressWarnings("unchecked")
    public static RecipeType<TieredPressingRecipe> pressingType() {
        return (RecipeType<TieredPressingRecipe>) (RecipeType<?>) PRESSING.getType();
    }

    @SuppressWarnings("unchecked")
    public static RecipeType<TieredMixingRecipe> mixingType() {
        return (RecipeType<TieredMixingRecipe>) (RecipeType<?>) MIXING.getType();
    }

    @SuppressWarnings("unchecked")
    public static RecipeType<TieredCrushingRecipe> crushingType() {
        return (RecipeType<TieredCrushingRecipe>) (RecipeType<?>) CRUSHING.getType();
    }

    @SuppressWarnings("unchecked")
    public static RecipeType<TieredSawingRecipe> sawingType() {
        return (RecipeType<TieredSawingRecipe>) (RecipeType<?>) SAWING.getType();
    }
}
