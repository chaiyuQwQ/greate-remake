package electrolyte.greate.registry;

import com.gregtechceu.gtceu.api.material.material.Material;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import electrolyte.greate.Greate;
import electrolyte.greate.content.kinetics.crusher.TieredCrushingWheelBlock;
import electrolyte.greate.content.kinetics.crusher.TieredCrushingWheelControllerBlock;
import electrolyte.greate.foundation.kinetics.GreateStressValues;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CrushingWheels {

    public static final List<BlockEntry<TieredCrushingWheelBlock>> WHEELS =
            new ArrayList<>(GreateTiers.TIER_COUNT);

    public static final List<BlockEntry<TieredCrushingWheelControllerBlock>> CONTROLLERS =
            new ArrayList<>(GreateTiers.TIER_COUNT);

    public static void register() {
        for (GreateTiers.TierMaterial tierMaterial : GreateTiers.MATERIALS) {
            Material material = GreateMaterials.forTier(tierMaterial.id());
            if (material == null) {
                Greate.LOGGER.warn("[Greate] 材质 {} 未就绪，跳过该等级的粉碎轮", tierMaterial.id());
                continue;
            }
            int tier = tierMaterial.tier();

            BlockEntry<TieredCrushingWheelBlock> wheel = Greate.GT_REGISTRATE
                    .block(tierMaterial.id() + "_crushing_wheel",
                            properties -> new TieredCrushingWheelBlock(properties, tier))
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.mapColor(MapColor.METAL))

                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .onRegister(block -> GreateStressValues.registerImpact(
                            block, () -> GreateStressValues.crushingWheelImpact(tier)))

                    .blockstate((ctx, prov) -> { })
                    .item()
                    .model((ctx, prov) -> { })
                    .build()
                    .register();
            WHEELS.add(wheel);

            BlockEntry<TieredCrushingWheelControllerBlock> controller = Greate.GT_REGISTRATE
                    .block(tierMaterial.id() + "_crushing_wheel_controller",
                            properties -> new TieredCrushingWheelControllerBlock(properties, tier))

                    .properties(p -> p.mapColor(MapColor.STONE)
                            .noOcclusion()
                            .noLootTable()
                            .air()
                            .noCollission()
                            .pushReaction(PushReaction.BLOCK))
                    .blockstate((ctx, prov) -> { })
                    .register();
            CONTROLLERS.add(controller);
        }

        Greate.LOGGER.info("[Greate] registered {} tiered crushing wheels ({} controllers)",
                WHEELS.size(), CONTROLLERS.size());
    }

    public static List<BlockEntry<TieredCrushingWheelBlock>> wheels() {
        return Collections.unmodifiableList(WHEELS);
    }

    public static List<BlockEntry<TieredCrushingWheelControllerBlock>> controllers() {
        return Collections.unmodifiableList(CONTROLLERS);
    }

    private CrushingWheels() {
    }
}
