package electrolyte.greate.registry;

import com.gregtechceu.gtceu.api.material.material.Material;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import electrolyte.greate.Greate;
import electrolyte.greate.content.kinetics.mixer.TieredMechanicalMixerBlock;
import electrolyte.greate.foundation.kinetics.GreateStressValues;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.material.MapColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MechanicalMixers {

    public static final List<BlockEntry<TieredMechanicalMixerBlock>> ALL =
            new ArrayList<>(GreateTiers.TIER_COUNT);

    public static void register() {
        for (GreateTiers.TierMaterial tierMaterial : GreateTiers.MATERIALS) {
            Material material = GreateMaterials.forTier(tierMaterial.id());
            if (material == null) {
                Greate.LOGGER.warn("[Greate] 材质 {} 未就绪，跳过该等级的机械搅拌器", tierMaterial.id());
                continue;
            }
            int tier = tierMaterial.tier();

            BlockEntry<TieredMechanicalMixerBlock> entry = Greate.GT_REGISTRATE
                    .block(tierMaterial.id() + "_mechanical_mixer",
                            properties -> new TieredMechanicalMixerBlock(properties, tier))
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .tag(BlockTags.MINEABLE_WITH_AXE, BlockTags.MINEABLE_WITH_PICKAXE)

                    .onRegister(block -> GreateStressValues.registerImpact(
                            block, () -> GreateStressValues.mixerImpact(tier)))
                    .blockstate((ctx, prov) -> { })
                    .item()
                    .model((ctx, prov) -> { })
                    .build()
                    .register();

            ALL.add(entry);
        }

        Greate.LOGGER.info("[Greate] registered {} tiered mechanical mixers", ALL.size());
    }

    public static List<BlockEntry<TieredMechanicalMixerBlock>> all() {
        return Collections.unmodifiableList(ALL);
    }

    private MechanicalMixers() {
    }
}
