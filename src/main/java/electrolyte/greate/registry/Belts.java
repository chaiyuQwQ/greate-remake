package electrolyte.greate.registry;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.gregtechceu.gtceu.data.item.GTItems;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import com.simibubi.create.content.kinetics.belt.BeltModel;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import electrolyte.greate.Greate;
import electrolyte.greate.content.kinetics.belt.TieredBeltBlock;
import electrolyte.greate.content.kinetics.belt.item.TieredBeltConnectorItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Belts {

    public static final String[] POLYMER_IDS = {
            "rubber", "silicone_rubber", "polyethylene", "polytetrafluoroethylene", "polybenzimidazole"
    };

    public static final List<BlockEntry<TieredBeltBlock>> ALL =
            new ArrayList<>(GreateTiers.TIER_COUNT);

    public static final List<ItemEntry<TieredBeltConnectorItem>> CONNECTOR_ITEMS =
            new ArrayList<>(POLYMER_IDS.length);

    public static Table<TagPrefix, Material, ItemEntry<TieredBeltConnectorItem>> BELT_CONNECTORS = ImmutableTable.of();

    public static void register() {
        for (GreateTiers.TierMaterial tierMaterial : GreateTiers.MATERIALS) {
            int tier = tierMaterial.tier();
            Material shaftMaterial = GreateMaterials.forTier(tierMaterial.id());
            if (shaftMaterial == null) {
                Greate.LOGGER.warn("[Greate] 材质 {} 未就绪，跳过该等级的传送带", tierMaterial.id());
                continue;
            }
            int polymerIndex = tier / 2;
            Material beltMaterial = polymerMaterial(polymerIndex);
            if (beltMaterial == null) {
                Greate.LOGGER.warn("[Greate] 聚合物 {} 未就绪，跳过该等级的传送带", POLYMER_IDS[polymerIndex]);
                continue;
            }

            BlockEntry<TieredBeltBlock> belt = Greate.GT_REGISTRATE
                    .block(POLYMER_IDS[polymerIndex] + "_belt_" + tierMaterial.id(),
                            properties -> new TieredBeltBlock(properties, tier, beltMaterial))

                    .properties(p -> p.sound(SoundType.WOOL)
                            .strength(0.8F)
                            .mapColor(MapColor.COLOR_GRAY)

                            .noLootTable())

                    .tag(BlockTags.MINEABLE_WITH_AXE, BlockTags.MINEABLE_WITH_PICKAXE)

                    .onRegister(CreateRegistrate.blockModel(() -> BeltModel::new))

                    .blockstate((ctx, prov) -> {
                    })
                    .register();

            ALL.add(belt);
        }

        ImmutableTable.Builder<TagPrefix, Material, ItemEntry<TieredBeltConnectorItem>> builder =
                ImmutableTable.builder();
        for (int polymerIndex = 0; polymerIndex < POLYMER_IDS.length; polymerIndex++) {
            Material beltMaterial = polymerMaterial(polymerIndex);
            if (beltMaterial == null) {
                Greate.LOGGER.warn("[Greate] 聚合物 {} 未就绪，跳过该传送带连接器", POLYMER_IDS[polymerIndex]);
                continue;
            }

            final int index = polymerIndex;
            ItemEntry<TieredBeltConnectorItem> connector = Greate.GT_REGISTRATE
                    .item(POLYMER_IDS[polymerIndex] + "_belt_connector",
                            properties -> new TieredBeltConnectorItem(properties, beltMaterial, index))

                    .model((ctx, prov) -> {
                    })

                    .transform(GTItems.unificationItem(GreateTagPrefixes.beltConnector, beltMaterial))
                    .register();

            CONNECTOR_ITEMS.add(connector);
            builder.put(GreateTagPrefixes.beltConnector, beltMaterial, connector);
        }
        BELT_CONNECTORS = builder.build();

        Greate.LOGGER.info("[Greate] registered {} tiered belts, {} belt connectors, {} 条 前缀×材料 映射",
                ALL.size(), CONNECTOR_ITEMS.size(), BELT_CONNECTORS.size());
    }

    public static Material polymerMaterial(int polymerIndex) {
        return switch (polymerIndex) {
            case 0 -> GTMaterials.Rubber;
            case 1 -> GTMaterials.SiliconeRubber;
            case 2 -> GTMaterials.Polyethylene;
            case 3 -> GTMaterials.Polytetrafluoroethylene;
            case 4 -> GTMaterials.Polybenzimidazole;
            default -> null;
        };
    }

    public static int polymerIndex(int tier) {
        return tier / 2;
    }

    public static int maxLength(int tier) {
        return electrolyte.greate.infrastructure.config.GreateConfig.beltLength(tier / 2);
    }

    public static List<BlockEntry<TieredBeltBlock>> all() {
        return Collections.unmodifiableList(ALL);
    }

    public static List<ItemEntry<TieredBeltConnectorItem>> connectors() {
        return Collections.unmodifiableList(CONNECTOR_ITEMS);
    }

    private Belts() {
    }
}
