package electrolyte.greate.registry;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockModel;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import electrolyte.greate.Greate;
import electrolyte.greate.content.kinetics.simpleRelays.TieredShaftBlock;
import net.minecraft.tags.BlockTags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Shafts {

    public static final List<BlockEntry<TieredShaftBlock>> ALL = new ArrayList<>(GreateTiers.TIER_COUNT);

    public static Table<TagPrefix, Material, BlockEntry<TieredShaftBlock>> TABLE = ImmutableTable.of();

    public static void register() {
        ImmutableTable.Builder<TagPrefix, Material, BlockEntry<TieredShaftBlock>> builder = ImmutableTable.builder();
        GTRegistrate registrate = Greate.GT_REGISTRATE;

        for (GreateTiers.TierMaterial tierMaterial : GreateTiers.MATERIALS) {
            Material material = GreateMaterials.forTier(tierMaterial.id());
            if (material == null) {
                Greate.LOGGER.warn("[Greate] 材质 {} 未就绪，跳过该等级的传动轴", tierMaterial.id());
                continue;
            }

            BlockEntry<TieredShaftBlock> entry = registrate
                    .block(tierMaterial.id() + "_shaft",
                            properties -> new TieredShaftBlock(properties, tierMaterial.tier()))
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.noOcclusion())
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)

                    .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))

                    .blockstate((ctx, prov) -> { })
                    .item()

                    .model((ctx, prov) -> { })
                    .build()
                    .register();

            builder.put(GreateTagPrefixes.shaft, material, entry);
            ALL.add(entry);
        }

        TABLE = builder.build();
        Greate.LOGGER.info("[Greate] registered {} tiered shafts, {} 条 前缀×材料 映射",
                ALL.size(), TABLE.size());
    }

    public static List<BlockEntry<TieredShaftBlock>> all() {
        return Collections.unmodifiableList(ALL);
    }

    private Shafts() {
    }
}
