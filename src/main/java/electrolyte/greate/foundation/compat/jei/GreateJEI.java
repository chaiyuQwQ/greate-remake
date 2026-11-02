package electrolyte.greate.foundation.compat.jei;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import electrolyte.greate.Greate;
import electrolyte.greate.content.kinetics.crusher.TieredCrushingRecipe;
import electrolyte.greate.content.kinetics.millstone.TieredMillingRecipe;
import electrolyte.greate.content.kinetics.press.TieredPressingRecipe;
import electrolyte.greate.content.kinetics.saw.TieredSawingRecipe;
import electrolyte.greate.registry.CrushingWheels;
import electrolyte.greate.registry.EncasedFans;
import electrolyte.greate.registry.Gearboxes;
import electrolyte.greate.registry.GreateRecipeTypes;
import electrolyte.greate.registry.MechanicalMixers;
import electrolyte.greate.registry.MechanicalPresses;
import electrolyte.greate.registry.Millstones;
import electrolyte.greate.registry.Saws;
import electrolyte.greate.registry.VerticalGearboxes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;

@JeiPlugin
public final class GreateJEI implements IModPlugin {

    public static final ResourceLocation UID = Greate.id("jei_plugin");

    public static final ResourceLocation MILLING_CATEGORY_ID = Greate.id("milling");
    public static final ResourceLocation PRESSING_CATEGORY_ID = Greate.id("pressing");
    public static final ResourceLocation MIXING_CATEGORY_ID = Greate.id("mixing");
    public static final ResourceLocation CRUSHING_CATEGORY_ID = Greate.id("crushing");
    public static final ResourceLocation SAWING_CATEGORY_ID = Greate.id("sawing");

    private GreateMillingCategory millingCategory;
    private GreatePressingCategory pressingCategory;
    private GreateMixingCategory mixingCategory;
    private GreateCrushingCategory crushingCategory;
    private GreateSawingCategory sawingCategory;

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        millingCategory = new GreateMillingCategory(new CreateRecipeCategory.Info<>(
                RecipeType.createRecipeHolderType(MILLING_CATEGORY_ID),
                Component.translatable("greate_remake.recipe.milling"),
                new EmptyBackground(177, 53),
                new DoubleItemIcon(() -> edgeStack(Millstones.ALL.isEmpty() ? null : Millstones.ALL.get(0).get()),
                        () -> edgeStack(Millstones.ALL.isEmpty() ? null
                                : Millstones.ALL.get(Millstones.ALL.size() - 1).get())),
                GreateJEI::millingRecipes,
                tierStacks(Millstones.ALL.size(), i -> new ItemStack(Millstones.ALL.get(i).get()))));
        registration.addRecipeCategories(millingCategory);

        pressingCategory = new GreatePressingCategory(new CreateRecipeCategory.Info<>(
                RecipeType.createRecipeHolderType(PRESSING_CATEGORY_ID),
                Component.translatable("greate_remake.recipe.pressing"),
                new EmptyBackground(177, 70),
                new DoubleItemIcon(() -> edgeStack(MechanicalPresses.ALL.isEmpty() ? null
                                : MechanicalPresses.ALL.get(0).get()),
                        () -> edgeStack(MechanicalPresses.ALL.isEmpty() ? null
                                : MechanicalPresses.ALL.get(MechanicalPresses.ALL.size() - 1).get())),
                GreateJEI::pressingRecipes,
                tierStacks(MechanicalPresses.ALL.size(),
                        i -> new ItemStack(MechanicalPresses.ALL.get(i).get()))));
        registration.addRecipeCategories(pressingCategory);

        mixingCategory = new GreateMixingCategory(new CreateRecipeCategory.Info<>(
                RecipeType.createRecipeHolderType(MIXING_CATEGORY_ID),
                Component.translatable("greate_remake.recipe.mixing"),
                new EmptyBackground(177, 103),
                new DoubleItemIcon(() -> edgeStack(MechanicalMixers.ALL.isEmpty() ? null
                                : MechanicalMixers.ALL.get(0).get()),
                        () -> new ItemStack(AllBlocks.BASIN.get())),
                GreateJEI::mixingRecipes,
                mixCatalysts()));
        registration.addRecipeCategories(mixingCategory);

        crushingCategory = new GreateCrushingCategory(new CreateRecipeCategory.Info<>(
                RecipeType.createRecipeHolderType(CRUSHING_CATEGORY_ID),
                Component.translatable("greate_remake.recipe.crushing"),
                new EmptyBackground(177, 100),
                new DoubleItemIcon(() -> edgeStack(CrushingWheels.WHEELS.isEmpty() ? null
                                : CrushingWheels.WHEELS.get(0).get()),
                        () -> edgeStack(CrushingWheels.WHEELS.isEmpty() ? null
                                : CrushingWheels.WHEELS.get(CrushingWheels.WHEELS.size() - 1).get())),
                GreateJEI::crushingRecipes,
                tierStacks(CrushingWheels.WHEELS.size(),
                        i -> new ItemStack(CrushingWheels.WHEELS.get(i).get()))));
        registration.addRecipeCategories(crushingCategory);

        sawingCategory = new GreateSawingCategory(new CreateRecipeCategory.Info<>(
                RecipeType.createRecipeHolderType(SAWING_CATEGORY_ID),
                Component.translatable("greate_remake.recipe.sawing"),
                new EmptyBackground(177, 70),
                new DoubleItemIcon(() -> edgeStack(Saws.ALL.isEmpty() ? null : Saws.ALL.get(0).get()),
                        () -> edgeStack(Saws.ALL.isEmpty() ? null
                                : Saws.ALL.get(Saws.ALL.size() - 1).get())),
                GreateJEI::sawingRecipes,
                tierStacks(Saws.ALL.size(), i -> new ItemStack(Saws.ALL.get(i).get()))),
                GreateSawingCategory.animatedSaw());
        registration.addRecipeCategories(sawingCategory);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (millingCategory != null) {
            millingCategory.registerRecipes(registration);
        }
        if (pressingCategory != null) {
            pressingCategory.registerRecipes(registration);
        }
        if (mixingCategory != null) {
            mixingCategory.registerRecipes(registration);
        }
        if (crushingCategory != null) {
            crushingCategory.registerRecipes(registration);
        }
        if (sawingCategory != null) {
            sawingCategory.registerRecipes(registration);
        }

        Greate.LOGGER.info("[Greate][jei] 分类配方数 milling={}, pressing={}, mixing={}, crushing={}, sawing={}",
                millingRecipes().size(), pressingRecipes().size(), mixingRecipes().size(),
                crushingRecipes().size(), sawingRecipes().size());

        Component[] fanInfo = {
                Component.translatable("greate_remake.jei.fan.info"),
                Component.translatable("greate_remake.jei.fan.info2"),
                Component.translatable("greate_remake.jei.fan.info3")
        };
        for (var entry : EncasedFans.ALL) {
            registration.addIngredientInfo(entry.get(), fanInfo);
        }

        Component[] gearboxInfo = {
                Component.translatable("greate_remake.jei.gearbox.info"),
                Component.translatable("greate_remake.jei.gearbox.info2")
        };
        for (var entry : Gearboxes.ALL) {
            registration.addIngredientInfo(entry.get(), gearboxInfo);
        }
        for (var entry : VerticalGearboxes.ALL) {
            registration.addIngredientInfo(entry.get(), gearboxInfo);
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (millingCategory != null) {
            millingCategory.registerCatalysts(registration);
        }
        if (pressingCategory != null) {
            pressingCategory.registerCatalysts(registration);
        }
        if (mixingCategory != null) {
            mixingCategory.registerCatalysts(registration);
        }
        if (crushingCategory != null) {
            crushingCategory.registerCatalysts(registration);
        }
        if (sawingCategory != null) {
            sawingCategory.registerCatalysts(registration);
        }

        for (String path : FAN_CATEGORY_PATHS) {
            RecipeType<?> type = RecipeType.createRecipeHolderType(
                    ResourceLocation.fromNamespaceAndPath("create", path));
            for (var entry : EncasedFans.ALL) {
                registration.addRecipeCatalysts(type, entry.get());
            }
        }
    }

    private static final String[] FAN_CATEGORY_PATHS = {
            "fan_smoking", "fan_blasting", "fan_washing", "fan_haunting"
    };

    private static List<RecipeHolder<TieredMillingRecipe>> millingRecipes() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }
        return level.getRecipeManager().getAllRecipesFor(GreateRecipeTypes.millingType());
    }

    private static List<RecipeHolder<TieredPressingRecipe>> pressingRecipes() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }
        return level.getRecipeManager().getAllRecipesFor(GreateRecipeTypes.pressingType());
    }

    @SuppressWarnings("unchecked")
    private static List<RecipeHolder<BasinRecipe>> mixingRecipes() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }
        List<? extends RecipeHolder<? extends BasinRecipe>> source =
                level.getRecipeManager().getAllRecipesFor(GreateRecipeTypes.mixingType());
        List<RecipeHolder<BasinRecipe>> result = new ArrayList<>(source.size());
        for (RecipeHolder<? extends BasinRecipe> holder : source) {
            result.add((RecipeHolder<BasinRecipe>) holder);
        }
        return result;
    }

    private static List<RecipeHolder<TieredCrushingRecipe>> crushingRecipes() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }
        return level.getRecipeManager().getAllRecipesFor(GreateRecipeTypes.crushingType());
    }

    private static List<RecipeHolder<TieredSawingRecipe>> sawingRecipes() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }
        return level.getRecipeManager().getAllRecipesFor(GreateRecipeTypes.sawingType());
    }

    private static ItemStack edgeStack(net.minecraft.world.level.block.Block block) {
        return block == null ? ItemStack.EMPTY : new ItemStack(block);
    }

    private static List<Supplier<? extends ItemStack>> tierStacks(int count, IntFunction<ItemStack> factory) {
        List<Supplier<? extends ItemStack>> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int index = i;
            list.add(() -> factory.apply(index));
        }
        return list;
    }

    private static List<Supplier<? extends ItemStack>> mixCatalysts() {
        List<Supplier<? extends ItemStack>> list = new ArrayList<>(tierStacks(MechanicalMixers.ALL.size(),
                i -> new ItemStack(MechanicalMixers.ALL.get(i).get())));
        list.add(() -> new ItemStack(AllBlocks.BASIN.get()));
        return list;
    }
}
