package electrolyte.greate.registry;

import com.tterrag.registrate.util.entry.ItemEntry;
import electrolyte.greate.Greate;
import electrolyte.greate.content.kinetics.gearbox.TieredGearboxBlock;
import electrolyte.greate.content.kinetics.gearbox.TieredVerticalGearboxItem;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class VerticalGearboxes {

    public static final List<ItemEntry<TieredVerticalGearboxItem>> ALL =
            new ArrayList<>(GreateTiers.TIER_COUNT);

    public static void register() {
        for (int i = 0; i < Gearboxes.ALL.size(); i++) {
            var gearbox = Gearboxes.ALL.get(i);

            int tier = Gearboxes.TIERS.get(i);
            GreateTiers.TierMaterial tierMaterial = GreateTiers.MATERIALS.get(tier);

            ItemEntry<TieredVerticalGearboxItem> entry = Greate.GT_REGISTRATE
                    .item(tierMaterial.id() + "_vertical_gearbox",
                            properties -> new TieredVerticalGearboxItem(gearbox.get(), properties))

                    .model((ctx, prov) -> { })

                    .onRegister(item -> gearbox.get().setVerticalItem(item))
                    .register();

            ALL.add(entry);
        }

        Greate.LOGGER.info("[Greate] registered {} tiered vertical gearboxes (items)", ALL.size());
    }

    public static List<ItemEntry<TieredVerticalGearboxItem>> all() {
        return Collections.unmodifiableList(ALL);
    }

    public static Item byTier(int tier) {
        return tier >= 0 && tier < ALL.size() ? ALL.get(tier).get() : null;
    }

    private VerticalGearboxes() {
    }
}
