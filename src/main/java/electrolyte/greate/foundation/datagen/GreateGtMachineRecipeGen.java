package electrolyte.greate.foundation.datagen;

import com.gregtechceu.gtceu.api.material.ChemicalHelper;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.gregtechceu.gtceu.data.item.GTItems;
import com.gregtechceu.gtceu.data.machine.GTMachines;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.Create;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeBuilder;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import electrolyte.greate.Greate;
import electrolyte.greate.registry.Cogwheels;
import electrolyte.greate.registry.CrushingWheels;
import electrolyte.greate.registry.GreateTiers;
import electrolyte.greate.registry.Shafts;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

import static com.gregtechceu.gtceu.api.GTValues.LV;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.data.recipe.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.gregtechceu.gtceu.data.recipe.GTRecipeTypes.CHEMICAL_BATH_RECIPES;
import static com.gregtechceu.gtceu.data.recipe.GTRecipeTypes.CUTTER_RECIPES;

public class GreateGtMachineRecipeGen extends RecipeProvider {

    public GreateGtMachineRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput out) {

        ASSEMBLER_RECIPES.recipeBuilder(Greate.id("hopper_iron"))
                .inputItems(TagPrefix.plate, GTMaterials.Iron, 5)
                .inputItems(Tags.Items.CHESTS_WOODEN)
                .outputItems(Items.HOPPER)
                .circuitMeta(10)
                .duration(20 * 40)
                .EUt(2)
                .save(out);

        ASSEMBLER_RECIPES.recipeBuilder(Greate.id("chute_iron"))
                .inputItems(TagPrefix.plate, GTMaterials.Iron, 5)
                .inputItems(Tags.Items.CHESTS_WOODEN)
                .outputItems(AllBlocks.CHUTE)
                .circuitMeta(11)
                .duration(20 * 40)
                .EUt(2)
                .save(out);

        ASSEMBLER_RECIPES.recipeBuilder(Greate.id("basin"))
                .inputItems(andesitePlate(), 5)
                .outputItems(AllBlocks.BASIN)
                .circuitMeta(1)
                .duration(20 * 35)
                .EUt(4)
                .save(out);

        ASSEMBLER_RECIPES.recipeBuilder(Greate.id("depot"))
                .inputItems(andesitePlate())
                .inputItems(AllBlocks.ANDESITE_CASING)
                .outputItems(AllBlocks.DEPOT)
                .circuitMeta(3)
                .duration(20 * 15)
                .EUt(VA[LV])
                .save(out);

        ASSEMBLER_RECIPES.recipeBuilder(Greate.id("item_vault_iron"))
                .inputItems(TagPrefix.screw, GTMaterials.Iron, 2)
                .inputItems(GTMachines.WOODEN_CRATE)
                .inputItems(TagPrefix.plate, GTMaterials.Iron, 2)
                .outputItems(AllBlocks.ITEM_VAULT, 2)
                .circuitMeta(1)
                .duration(20 * 20)
                .EUt(VA[LV])
                .save(out);

        ASSEMBLER_RECIPES.recipeBuilder(Greate.id("electron_tube"))
                .inputItems(TagPrefix.wireGtSingle, GTMaterials.Steel, 2)
                .inputItems(GTItems.GLASS_TUBE)
                .inputItems(AllItems.POLISHED_ROSE_QUARTZ)
                .outputItems(AllItems.ELECTRON_TUBE, 2)
                .circuitMeta(1)
                .duration(20 * 20)
                .EUt(VA[LV])
                .save(out);

        for (GreateTiers.TierMaterial tierMaterial : GreateTiers.MATERIALS) {
            int tier = tierMaterial.tier();
            ItemStack plate = tier == 0 ? andesitePlate() : plate(tierMaterial);
            if (plate.isEmpty()) {
                Greate.LOGGER.warn("[Greate] datagen: 跳过 {} 的切割机轴配方（没有可用的板）", tierMaterial.id());
                continue;
            }
            CUTTER_RECIPES.recipeBuilder(Greate.id("shaft_" + tierMaterial.id()))
                    .inputItems(plate)
                    .outputItems(new ItemStack(Shafts.ALL.get(tier).get()), 2)
                    .duration(20 * 5)
                    .EUt(VA[LV])
                    .save(out);
        }

        ASSEMBLER_RECIPES.recipeBuilder(Greate.id("fluid_tank_copper"))
                .inputItems(TagPrefix.screw, GTMaterials.Copper, 2)
                .inputItems(GTMachines.WOODEN_DRUM)
                .inputFluids(GTMaterials.Glass.getFluid(2000))
                .outputItems(AllBlocks.FLUID_TANK)
                .circuitMeta(17)
                .duration(20 * 20)
                .EUt(VA[LV])
                .save(out);

        for (DyeColor color : DyeColor.values()) {
            String dye = color.getName();
            Item seat = item(Create.asResource(dye + "_seat"));
            Item valveHandle = item(Create.asResource(dye + "_valve_handle"));
            if (seat != null) {
                CHEMICAL_BATH_RECIPES.recipeBuilder(Greate.id(dye + "_seat"))
                        .inputItems(AllItemTags.SEATS.tag)
                        .inputFluids(GTMaterials.CHEMICAL_DYES[color.ordinal()].getFluid(1000))
                        .outputItems(seat)
                        .duration(20)
                        .EUt(VA[LV])
                        .save(out);
            }
            if (valveHandle != null) {
                CHEMICAL_BATH_RECIPES.recipeBuilder(Greate.id(dye + "_valve_handle"))
                        .inputItems(AllItemTags.VALVE_HANDLES.tag)
                        .inputFluids(GTMaterials.CHEMICAL_DYES[color.ordinal()].getFluid(1000))
                        .outputItems(valveHandle)
                        .duration(20)
                        .EUt(VA[LV])
                        .save(out);
            }
        }
        CHEMICAL_BATH_RECIPES.recipeBuilder(Greate.id("decolor_seat"))
                .inputItems(AllItemTags.SEATS.tag)
                .inputFluids(GTMaterials.Chlorine.getFluid(20))
                .outputItems(itemOrAir(Create.asResource("white_seat")))
                .duration(400)
                .EUt(2)
                .save(out);
        CHEMICAL_BATH_RECIPES.recipeBuilder(Greate.id("decolor_valve_handle"))
                .inputItems(AllItemTags.VALVE_HANDLES.tag)
                .inputFluids(GTMaterials.Chlorine.getFluid(20))
                .outputItems(itemOrAir(Create.asResource("copper_valve_handle")))
                .duration(400)
                .EUt(2)
                .save(out);

        for (GreateTiers.TierMaterial tierMaterial : GreateTiers.MATERIALS) {
            int tier = tierMaterial.tier();
            ItemStack plate = tier == 0 ? andesitePlate() : plate(tierMaterial);
            if (plate.isEmpty()) {
                continue;
            }
            ItemStack shaft = new ItemStack(Shafts.ALL.get(tier).get());
            ItemStack cogwheel = new ItemStack(Cogwheels.SMALL.get(tier).get());
            ItemStack largeCogwheel = new ItemStack(Cogwheels.LARGE.get(tier).get());

            new ItemApplicationRecipe.Builder<>(DeployerApplicationRecipe::new,
                    Greate.id(tierMaterial.id() + "_cogwheel_deploying"))
                    .withItemIngredients(Ingredient.of(shaft.getItem()), Ingredient.of(plate.getItem()))
                    .withItemOutputs(new ProcessingOutput(cogwheel, 1))
                    .build(out);

            new ItemApplicationRecipe.Builder<>(DeployerApplicationRecipe::new,
                    Greate.id(tierMaterial.id() + "_large_cogwheel_deploying"))
                    .withItemIngredients(Ingredient.of(cogwheel.getItem()), Ingredient.of(plate.getItem()))
                    .withItemOutputs(new ProcessingOutput(largeCogwheel, 1))
                    .build(out);
        }

        new SequencedAssemblyRecipeBuilder(Greate.id("precision_mechanism"))
                .require(com.simibubi.create.AllTags.commonItemTag("plates/gold"))
                .transitionTo(AllItems.INCOMPLETE_PRECISION_MECHANISM.get())
                .addStep(DeployerApplicationRecipe::new, r -> r.require(Cogwheels.SMALL.get(0).get()))
                .addStep(DeployerApplicationRecipe::new, r -> r.require(Cogwheels.LARGE.get(0).get()))
                .addStep(DeployerApplicationRecipe::new, r -> r.require(Items.IRON_NUGGET))
                .addOutput(AllItems.PRECISION_MECHANISM.get(), 120)
                .addOutput(AllItems.GOLDEN_SHEET.get(), 8)
                .addOutput(AllItems.ANDESITE_ALLOY.get(), 8)
                .addOutput(AllBlocks.COGWHEEL.get(), 5)
                .addOutput(Items.GOLD_NUGGET, 3)
                .addOutput(AllBlocks.SHAFT.get(), 2)
                .addOutput(AllItems.CRUSHED_GOLD.get(), 2)
                .addOutput(Items.IRON_INGOT, 1)
                .addOutput(Items.CLOCK, 1)
                .loops(5)
                .build(out);

        for (GreateTiers.TierMaterial tierMaterial : GreateTiers.MATERIALS) {
            int tier = tierMaterial.tier();
            ItemStack plate = tier == 0 ? andesitePlate() : plate(tierMaterial);
            if (plate.isEmpty()) {
                continue;
            }
            ItemStack shaft = new ItemStack(Shafts.ALL.get(tier).get());
            ItemStack cogwheel = new ItemStack(Cogwheels.SMALL.get(tier).get());
            ItemStack largeCogwheel = new ItemStack(Cogwheels.LARGE.get(tier).get());

            new SequencedAssemblyRecipeBuilder(Greate.id(tierMaterial.id() + "_large_cogwheel"))
                    .require(shaft.getItem())
                    .transitionTo(cogwheel.getItem())
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(plate.getItem()))
                    .addStep(DeployerApplicationRecipe::new, r -> r.require(plate.getItem()))
                    .addOutput(largeCogwheel, 1)
                    .loops(1)
                    .build(out);
        }

        ItemStack steelPlate = ChemicalHelper.get(TagPrefix.plate, GTMaterials.Steel);
        for (GreateTiers.TierMaterial tierMaterial : GreateTiers.MATERIALS) {
            int tier = tierMaterial.tier();
            ItemStack plate = tier == 0 ? andesitePlate() : plate(tierMaterial);
            if (plate.isEmpty()) {
                continue;
            }
            MechanicalCraftingRecipeBuilder
                    .shapedRecipe(CrushingWheels.WHEELS.get(tier).get(), 2)
                    .key('A', plate.getItem())
                    .key('C', steelPlate.getItem())
                    .key('S', Shafts.ALL.get(tier).get().asItem())
                    .patternLine(" AAA ")
                    .patternLine("AACAA")
                    .patternLine("ACSCA")
                    .patternLine("AACAA")
                    .patternLine(" AAA ")
                    .build(out, Greate.id(tierMaterial.id() + "_crushing_wheel"));
        }
    }

    private static Item item(ResourceLocation id) {
        Item item = BuiltInRegistries.ITEM.get(id);
        return item == Items.AIR ? null : item;
    }

    private static Item itemOrAir(ResourceLocation id) {
        return BuiltInRegistries.ITEM.get(id);
    }

    private static ItemStack andesitePlate() {
        return new ItemStack(AllItems.ANDESITE_ALLOY.get());
    }

    private static ItemStack plate(GreateTiers.TierMaterial tierMaterial) {
        Material material = materialOf(tierMaterial);
        if (material == null) {
            return ItemStack.EMPTY;
        }
        ItemStack plate = ChemicalHelper.get(TagPrefix.plate, material);
        return plate.isEmpty() ? ChemicalHelper.get(TagPrefix.ingot, material) : plate;
    }

    private static Material materialOf(GreateTiers.TierMaterial tierMaterial) {
        return switch (tierMaterial.id()) {
            case "steel" -> GTMaterials.Steel;
            case "aluminium" -> GTMaterials.Aluminium;
            case "stainless_steel" -> GTMaterials.StainlessSteel;
            case "titanium" -> GTMaterials.Titanium;
            case "tungsten_steel" -> GTMaterials.TungstenSteel;
            case "rhodium_plated_palladium" -> GTMaterials.RhodiumPlatedPalladium;
            case "naquadah_alloy" -> GTMaterials.NaquadahAlloy;
            case "darmstadtium" -> GTMaterials.Darmstadtium;
            case "neutronium" -> GTMaterials.Neutronium;
            default -> null;
        };
    }
}
