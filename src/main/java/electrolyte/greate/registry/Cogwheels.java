package electrolyte.greate.registry;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockModel;
import com.simibubi.create.content.kinetics.simpleRelays.CogwheelBlockItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import electrolyte.greate.Greate;
import electrolyte.greate.content.kinetics.simpleRelays.TieredCogWheelBlock;
import net.minecraft.tags.BlockTags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Cogwheels {

    public static final List<BlockEntry<TieredCogWheelBlock>> SMALL = new ArrayList<>(GreateTiers.TIER_COUNT);
    public static final List<BlockEntry<TieredCogWheelBlock>> LARGE = new ArrayList<>(GreateTiers.TIER_COUNT);

    public static Table<TagPrefix, Material, BlockEntry<TieredCogWheelBlock>> SMALL_TABLE = ImmutableTable.of();
    public static Table<TagPrefix, Material, BlockEntry<TieredCogWheelBlock>> LARGE_TABLE = ImmutableTable.of();

    public static void register() {
        SMALL_TABLE = build(false, GreateTagPrefixes.cogwheel, "", "Cogwheel", SMALL);
        LARGE_TABLE = build(true, GreateTagPrefixes.largeCogwheel, "large_", "Large Cogwheel", LARGE);

        Greate.LOGGER.info("[Greate] registered {} cogwheels ({} small + {} large), {} 条 前缀×材料 映射",
                SMALL.size() + LARGE.size(), SMALL.size(), LARGE.size(),
                SMALL_TABLE.size() + LARGE_TABLE.size());
    }

    private static Table<TagPrefix, Material, BlockEntry<TieredCogWheelBlock>> build(
            boolean large, TagPrefix prefix, String idPrefix, String langSuffix,
            List<BlockEntry<TieredCogWheelBlock>> out) {

        ImmutableTable.Builder<TagPrefix, Material, BlockEntry<TieredCogWheelBlock>> builder = ImmutableTable.builder();

        for (GreateTiers.TierMaterial tierMaterial : GreateTiers.MATERIALS) {
            Material material = GreateMaterials.forTier(tierMaterial.id());
            if (material == null) {
                Greate.LOGGER.warn("[Greate] 材质 {} 未就绪，跳过该等级的{}", tierMaterial.id(), langSuffix);
                continue;
            }

            int tier = tierMaterial.tier();
            BlockEntry<TieredCogWheelBlock> entry = Greate.GT_REGISTRATE
                    .block(idPrefix + tierMaterial.id() + "_cogwheel",
                            properties -> large
                                    ? TieredCogWheelBlock.large(properties, tier)
                                    : TieredCogWheelBlock.small(properties, tier))
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.noOcclusion())
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.MINEABLE_WITH_AXE)

                    .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
                    .blockstate((ctx, prov) -> { })
                    .item((block, props) -> new CogwheelBlockItem(block, props))
                    .model((ctx, prov) -> { })
                    .build()
                    .register();

            builder.put(prefix, material, entry);
            out.add(entry);
        }

        return builder.build();
    }

    public static List<BlockEntry<TieredCogWheelBlock>> all() {
        List<BlockEntry<TieredCogWheelBlock>> list = new ArrayList<>(SMALL.size() + LARGE.size());
        list.addAll(SMALL);
        list.addAll(LARGE);
        return Collections.unmodifiableList(list);
    }

    private Cogwheels() {
    }
}
