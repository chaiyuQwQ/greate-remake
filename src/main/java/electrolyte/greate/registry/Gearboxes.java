package electrolyte.greate.registry;

import com.gregtechceu.gtceu.api.material.material.Material;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import electrolyte.greate.Greate;
import electrolyte.greate.content.kinetics.gearbox.TieredGearboxBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Gearboxes {

    public static final List<BlockEntry<TieredGearboxBlock>> ALL =
            new ArrayList<>(GreateTiers.TIER_COUNT);

    public static final List<Integer> TIERS = new ArrayList<>(GreateTiers.TIER_COUNT);

    public static void register() {
        for (GreateTiers.TierMaterial tierMaterial : GreateTiers.MATERIALS) {
            Material material = GreateMaterials.forTier(tierMaterial.id());
            if (material == null) {
                Greate.LOGGER.warn("[Greate] 材质 {} 未就绪，跳过该等级的齿轮箱", tierMaterial.id());
                continue;
            }
            int tier = tierMaterial.tier();

            BlockEntry<TieredGearboxBlock> entry = Greate.GT_REGISTRATE
                    .block(tierMaterial.id() + "_gearbox",
                            properties -> new TieredGearboxBlock(properties, tier))
                    .initialProperties(SharedProperties::stone)

                    .properties(p -> p.noOcclusion()
                            .mapColor(MapColor.PODZOL)
                            .pushReaction(PushReaction.PUSH_ONLY))
                    .tag(BlockTags.MINEABLE_WITH_AXE, BlockTags.MINEABLE_WITH_PICKAXE)

                    .blockstate((ctx, prov) -> { })
                    .item()
                    .model((ctx, prov) -> { })
                    .build()
                    .register();

            ALL.add(entry);
            TIERS.add(tier);
        }

        Greate.LOGGER.info("[Greate] registered {} tiered gearboxes", ALL.size());
    }

    public static List<BlockEntry<TieredGearboxBlock>> all() {
        return Collections.unmodifiableList(ALL);
    }

    private Gearboxes() {
    }
}
