package electrolyte.greate.foundation.gametest;

import com.gregtechceu.gtceu.api.material.ChemicalHelper;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import electrolyte.greate.Greate;
import electrolyte.greate.content.fluids.pump.TieredPumpBlockEntity;
import electrolyte.greate.content.kinetics.belt.TieredBeltBlock;
import electrolyte.greate.content.kinetics.belt.TieredBeltBlockEntity;
import electrolyte.greate.content.kinetics.belt.item.TieredBeltConnectorItem;
import electrolyte.greate.content.kinetics.crusher.TieredCrushingRecipe;
import electrolyte.greate.content.kinetics.crusher.TieredCrushingWheelBlockEntity;
import electrolyte.greate.content.kinetics.crusher.TieredCrushingWheelControllerBlockEntity;
import electrolyte.greate.content.kinetics.fan.TieredAirCurrent;
import electrolyte.greate.content.kinetics.fan.TieredEncasedFanBlockEntity;
import electrolyte.greate.content.kinetics.fan.TieredFanProcessing;
import electrolyte.greate.content.kinetics.fan.TieredSplashingType;
import electrolyte.greate.content.kinetics.gearbox.TieredGearboxBlock;
import electrolyte.greate.content.kinetics.gearbox.TieredGearboxBlockEntity;
import electrolyte.greate.content.kinetics.gearbox.TieredVerticalGearboxItem;
import electrolyte.greate.content.kinetics.millstone.TieredMillstoneBlockEntity;
import electrolyte.greate.content.kinetics.mixer.TieredMechanicalMixerBlockEntity;
import electrolyte.greate.content.kinetics.mixer.TieredMixingRecipe;
import electrolyte.greate.content.kinetics.press.TieredMechanicalPressBlockEntity;
import electrolyte.greate.content.kinetics.press.TieredPressingRecipe;
import electrolyte.greate.content.kinetics.saw.TieredSawBlockEntity;
import electrolyte.greate.content.kinetics.saw.TieredSawingRecipe;
import electrolyte.greate.content.kinetics.simpleRelays.TieredBracketedKineticBlockEntity;
import electrolyte.greate.content.kinetics.simpleRelays.TieredShaftBlock;
import electrolyte.greate.foundation.recipe.GreateCrushingRecipes;
import electrolyte.greate.infrastructure.config.GreateConfig;
import electrolyte.greate.foundation.recipe.GreateMillingRecipes;
import electrolyte.greate.foundation.recipe.GreatePressingRecipes;
import electrolyte.greate.foundation.recipe.GreateRecipeRemoval;
import electrolyte.greate.foundation.recipe.GreateSawingRecipes;
import electrolyte.greate.registry.Belts;
import electrolyte.greate.registry.Cogwheels;
import electrolyte.greate.registry.CrushingWheels;
import electrolyte.greate.registry.EncasedFans;
import electrolyte.greate.registry.Gearboxes;
import electrolyte.greate.registry.GreateBlockEntityTypes;
import electrolyte.greate.registry.GreateRecipeTypes;
import electrolyte.greate.registry.GreateTiers;
import electrolyte.greate.registry.MechanicalMixers;
import electrolyte.greate.registry.MechanicalPresses;
import electrolyte.greate.registry.MechanicalPumps;
import electrolyte.greate.registry.Millstones;
import electrolyte.greate.registry.Saws;
import electrolyte.greate.registry.Shafts;
import electrolyte.greate.registry.VerticalGearboxes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

@GameTestHolder(Greate.MOD_ID)
@PrefixGameTestTemplate(false)
public final class GreateGameTests {

    private static final String TEMPLATE = "empty";

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new GameTestAssertException(message);
        }
    }

    @GameTest(template = TEMPLATE, batch = "greate_registration", timeoutTicks = 100)
    public static void registrationsAreComplete(GameTestHelper helper) {
        int tiers = GreateTiers.TIER_COUNT;
        check(Shafts.ALL.size() == tiers, "expected " + tiers + " shafts, got " + Shafts.ALL.size());
        check(Cogwheels.SMALL.size() == tiers, "expected " + tiers + " small cogwheels, got " + Cogwheels.SMALL.size());
        check(Cogwheels.LARGE.size() == tiers, "expected " + tiers + " large cogwheels, got " + Cogwheels.LARGE.size());
        check(Millstones.ALL.size() == tiers, "expected " + tiers + " millstones, got " + Millstones.ALL.size());
        check(MechanicalPresses.ALL.size() == tiers,
                "expected " + tiers + " mechanical presses, got " + MechanicalPresses.ALL.size());
        check(Gearboxes.ALL.size() == tiers,
                "expected " + tiers + " gearboxes, got " + Gearboxes.ALL.size());
        check(MechanicalMixers.ALL.size() == tiers,
                "expected " + tiers + " mechanical mixers, got " + MechanicalMixers.ALL.size());
        check(MechanicalPumps.ALL.size() == tiers,
                "expected " + tiers + " mechanical pumps, got " + MechanicalPumps.ALL.size());
        check(CrushingWheels.WHEELS.size() == tiers,
                "expected " + tiers + " crushing wheels, got " + CrushingWheels.WHEELS.size());
        check(CrushingWheels.CONTROLLERS.size() == tiers,
                "expected " + tiers + " crushing wheel controllers, got "
                        + CrushingWheels.CONTROLLERS.size());
        check(EncasedFans.ALL.size() == tiers,
                "expected " + tiers + " encased fans, got " + EncasedFans.ALL.size());
        check(Saws.ALL.size() == tiers,
                "expected " + tiers + " mechanical saws, got " + Saws.ALL.size());
        check(VerticalGearboxes.ALL.size() == tiers,
                "expected " + tiers + " vertical gearbox items, got " + VerticalGearboxes.ALL.size());
        check(Belts.ALL.size() == tiers,
                "expected " + tiers + " belts, got " + Belts.ALL.size());
        check(Belts.CONNECTOR_ITEMS.size() == 5,
                "expected 5 belt connector items, got " + Belts.CONNECTOR_ITEMS.size());
        check(Belts.BELT_CONNECTORS.size() == 5,
                "expected 5 belt connector prefix mappings, got " + Belts.BELT_CONNECTORS.size());

        long blocks = BuiltInRegistries.BLOCK.keySet().stream()
                .filter(id -> id.getNamespace().equals(Greate.MOD_ID)).count();
        long items = BuiltInRegistries.ITEM.keySet().stream()
                .filter(id -> id.getNamespace().equals(Greate.MOD_ID)).count();

        check(blocks == 130, "expected 130 greate blocks, got " + blocks);
        check(items == 125, "expected 125 greate items, got " + items);

        for (String name : new String[]{"shaft", "cogwheel", "large_cogwheel", "belt_connector"}) {
            TagPrefix prefix = TagPrefix.get(name);
            check(prefix != null, "TagPrefix gtceu:" + name + " missing");
            check(prefix.hasItemTable(), "TagPrefix gtceu:" + name + " has no itemTable");
        }

        check(GreateBlockEntityTypes.TIERED_BRACKETED_KINETIC.get() != null, "TIERED_BRACKETED_KINETIC missing");
        check(GreateBlockEntityTypes.TIERED_MILLSTONE.get() != null, "TIERED_MILLSTONE missing");
        check(GreateBlockEntityTypes.TIERED_MECHANICAL_PRESS.get() != null, "TIERED_MECHANICAL_PRESS missing");
        check(GreateBlockEntityTypes.TIERED_GEARBOX.get() != null, "TIERED_GEARBOX missing");
        check(GreateBlockEntityTypes.TIERED_MECHANICAL_MIXER.get() != null, "TIERED_MECHANICAL_MIXER missing");
        check(GreateBlockEntityTypes.TIERED_MECHANICAL_PUMP.get() != null, "TIERED_MECHANICAL_PUMP missing");
        check(GreateBlockEntityTypes.TIERED_CRUSHING_WHEEL.get() != null, "TIERED_CRUSHING_WHEEL missing");
        check(GreateBlockEntityTypes.TIERED_CRUSHING_WHEEL_CONTROLLER.get() != null,
                "TIERED_CRUSHING_WHEEL_CONTROLLER missing");
        check(GreateBlockEntityTypes.TIERED_ENCASED_FAN.get() != null, "TIERED_ENCASED_FAN missing");
        check(GreateBlockEntityTypes.TIERED_SAW.get() != null, "TIERED_SAW missing");
        check(GreateBlockEntityTypes.TIERED_BELT.get() != null, "TIERED_BELT missing");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_blockentity", timeoutTicks = 100)
    public static void millstoneBlockEntityCarriesTier(GameTestHelper helper) {
        int lastTier = GreateTiers.TIER_COUNT - 1;
        BlockState uls = Millstones.ALL.get(0).get().defaultBlockState();
        BlockState uhs = Millstones.ALL.get(lastTier).get().defaultBlockState();

        BlockPos ulsPos = new BlockPos(1, 1, 1);
        BlockPos uhsPos = new BlockPos(1, 1, 2);
        helper.setBlock(ulsPos, uls);
        helper.setBlock(uhsPos, uhs);

        BlockEntity ulsBe = helper.getLevel().getBlockEntity(helper.absolutePos(ulsPos));
        check(ulsBe instanceof TieredMillstoneBlockEntity, "ULV millstone BE type wrong: " + ulsBe);
        check(((TieredMillstoneBlockEntity) ulsBe).getTier() == 0, "ULV millstone tier should be 0");
        check(((TieredMillstoneBlockEntity) ulsBe).getBlockState().is(Millstones.ALL.get(0).get()),
                "ULV millstone block state mismatch");

        BlockEntity uhsBe = helper.getLevel().getBlockEntity(helper.absolutePos(uhsPos));
        check(uhsBe instanceof TieredMillstoneBlockEntity, "UHV millstone BE type wrong: " + uhsBe);
        check(((TieredMillstoneBlockEntity) uhsBe).getTier() == lastTier,
                "UHV millstone tier should be " + lastTier);
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_recipe_tier", timeoutTicks = 100)
    public static void millingRecipeTierIsEnforced(GameTestHelper helper) {
        ItemStack zincIngot = new ItemStack(AllItems.ZINC_INGOT.get());

        GreateMillingRecipes.Resolved atUls = GreateMillingRecipes.find(helper.getLevel(), zincIngot, 0);
        check(atUls == null, "ULV millstone must refuse a tier-2 recipe, but resolved: " + atUls);

        GreateMillingRecipes.Resolved atMv = GreateMillingRecipes.find(helper.getLevel(), zincIngot, 2);
        check(atMv != null, "MV millstone should process the tier-2 zinc recipe");
        check(atMv.recipeTier() == 2, "expected recipe tier 2, got " + atMv.recipeTier());
        check(atMv.results().size() == 1, "expected 1 result, got " + atMv.results().size());

        GreateMillingRecipes.Resolved atUhs = GreateMillingRecipes.find(helper.getLevel(), zincIngot,
                GreateTiers.TIER_COUNT - 1);
        check(atUhs != null, "UHV millstone should process the tier-2 zinc recipe");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_recipe_tier", timeoutTicks = 100)
    public static void millingRecipeCircuitIsEnforced(GameTestHelper helper) {
        ItemStack steelMillstone = new ItemStack(Millstones.ALL.get(1).get().asItem());

        var millingRecipes = helper.getLevel().getRecipeManager()
                .getAllRecipesFor(GreateRecipeTypes.millingType());
        check(millingRecipes.stream().anyMatch(holder -> holder.value().getCircuitNumber() == 3),
                "datagen circuit recipe is not loaded/parsed, greate_remake:milling recipes=" + millingRecipes.size());

        check(GreateMillingRecipes.circuitMatches(-1, -1), "no-circuit recipe should match no-circuit machine");
        check(GreateMillingRecipes.circuitMatches(-1, 5), "no-circuit recipe should match any machine");
        check(GreateMillingRecipes.circuitMatches(3, 3), "same circuit should match");
        check(!GreateMillingRecipes.circuitMatches(3, -1), "circuit recipe must not match circuit-less machine");
        check(!GreateMillingRecipes.circuitMatches(3, 2), "different circuits must not match");

        check(GreateMillingRecipes.find(helper.getLevel(), steelMillstone, 2, -1) == null,
                "circuit-less millstone must refuse a circuit recipe");

        check(GreateMillingRecipes.find(helper.getLevel(), steelMillstone, 2, 2) == null,
                "wrong circuit number must be refused");

        GreateMillingRecipes.Resolved resolved = GreateMillingRecipes.find(helper.getLevel(), steelMillstone, 2, 3);
        check(resolved != null, "circuit=3 machine should resolve the circuit-3 steel recipe");
        check(resolved.circuitNumber() == 3, "expected circuit 3, got " + resolved.circuitNumber());
        check(resolved.recipeTier() == 2, "expected recipe tier 2, got " + resolved.recipeTier());

        check(GreateMillingRecipes.find(helper.getLevel(), steelMillstone, 1, 3) == null,
                "tier-1 machine must refuse a tier-2 recipe even with the right circuit");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_recipe_bridge", timeoutTicks = 100)
    public static void gtMaceratorRecipesAreBridged(GameTestHelper helper) {
        ItemStack crushedIron = ChemicalHelper.get(TagPrefix.crushed, GTMaterials.Iron);
        check(!crushedIron.isEmpty(), "GT item gtceu:crushed_iron_ore is missing");
        check(crushedIron.getItem() == ChemicalHelper.get(TagPrefix.crushed, GTMaterials.Iron).getItem(),
                "GT crushed iron lookup is unstable");

        GreateMillingRecipes.Resolved resolved = GreateMillingRecipes.find(helper.getLevel(), crushedIron,
                GreateTiers.TIER_COUNT - 1);
        check(resolved != null, "GT macerator bridge did not resolve crushed iron ore");
        check(!resolved.results().isEmpty(), "GT macerator bridge resolved an empty result list");
        check(resolved.duration() > 0, "GT macerator bridge resolved duration " + resolved.duration());
        check(resolved.circuitNumber() == -1, "unconditional GT macerator recipe should have no circuit, got "
                + resolved.circuitNumber());
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_blockentity", timeoutTicks = 100)
    public static void pressBlockEntityCarriesTier(GameTestHelper helper) {
        int lastTier = GreateTiers.TIER_COUNT - 1;
        BlockPos ulsPos = new BlockPos(1, 1, 1);
        BlockPos uhsPos = new BlockPos(1, 1, 2);
        helper.setBlock(ulsPos, MechanicalPresses.ALL.get(0).get().defaultBlockState());
        helper.setBlock(uhsPos, MechanicalPresses.ALL.get(lastTier).get().defaultBlockState());

        BlockEntity ulsBe = helper.getLevel().getBlockEntity(helper.absolutePos(ulsPos));
        check(ulsBe instanceof TieredMechanicalPressBlockEntity, "ULV press BE type wrong: " + ulsBe);
        check(((TieredMechanicalPressBlockEntity) ulsBe).getTier() == 0, "ULV press tier should be 0");

        BlockEntity uhsBe = helper.getLevel().getBlockEntity(helper.absolutePos(uhsPos));
        check(uhsBe instanceof TieredMechanicalPressBlockEntity, "UHV press BE type wrong: " + uhsBe);
        check(((TieredMechanicalPressBlockEntity) uhsBe).getTier() == lastTier,
                "UHV press tier should be " + lastTier);
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_recipe_tier", timeoutTicks = 100)
    public static void pressingRecipeTierIsEnforced(GameTestHelper helper) {
        ItemStack steelIngot = ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Steel);
        check(!steelIngot.isEmpty(), "GT item gtceu:steel_ingot is missing");

        var pressingRecipes = helper.getLevel().getRecipeManager()
                .getAllRecipesFor(GreateRecipeTypes.pressingType());
        check(pressingRecipes.stream().anyMatch(holder -> holder.value().getRecipeTier() == 1),
                "datagen tier-1 pressing recipe is not loaded, greate_remake:pressing recipes=" + pressingRecipes.size());

        check(GreatePressingRecipes.find(helper.getLevel(), steelIngot, 0) == null,
                "ULV press must refuse a tier-1 recipe");

        var atLv = GreatePressingRecipes.find(helper.getLevel(), steelIngot, 1);
        check(atLv != null, "LV press should resolve the tier-1 steel plate recipe");
        check(atLv.value() instanceof TieredPressingRecipe,
                "resolved recipe should be a TieredPressingRecipe, got " + atLv.value().getClass());
        check(((TieredPressingRecipe) atLv.value()).getRecipeTier() == 1,
                "expected recipe tier 1, got " + ((TieredPressingRecipe) atLv.value()).getRecipeTier());

        check(GreatePressingRecipes.find(helper.getLevel(), steelIngot, GreateTiers.TIER_COUNT - 1) != null,
                "UHV press should process the tier-1 steel plate recipe");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_blockentity", timeoutTicks = 100)
    public static void gearboxBlockEntityCarriesTier(GameTestHelper helper) {
        int lastTier = GreateTiers.TIER_COUNT - 1;
        BlockPos ulsPos = new BlockPos(1, 1, 1);
        BlockPos uhsPos = new BlockPos(1, 1, 2);

        BlockState uls = Gearboxes.ALL.get(0).get().defaultBlockState();
        BlockState uhs = Gearboxes.ALL.get(lastTier).get().defaultBlockState();
        helper.setBlock(ulsPos, uls);
        helper.setBlock(uhsPos, uhs);

        BlockEntity ulsBe = helper.getLevel().getBlockEntity(helper.absolutePos(ulsPos));
        check(ulsBe instanceof TieredGearboxBlockEntity, "ULV gearbox BE type wrong: " + ulsBe);
        check(((TieredGearboxBlockEntity) ulsBe).getTier() == 0, "ULV gearbox tier should be 0");

        BlockEntity uhsBe = helper.getLevel().getBlockEntity(helper.absolutePos(uhsPos));
        check(uhsBe instanceof TieredGearboxBlockEntity, "UHV gearbox BE type wrong: " + uhsBe);
        check(((TieredGearboxBlockEntity) uhsBe).getTier() == lastTier,
                "UHV gearbox tier should be " + lastTier);

        check(uls.getValue(BlockStateProperties.AXIS) != null, "gearbox state must carry AXIS");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_blockentity", timeoutTicks = 100)
    public static void verticalGearboxItemMatchesItsTier(GameTestHelper helper) {
        int lastTier = GreateTiers.TIER_COUNT - 1;
        for (int tier = 0; tier <= lastTier; tier++) {
            TieredGearboxBlock block = Gearboxes.ALL.get(tier).get();
            TieredVerticalGearboxItem item = VerticalGearboxes.ALL.get(tier).get();
            check(item.getBlock() == block,
                    "tier " + tier + " vertical gearbox points at " + item.getBlock() + " instead of " + block);
            check(item.getTier() == tier, "tier " + tier + " vertical gearbox reports tier " + item.getTier());
            check(block.hasVerticalItem(), "tier " + tier + " gearbox has no vertical item bound");
        }

        TieredGearboxBlock block = Gearboxes.ALL.get(1).get();
        BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));

        BlockState verticalAxis = block.defaultBlockState().setValue(BlockStateProperties.AXIS, Direction.Axis.Y);
        check(block.getCloneItemStack(verticalAxis, null, helper.getLevel(), pos, null).is(block.asItem()),
                "a vertical-axis gearbox should clone as the block item");

        BlockState horizontalAxis = block.defaultBlockState().setValue(BlockStateProperties.AXIS, Direction.Axis.X);
        ItemStack cloned = block.getCloneItemStack(horizontalAxis, null, helper.getLevel(), pos, null);
        check(cloned.is(VerticalGearboxes.byTier(1)),
                "a horizontal-axis gearbox should clone as the tier-1 vertical gearbox item, got " + cloned);
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_blockentity", timeoutTicks = 100)
    public static void mixerBlockEntityCarriesTier(GameTestHelper helper) {
        int lastTier = GreateTiers.TIER_COUNT - 1;
        BlockPos ulsPos = new BlockPos(1, 1, 1);
        BlockPos uhsPos = new BlockPos(1, 1, 2);
        helper.setBlock(ulsPos, MechanicalMixers.ALL.get(0).get().defaultBlockState());
        helper.setBlock(uhsPos, MechanicalMixers.ALL.get(lastTier).get().defaultBlockState());

        BlockEntity ulsBe = helper.getLevel().getBlockEntity(helper.absolutePos(ulsPos));
        check(ulsBe instanceof TieredMechanicalMixerBlockEntity, "ULV mixer BE type wrong: " + ulsBe);
        check(((TieredMechanicalMixerBlockEntity) ulsBe).getTier() == 0, "ULV mixer tier should be 0");

        BlockEntity uhsBe = helper.getLevel().getBlockEntity(helper.absolutePos(uhsPos));
        check(uhsBe instanceof TieredMechanicalMixerBlockEntity, "UHV mixer BE type wrong: " + uhsBe);
        check(((TieredMechanicalMixerBlockEntity) uhsBe).getTier() == lastTier,
                "UHV mixer tier should be " + lastTier);
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_recipe_tier", timeoutTicks = 100)
    public static void mixingRecipeTierIsEnforced(GameTestHelper helper) {
        var recipes = helper.getLevel().getRecipeManager().getAllRecipesFor(GreateRecipeTypes.mixingType());
        check(!recipes.isEmpty(), "greate_remake:mixing recipes are not loaded");

        var tiered = recipes.stream()
                .map(RecipeHolder::value)
                .filter(TieredMixingRecipe.class::isInstance)
                .map(TieredMixingRecipe.class::cast)
                .filter(r -> r.getRecipeTier() == 1)
                .findFirst()
                .orElse(null);
        check(tiered != null, "expected a tier-1 greate_remake:mixing recipe, got " + recipes.size() + " recipes");

        BlockPos lowPos = new BlockPos(1, 1, 1);
        BlockPos highPos = new BlockPos(1, 1, 2);
        helper.setBlock(lowPos, MechanicalMixers.ALL.get(0).get().defaultBlockState());
        helper.setBlock(highPos, MechanicalMixers.ALL.get(4).get().defaultBlockState());

        TieredMechanicalMixerBlockEntity low =
                (TieredMechanicalMixerBlockEntity) helper.getLevel().getBlockEntity(helper.absolutePos(lowPos));
        TieredMechanicalMixerBlockEntity high =
                (TieredMechanicalMixerBlockEntity) helper.getLevel().getBlockEntity(helper.absolutePos(highPos));
        check(low != null && high != null, "mixer BE missing");

        check(!low.allowsInBasin(new RecipeHolder<>(Greate.id("test_mixing"), tiered)),
                "ULV mixer must reject a tier-1 mixing recipe");
        check(high.allowsInBasin(new RecipeHolder<>(Greate.id("test_mixing"), tiered)),
                "MV mixer should accept a tier-1 mixing recipe");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_blockentity", timeoutTicks = 100)
    public static void pumpBlockEntityCarriesTier(GameTestHelper helper) {
        int lastTier = GreateTiers.TIER_COUNT - 1;
        BlockPos ulsPos = new BlockPos(1, 1, 1);
        BlockPos uhsPos = new BlockPos(1, 1, 2);
        helper.setBlock(ulsPos, MechanicalPumps.ALL.get(0).get().defaultBlockState());
        helper.setBlock(uhsPos, MechanicalPumps.ALL.get(lastTier).get().defaultBlockState());

        BlockEntity ulsBe = helper.getLevel().getBlockEntity(helper.absolutePos(ulsPos));
        check(ulsBe instanceof TieredPumpBlockEntity, "ULV pump BE type wrong: " + ulsBe);
        check(((TieredPumpBlockEntity) ulsBe).getTier() == 0, "ULV pump tier should be 0");

        BlockEntity uhsBe = helper.getLevel().getBlockEntity(helper.absolutePos(uhsPos));
        check(uhsBe instanceof TieredPumpBlockEntity, "UHV pump BE type wrong: " + uhsBe);
        check(((TieredPumpBlockEntity) uhsBe).getTier() == lastTier, "UHV pump tier should be " + lastTier);
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_blockentity", timeoutTicks = 100)
    public static void crushingWheelBlockEntityCarriesTier(GameTestHelper helper) {
        int lastTier = GreateTiers.TIER_COUNT - 1;
        BlockPos ulsPos = new BlockPos(1, 1, 1);
        BlockPos ulsCtrlPos = new BlockPos(1, 1, 2);
        BlockPos uhsPos = new BlockPos(1, 1, 3);
        BlockPos uhsCtrlPos = new BlockPos(1, 1, 4);

        helper.setBlock(ulsPos, CrushingWheels.WHEELS.get(0).get().defaultBlockState());
        helper.setBlock(ulsCtrlPos, CrushingWheels.CONTROLLERS.get(0).get().defaultBlockState());
        helper.setBlock(uhsPos, CrushingWheels.WHEELS.get(lastTier).get().defaultBlockState());
        helper.setBlock(uhsCtrlPos, CrushingWheels.CONTROLLERS.get(lastTier).get().defaultBlockState());

        BlockEntity ulsBe = helper.getLevel().getBlockEntity(helper.absolutePos(ulsPos));
        check(ulsBe instanceof TieredCrushingWheelBlockEntity, "ULV crushing wheel BE type wrong: " + ulsBe);
        check(((TieredCrushingWheelBlockEntity) ulsBe).getTier() == 0, "ULV crushing wheel tier should be 0");

        BlockEntity ulsCtrlBe = helper.getLevel().getBlockEntity(helper.absolutePos(ulsCtrlPos));
        check(ulsCtrlBe instanceof TieredCrushingWheelControllerBlockEntity,
                "ULV crushing wheel controller BE type wrong: " + ulsCtrlBe);
        check(((TieredCrushingWheelControllerBlockEntity) ulsCtrlBe).getTier() == 0,
                "ULV crushing wheel controller tier should be 0");

        BlockEntity uhsBe = helper.getLevel().getBlockEntity(helper.absolutePos(uhsPos));
        check(uhsBe instanceof TieredCrushingWheelBlockEntity, "UHV crushing wheel BE type wrong: " + uhsBe);
        check(((TieredCrushingWheelBlockEntity) uhsBe).getTier() == lastTier,
                "UHV crushing wheel tier should be " + lastTier);

        BlockEntity uhsCtrlBe = helper.getLevel().getBlockEntity(helper.absolutePos(uhsCtrlPos));
        check(uhsCtrlBe instanceof TieredCrushingWheelControllerBlockEntity,
                "UHV crushing wheel controller BE type wrong: " + uhsCtrlBe);
        check(((TieredCrushingWheelControllerBlockEntity) uhsCtrlBe).getTier() == lastTier,
                "UHV crushing wheel controller tier should be " + lastTier);
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_recipe_tier", timeoutTicks = 100)
    public static void crushingRecipeTierIsEnforced(GameTestHelper helper) {
        var recipes = helper.getLevel().getRecipeManager()
                .getAllRecipesFor(GreateRecipeTypes.crushingType());
        check(recipes.size() >= 3, "expected at least 3 greate_remake:crushing recipes, got " + recipes.size());

        ItemStack steelIngot = ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Steel);
        check(!steelIngot.isEmpty(), "GT item gtceu:steel_ingot is missing");

        GreateCrushingRecipes.Resolution atUls = GreateCrushingRecipes.resolve(helper.getLevel(), steelIngot, 0);
        check(!atUls.found(), "ULV crushing wheel must not resolve the tier-1 steel recipe");
        check(atUls.blocked(), "ULV crushing wheel should report the match as tier-blocked");

        GreateCrushingRecipes.Resolution atLv = GreateCrushingRecipes.resolve(helper.getLevel(), steelIngot, 1);
        check(atLv.found(), "LV crushing wheel should resolve the tier-1 steel recipe");
        check(atLv.recipe().value().getRecipeTier() == 1,
                "expected recipe tier 1, got " + atLv.recipe().value().getRecipeTier());
        check(atLv.recipe().value().getRollableResults().size() == 1,
                "expected 1 result, got " + atLv.recipe().value().getRollableResults().size());

        GreateCrushingRecipes.Resolution unknown = GreateCrushingRecipes.resolve(helper.getLevel(),
                new ItemStack(net.minecraft.world.item.Items.DIRT), GreateTiers.TIER_COUNT - 1);
        check(!unknown.found() && !unknown.blocked(),
                "an unmatched input must fall through to Create recipes (neither found nor blocked)");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_blockentity", timeoutTicks = 100)
    public static void fanBlockEntityCarriesTier(GameTestHelper helper) {
        int lastTier = GreateTiers.TIER_COUNT - 1;
        BlockPos ulsPos = new BlockPos(1, 1, 1);
        BlockPos uhsPos = new BlockPos(1, 1, 2);
        helper.setBlock(ulsPos, EncasedFans.ALL.get(0).get().defaultBlockState());
        helper.setBlock(uhsPos, EncasedFans.ALL.get(lastTier).get().defaultBlockState());

        BlockEntity ulsBe = helper.getLevel().getBlockEntity(helper.absolutePos(ulsPos));
        check(ulsBe instanceof TieredEncasedFanBlockEntity, "ULV fan BE type wrong: " + ulsBe);
        check(((TieredEncasedFanBlockEntity) ulsBe).getTier() == 0, "ULV fan tier should be 0");

        BlockEntity uhsBe = helper.getLevel().getBlockEntity(helper.absolutePos(uhsPos));
        check(uhsBe instanceof TieredEncasedFanBlockEntity, "UHV fan BE type wrong: " + uhsBe);
        check(((TieredEncasedFanBlockEntity) uhsBe).getTier() == lastTier,
                "UHV fan tier should be " + lastTier);
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_processing", timeoutTicks = 600)
    public static void millstoneProcessesTieredRecipe(GameTestHelper helper) {
        BlockPos motorPos = new BlockPos(1, 1, 1);
        BlockPos millPos = new BlockPos(1, 2, 1);
        placePoweredMillstone(helper, motorPos, millPos, 2);

        TieredMillstoneBlockEntity millstone = (TieredMillstoneBlockEntity) helper.getLevel()
                .getBlockEntity(helper.absolutePos(millPos));
        check(millstone != null, "millstone BE missing");
        millstone.inputInv.setStackInSlot(0, new ItemStack(AllItems.ZINC_INGOT.get()));

        helper.runAtTickTime(360, () -> {
            check(millstone.getSpeed() != 0, "millstone is not rotating (speed=0)");
            check(millstone.inputInv.getStackInSlot(0).isEmpty(),
                    "zinc ingot was not consumed, input=" + millstone.inputInv.getStackInSlot(0));
            check(countOutput(millstone, "gtceu:zinc_dust") > 0,
                    "expected gtceu:zinc_dust in output, got " + describeOutput(millstone));
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE, batch = "greate_processing", timeoutTicks = 600)
    public static void millstoneRefusesRecipeBelowTier(GameTestHelper helper) {
        BlockPos motorPos = new BlockPos(1, 1, 1);
        BlockPos millPos = new BlockPos(1, 2, 1);
        placePoweredMillstone(helper, motorPos, millPos, 0);

        TieredMillstoneBlockEntity millstone = (TieredMillstoneBlockEntity) helper.getLevel()
                .getBlockEntity(helper.absolutePos(millPos));
        check(millstone != null, "millstone BE missing");
        millstone.inputInv.setStackInSlot(0, new ItemStack(AllItems.ZINC_INGOT.get()));

        helper.runAtTickTime(360, () -> {
            check(millstone.getSpeed() != 0, "millstone is not rotating (speed=0)");
            check(millstone.inputInv.getStackInSlot(0).is(AllItems.ZINC_INGOT.get()),
                    "tier-0 millstone must not consume the tier-2 recipe input");
            check(millstone.outputInv.getStackInSlot(0).isEmpty(),
                    "tier-0 millstone must not produce output, got " + describeOutput(millstone));
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE, batch = "greate_blockentity", timeoutTicks = 100)
    public static void sawBlockEntityCarriesTier(GameTestHelper helper) {
        int lastTier = GreateTiers.TIER_COUNT - 1;
        BlockPos ulsPos = new BlockPos(1, 1, 1);
        BlockPos uhsPos = new BlockPos(1, 1, 2);
        helper.setBlock(ulsPos, Saws.ALL.get(0).get().defaultBlockState());
        helper.setBlock(uhsPos, Saws.ALL.get(lastTier).get().defaultBlockState());

        BlockEntity ulsBe = helper.getLevel().getBlockEntity(helper.absolutePos(ulsPos));
        check(ulsBe instanceof TieredSawBlockEntity, "ULV saw BE type wrong: " + ulsBe);
        check(((TieredSawBlockEntity) ulsBe).getTier() == 0, "ULV saw tier should be 0");

        BlockEntity uhsBe = helper.getLevel().getBlockEntity(helper.absolutePos(uhsPos));
        check(uhsBe instanceof TieredSawBlockEntity, "UHV saw BE type wrong: " + uhsBe);
        check(((TieredSawBlockEntity) uhsBe).getTier() == lastTier,
                "UHV saw tier should be " + lastTier);
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_recipe_tier", timeoutTicks = 100)
    public static void sawingRecipeTierIsEnforced(GameTestHelper helper) {
        var recipes = helper.getLevel().getRecipeManager().getAllRecipesFor(GreateRecipeTypes.sawingType());
        check(recipes.stream().anyMatch(holder -> holder.value().getRecipeTier() == 1),
                "datagen tier-1 sawing recipe is not loaded, greate_remake:sawing recipes=" + recipes.size());

        ItemStack steelIngot = ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Steel);
        check(!steelIngot.isEmpty(), "GT item gtceu:steel_ingot is missing");

        GreateSawingRecipes.Resolution atUls = GreateSawingRecipes.resolve(helper.getLevel(), steelIngot, 0);
        check(!atUls.found(), "ULV saw must not resolve the tier-1 steel rod recipe");
        check(atUls.blocked(), "ULV saw should report the match as tier-blocked");

        GreateSawingRecipes.Resolution atLv = GreateSawingRecipes.resolve(helper.getLevel(), steelIngot, 1);
        check(atLv.found(), "LV saw should resolve the tier-1 steel rod recipe");
        check(atLv.recipe().value().getRecipeTier() == 1,
                "expected recipe tier 1, got " + atLv.recipe().value().getRecipeTier());
        check(atLv.recipe().value().getRollableResults().size() == 1,
                "expected 1 result, got " + atLv.recipe().value().getRollableResults().size());

        GreateSawingRecipes.Resolution unknown = GreateSawingRecipes.resolve(helper.getLevel(),
                new ItemStack(net.minecraft.world.item.Items.DIRT), GreateTiers.TIER_COUNT - 1);
        check(!unknown.found() && !unknown.blocked(),
                "an unmatched input must fall through to Create recipes (neither found nor blocked)");

        check(GreateSawingRecipes.gate(helper.getLevel(), 0, steelIngot, stack -> true, List.of()).isEmpty(),
                "tier-0 saw gate must return no recipe for a tier-1 input");
        check(GreateSawingRecipes.gate(helper.getLevel(), 1, steelIngot, stack -> true, List.of()).size() == 1,
                "tier-1 saw gate should return exactly one tiered recipe");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_processing", timeoutTicks = 600)
    public static void sawProcessesTieredRecipe(GameTestHelper helper) {
        BlockPos sawPos = new BlockPos(1, 1, 1);
        TieredSawBlockEntity saw = placePoweredSaw(helper, new BlockPos(0, 1, 1), sawPos, 1);

        saw.inventory.setStackInSlot(0, ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Steel));

        helper.runAtTickTime(360, () -> {
            check(saw.getSpeed() != 0, "saw is not rotating (speed=0)");
            int rods = countItemEntities(helper, sawPos, "gtceu:steel_rod");
            check(rods >= 2, "expected at least 2 gtceu:steel_rod dropped by the saw, got " + rods);
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE, batch = "greate_processing", timeoutTicks = 600)
    public static void sawRefusesRecipeBelowTier(GameTestHelper helper) {
        BlockPos sawPos = new BlockPos(1, 1, 1);
        TieredSawBlockEntity saw = placePoweredSaw(helper, new BlockPos(0, 1, 1), sawPos, 2);

        saw.inventory.setStackInSlot(0, ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Titanium));

        helper.runAtTickTime(360, () -> {
            check(saw.getSpeed() != 0, "saw is not rotating (speed=0)");
            check(countItemEntities(helper, sawPos, "gtceu:titanium_rod") == 0,
                    "tier-2 saw must not produce titanium rods from a tier-4 recipe");
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE, batch = "greate_processing", timeoutTicks = 700)
    public static void sawProcessesGtCutterRecipe(GameTestHelper helper) {
        BlockPos sawPos = new BlockPos(1, 1, 1);
        TieredSawBlockEntity saw = placePoweredSaw(helper, new BlockPos(0, 1, 1), sawPos, 1);

        saw.inventory.setStackInSlot(0, ChemicalHelper.get(TagPrefix.rod, GTMaterials.Steel));

        for (int x = 0; x <= 4; x++) {
            for (int z = 0; z <= 2; z++) {
                helper.setBlock(new BlockPos(x, 0, z), net.minecraft.world.level.block.Blocks.SMOOTH_STONE);
            }
        }

        helper.runAtTickTime(360, () -> checkBoltsOrRetry(helper, saw, sawPos, 650));
    }

    private static void checkBoltsOrRetry(GameTestHelper helper, TieredSawBlockEntity saw, BlockPos sawPos,
                                          int nextTick) {
        check(saw.getSpeed() != 0, "saw is not rotating (speed=0)");
        int bolts = countItemEntities(helper, sawPos, "gtceu:steel_bolt");
        if (bolts >= 4) {
            helper.succeed();
            return;
        }
        if (nextTick > 650) {
            check(false, "expected at least 4 gtceu:steel_bolt from the GT cutter bridge, got " + bolts
                    + " (saw slot0=" + saw.inventory.getStackInSlot(0) + ", speed=" + saw.getSpeed() + ")");
            return;
        }
        helper.runAtTickTime(nextTick, () -> checkBoltsOrRetry(helper, saw, sawPos, 700));
    }

    private static TieredSawBlockEntity placePoweredSaw(GameTestHelper helper, BlockPos motorPos, BlockPos sawPos,
                                                       int tier) {
        BlockState motor = AllBlocks.CREATIVE_MOTOR.getDefaultState()
                .setValue(BlockStateProperties.FACING, Direction.EAST);
        helper.setBlock(motorPos, motor);
        helper.setBlock(sawPos, Saws.ALL.get(tier).get().defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.UP));
        return (TieredSawBlockEntity) helper.getLevel().getBlockEntity(helper.absolutePos(sawPos));
    }

    private static int countItemEntities(GameTestHelper helper, BlockPos relativeCenter, String itemId) {
        AABB area = AABB.encapsulatingFullBlocks(
                helper.absolutePos(relativeCenter).offset(-4, -3, -4),
                helper.absolutePos(relativeCenter).offset(4, 3, 4));
        int count = 0;
        for (ItemEntity entity : helper.getLevel().getEntitiesOfClass(ItemEntity.class, area)) {
            ItemStack stack = entity.getItem();
            if (!stack.isEmpty() && BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(itemId)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static void placePoweredMillstone(GameTestHelper helper, BlockPos motorPos, BlockPos millPos, int tier) {

        BlockState motor = AllBlocks.CREATIVE_MOTOR.getDefaultState()
                .setValue(BlockStateProperties.FACING, Direction.UP);
        helper.setBlock(motorPos, motor);
        helper.setBlock(millPos, Millstones.ALL.get(tier).get().defaultBlockState());
    }

    private static int countOutput(TieredMillstoneBlockEntity millstone, String itemId) {
        int count = 0;
        for (int i = 0; i < millstone.outputInv.getSlots(); i++) {
            ItemStack stack = millstone.outputInv.getStackInSlot(i);
            if (!stack.isEmpty() && BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(itemId)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static String describeOutput(TieredMillstoneBlockEntity millstone) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < millstone.outputInv.getSlots(); i++) {
            ItemStack stack = millstone.outputInv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                builder.append(BuiltInRegistries.ITEM.getKey(stack.getItem())).append('x').append(stack.getCount())
                        .append(' ');
            }
        }
        return builder.length() == 0 ? "<empty>" : builder.toString();
    }

    @GameTest(template = TEMPLATE, batch = "greate_belt_create", timeoutTicks = 200)
    public static void beltConnectorCreatesTieredChain(GameTestHelper helper) {

        BlockPos startRelative = new BlockPos(0, 1, 1);
        BlockPos endRelative = new BlockPos(8, 1, 1);
        BlockState shaft = Shafts.ALL.get(0).get().defaultBlockState()
                .setValue(BlockStateProperties.AXIS, Axis.Z);

        for (int x = 0; x <= 8; x++) {
            helper.setBlock(new BlockPos(x, 1, 1), net.minecraft.world.level.block.Blocks.AIR);
        }
        helper.setBlock(startRelative, shaft);
        helper.setBlock(endRelative, shaft);

        BlockPos start = helper.absolutePos(startRelative);
        BlockPos end = helper.absolutePos(endRelative);
        TieredBeltConnectorItem connector = Belts.CONNECTOR_ITEMS.get(0).get();

        check(connector.getMaxLength() == GreateConfig.beltLength(0),
                "belt connector max length should follow config");

        BlockState firstState = helper.getLevel().getBlockState(start);
        BlockState secondState = helper.getLevel().getBlockState(end);
        check(firstState.getBlock() instanceof TieredShaftBlock,
                "start block should be a tiered shaft, got " + firstState);
        check(secondState.getBlock() instanceof TieredShaftBlock,
                "end block should be a tiered shaft, got " + secondState);
        check(firstState.getValue(BlockStateProperties.AXIS) == secondState.getValue(BlockStateProperties.AXIS),
                "both shafts should share the same axis: " + firstState + " vs " + secondState);
        check(helper.getLevel().getBlockEntity(start) instanceof TieredBracketedKineticBlockEntity,
                "start BE should be TieredBracketedKineticBlockEntity, got "
                        + helper.getLevel().getBlockEntity(start));
        check(helper.getLevel().getBlockEntity(end) instanceof TieredBracketedKineticBlockEntity,
                "end BE should be TieredBracketedKineticBlockEntity, got "
                        + helper.getLevel().getBlockEntity(end));
        check(connector.getValidShafts().stream().anyMatch(entry -> entry.get() == firstState.getBlock()),
                "connector valid shafts should contain start block: "
                        + connector.getValidShafts().stream().map(entry -> entry.getId()).toList()
                        + " vs " + firstState);
        check(start.distSqr(end) <= (long) connector.getMaxLength() * connector.getMaxLength(),
                "start/end distance " + Math.sqrt(start.distSqr(end)) + " exceeds max length "
                        + connector.getMaxLength());

        String failureReason = TieredBeltConnectorItem.connectFailureReason(
                helper.getLevel(), start, end, new ItemStack(connector));
        check(failureReason == null, "connector should accept two matching tiered shafts: " + failureReason);

        connector.createBelts(helper.getLevel(), start, end);

        for (int x = 0; x <= 8; x++) {
            BlockState state = helper.getLevel().getBlockState(helper.absolutePos(new BlockPos(x, 1, 1)));
            check(state.getBlock() instanceof TieredBeltBlock,
                    "expected tiered belt at x=" + x + ", got " + state);
        }

        BlockEntity middleBe = helper.getLevel().getBlockEntity(helper.absolutePos(new BlockPos(4, 1, 1)));
        check(middleBe instanceof TieredBeltBlockEntity,
                "middle belt BE should be TieredBeltBlockEntity, got " + middleBe);

        helper.runAtTickTime(5, () -> {
            BlockEntity controllerBe = helper.getLevel().getBlockEntity(helper.absolutePos(new BlockPos(0, 1, 1)));
            check(controllerBe instanceof TieredBeltBlockEntity,
                    "belt controller BE type wrong: " + controllerBe);
            TieredBeltBlockEntity controller = (TieredBeltBlockEntity) controllerBe;
            check(controller.isController(), "first belt segment should be controller");
            check(controller.beltLengthForDebug() == 9,
                    "expected belt length 9, got " + controller.beltLengthForDebug());
            check(controller.getTier() == 0, "expected tier 0, got " + controller.getTier());
            check(controller.hasPulley(), "START segment with shaft should report a pulley");
            check(controller.hasItemHandler(), "belt controller should initialize its item handler");
            check(controller.getBeltItemHandler() != null, "belt item handler capability should be available");

            BlockState middleState = helper.getLevel().getBlockState(helper.absolutePos(new BlockPos(4, 1, 1)));
            check(middleState.getBlock() instanceof TieredBeltBlock,
                    "middle belt block should still exist after ticking");
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE, batch = "greate_belt_reject", timeoutTicks = 100)
    public static void beltConnectorRejectsMismatchedTier(GameTestHelper helper) {
        BlockPos start = helper.absolutePos(new BlockPos(0, 1, 1));
        BlockPos end = helper.absolutePos(new BlockPos(8, 1, 1));
        helper.setBlock(new BlockPos(0, 1, 1), Shafts.ALL.get(0).get().defaultBlockState()
                .setValue(BlockStateProperties.AXIS, Axis.Z));
        helper.setBlock(new BlockPos(8, 1, 1), Shafts.ALL.get(1).get().defaultBlockState()
                .setValue(BlockStateProperties.AXIS, Axis.Z));

        TieredBeltConnectorItem connector = Belts.CONNECTOR_ITEMS.get(0).get();
        check(!TieredBeltConnectorItem.canConnect(helper.getLevel(), start, end, new ItemStack(connector)),
                "connector must reject two shafts of different tier");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_gt_crushing", timeoutTicks = 100)
    public static void gtMaceratorBridgeFeedsCrushingWheel(GameTestHelper helper) {
        ItemStack purifiedIron = ChemicalHelper.get(TagPrefix.crushedPurified, GTMaterials.Iron);
        check(!purifiedIron.isEmpty(), "GT item gtceu:purified_iron_ore is missing");

        GreateCrushingRecipes.Resolution resolution =
                GreateCrushingRecipes.resolve(helper.getLevel(), purifiedIron, 1);
        check(resolution.found(), "GT macerator bridge did not feed the crushing wheel");
        TieredCrushingRecipe recipe = resolution.recipe().value();
        check(recipe.getRecipeTier() <= 1, "bridged crushing recipe tier too high: " + recipe.getRecipeTier());
        check(recipe.getCircuitNumber() == -1, "bridged crushing recipe must not require a circuit");
        check(!recipe.getRollableResults().isEmpty(), "bridged crushing recipe has no results");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_gt_sawing", timeoutTicks = 100)
    public static void gtCutterBridgeFeedsSaw(GameTestHelper helper) {
        ItemStack steelRod = ChemicalHelper.get(TagPrefix.rod, GTMaterials.Steel);
        check(!steelRod.isEmpty(), "GT item gtceu:steel_rod is missing");

        GreateSawingRecipes.Resolution resolution = GreateSawingRecipes.resolve(helper.getLevel(), steelRod, 1);
        check(resolution.found(), "GT cutter bridge did not feed the saw");
        TieredSawingRecipe recipe = resolution.recipe().value();
        check(recipe.getRecipeTier() <= 1, "bridged sawing recipe tier too high: " + recipe.getRecipeTier());
        check(recipe.getCircuitNumber() == -1, "bridged sawing recipe must not require a circuit");
        check(!recipe.getRollableResults().isEmpty(), "bridged sawing recipe has no results");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_gt_pressing", timeoutTicks = 100)
    public static void gtBenderBridgeFeedsPressOnMatchingCircuit(GameTestHelper helper) {
        ItemStack tinIngot = ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Tin);
        check(!tinIngot.isEmpty(), "GT item gtceu:tin_ingot is missing");

        RecipeHolder<PressingRecipe> onCircuitOne = GreatePressingRecipes.find(
                helper.getLevel(), tinIngot, 1, 1);
        check(onCircuitOne != null, "GT bender bridge did not resolve tin ingot with circuit 1");
        check(onCircuitOne.value() instanceof TieredPressingRecipe,
                "bridged pressing recipe should be a TieredPressingRecipe, got "
                        + onCircuitOne.value().getClass().getName());
        check(((TieredPressingRecipe) onCircuitOne.value()).getRecipeTier() <= 1,
                "bridged pressing recipe tier too high: "
                        + ((TieredPressingRecipe) onCircuitOne.value()).getRecipeTier());

        check(GreatePressingRecipes.find(helper.getLevel(), tinIngot, 1, 0) == null,
                "press must not run the circuit-1 bender recipe while set to circuit 0");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_crafting", timeoutTicks = 100)
    public static void craftingRecipesAreRegistered(GameTestHelper helper) {
        for (String path : new String[]{
                "crafting/andesite_alloy_shaft",
                "crafting/steel_cogwheel",
                "crafting/steel_gearbox",
                "crafting/steel_mechanical_press",
                "crafting/aluminium_mechanical_pump",
                "crafting/neutronium_mechanical_saw",
                "crafting/rubber_belt_connector"}) {
            var holder = helper.getLevel().getRecipeManager().byKey(Greate.id(path));
            check(holder.isPresent(), "missing crafting recipe " + path);
            check(holder.get().value().getType() == net.minecraft.world.item.crafting.RecipeType.CRAFTING,
                    path + " is not a crafting recipe");
        }

        check(GreateRecipeRemoval.count() >= 10, "recipe removal list looks empty");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_fan_gate", timeoutTicks = 40)
    public static void fanTierGatePolicy(GameTestHelper helper) {

        ItemStack neutroniumDust = ChemicalHelper.get(TagPrefix.dust, GTMaterials.Neutronium);
        check(!neutroniumDust.isEmpty(), "gtceu neutronium dust is missing");
        check(!TieredFanProcessing.canProcess(neutroniumDust, 8),
                "UHV fan must not process neutronium (tier 9); gate is off");
        check(TieredFanProcessing.canProcess(neutroniumDust, 9),
                "UHV fan must process neutronium (tier 9)");

        ItemStack steelDust = ChemicalHelper.get(TagPrefix.dust, GTMaterials.Steel);
        check(!steelDust.isEmpty(), "gtceu steel dust is missing");
        check(!TieredFanProcessing.canProcess(steelDust, 0),
                "ULV fan must not process steel (tier 1)");
        check(TieredFanProcessing.canProcess(steelDust, 1),
                "LV fan must process steel (tier 1)");

        check(TieredFanProcessing.itemTier(
                ChemicalHelper.get(TagPrefix.dust, GTMaterials.TungstenSteel)) == 5,
                "tungsten steel must resolve to tier 5, not steel");
        check(TieredFanProcessing.itemTier(
                ChemicalHelper.get(TagPrefix.dust, GTMaterials.StainlessSteel)) == 3,
                "stainless steel must resolve to tier 3, not steel");

        check(TieredFanProcessing.itemTier(new ItemStack(net.minecraft.world.item.Items.STONE)) == 0,
                "vanilla stone must be tier 0");
        check(TieredFanProcessing.canProcess(new ItemStack(net.minecraft.world.item.Items.STONE), 0),
                "ULV fan must still process vanilla items");

        check(TieredFanProcessing.minTier(AllFanProcessingTypes.BLASTING) == 0, "blasting min tier should be 0");
        check(TieredFanProcessing.minTier(AllFanProcessingTypes.SPLASHING) == 1, "washing min tier should be 1");
        check(TieredFanProcessing.minTier(AllFanProcessingTypes.HAUNTING) == 2, "haunting min tier should be 2");
        check(!TieredFanProcessing.typeEnabled(AllFanProcessingTypes.SPLASHING, 0),
                "ULV fan must not run washing");
        check(TieredFanProcessing.typeEnabled(AllFanProcessingTypes.SPLASHING, 1),
                "LV fan must run washing");
        check(!TieredFanProcessing.typeEnabled(AllFanProcessingTypes.HAUNTING, 1),
                "LV fan must not run haunting");
        check(TieredFanProcessing.typeEnabled(AllFanProcessingTypes.BLASTING, 0),
                "ULV fan must still run blasting");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_fan_air", timeoutTicks = 120)
    public static void fanAirCurrentHidesGatedType(GameTestHelper helper) {

        int ulvTier = 0;
        int lvTier = 1;
        placeFanWithWater(helper, new BlockPos(0, 1, 1), new BlockPos(1, 1, 1), new BlockPos(3, 1, 1), ulvTier);
        placeFanWithWater(helper, new BlockPos(0, 1, 4), new BlockPos(1, 1, 4), new BlockPos(3, 1, 4), lvTier);

        helper.runAtTickTime(60, () -> {
            TieredAirCurrent ulvCurrent = fanAirCurrent(helper, new BlockPos(1, 1, 1));
            TieredAirCurrent lvCurrent = fanAirCurrent(helper, new BlockPos(1, 1, 4));
            check(ulvCurrent != null && lvCurrent != null, "fan air current is not a TieredAirCurrent");

            var lvType = lvCurrent.getTypeAt(2f);
            check(lvType != null && com.simibubi.create.api.registry.CreateBuiltInRegistries.FAN_PROCESSING_TYPE
                            .getKey(lvType).getPath().equals("splashing"),
                    "LV fan should see the washing catalyst, got " + lvType);
            check(ulvCurrent.getTypeAt(2f) == null,
                    "ULV fan must not see the washing catalyst, got " + ulvCurrent.getTypeAt(2f));
            helper.succeed();
        });
    }

    private static void placeFanWithWater(GameTestHelper helper, BlockPos motorPos, BlockPos fanPos,
                                          BlockPos waterPos, int tier) {
        helper.setBlock(motorPos, AllBlocks.CREATIVE_MOTOR.getDefaultState()
                .setValue(BlockStateProperties.FACING, Direction.EAST));
        helper.setBlock(fanPos, EncasedFans.ALL.get(tier).get().defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.EAST));
        helper.setBlock(waterPos, net.minecraft.world.level.block.Blocks.WATER.defaultBlockState());
    }

    private static TieredAirCurrent fanAirCurrent(GameTestHelper helper, BlockPos fanPos) {
        BlockEntity be = helper.getLevel().getBlockEntity(helper.absolutePos(fanPos));
        check(be instanceof TieredEncasedFanBlockEntity, "fan block entity is not TieredEncasedFanBlockEntity: " + be);
        var airCurrent = ((TieredEncasedFanBlockEntity) be).getAirCurrent();
        return airCurrent instanceof TieredAirCurrent tiered ? tiered : null;
    }

    @GameTest(template = TEMPLATE, batch = "greate_gt_recipes", timeoutTicks = 100)
    public static void gtMachineRecipesAreRegistered(GameTestHelper helper) {
        for (String path : new String[]{
                "assembler/hopper_iron",
                "assembler/chute_iron",
                "assembler/basin",
                "assembler/item_vault_iron",
                "assembler/electron_tube",
                "cutter/shaft_steel",
                "cutter/shaft_andesite_alloy",
                "cutter/shaft_neutronium"}) {
            check(helper.getLevel().getRecipeManager().byKey(Greate.id(path)).isPresent(),
                    "missing GT machine recipe " + path);
        }
        helper.succeed();
    }

    @GameTest(template = TEMPLATE, batch = "greate_fan_wash", timeoutTicks = 100)
    public static void fanWashingBridgesGtOreWasher(GameTestHelper helper) {
        ItemStack crushed = ChemicalHelper.get(TagPrefix.crushed, GTMaterials.Iron);
        check(!crushed.isEmpty(), "gtceu crushed iron ore is missing");

        int required = TieredSplashingType.requiredTier(crushed, helper.getLevel());
        check(required >= 0, "iron ore washer recipe should be resolvable, got tier " + required);

        List<ItemStack> washed = TieredSplashingType.wash(crushed, helper.getLevel(), required);
        check(!washed.isEmpty(), "fan tier " + required + " should wash gtceu:crushed_iron_ore");
        check(washed.stream().anyMatch(stack -> BuiltInRegistries.ITEM.getKey(stack.getItem())
                .toString().equals("gtceu:purified_iron_ore")),
                "washing should produce gtceu:purified_iron_ore, got " + washed);

        check(TieredSplashingType.wash(crushed, helper.getLevel(), required - 1).isEmpty(),
                "fan one tier below the recipe must not wash it");

        check(TieredFanProcessing.itemTier(crushed) == 0,
                "iron is not a tiered Greate material, item tier should be 0");
        helper.succeed();
    }

    private GreateGameTests() {
    }
}
