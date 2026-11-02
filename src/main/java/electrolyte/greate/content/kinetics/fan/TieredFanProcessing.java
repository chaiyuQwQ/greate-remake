package electrolyte.greate.content.kinetics.fan;

import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import electrolyte.greate.Greate;
import electrolyte.greate.infrastructure.config.GreateConfig;
import electrolyte.greate.registry.GreateMaterials;
import electrolyte.greate.registry.GreateTiers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Fan-tier gate rules, on two independent axes:
 * <ul>
 *   <li>per type: minimum fan tier (blasting/smoking 0, washing 1, haunting 2, configurable);</li>
 *   <li>per item: material tier must not exceed the fan tier.</li>
 * </ul>
 * Item ids are matched longest-first so {@code tungsten_steel} does not resolve to
 * {@code steel}; ids from other namespaces are never matched.
 */
public final class TieredFanProcessing {

    public static final String[] TYPE_KEYS = {"blasting", "smoking", "splashing", "haunting"};

    private static final String[] MATERIAL_IDS_DESC = GreateTiers.MATERIALS.stream()
            .sorted(Comparator.comparingInt(GreateTiers.TierMaterial::tier).reversed())
            .map(GreateTiers.TierMaterial::id)
            .toArray(String[]::new);

    private static final Map<Item, Integer> ITEM_TIER_CACHE = new HashMap<>();

    private static final ThreadLocal<Integer> CURRENT_TIER = new ThreadLocal<>();

    public static int currentTier() {
        Integer tier = CURRENT_TIER.get();
        return tier == null ? -1 : tier;
    }

    public static <T> T withTier(int tier, Supplier<T> body) {
        Integer previous = CURRENT_TIER.get();
        CURRENT_TIER.set(tier);
        try {
            return body.get();
        } finally {
            if (previous == null) {
                CURRENT_TIER.remove();
            } else {
                CURRENT_TIER.set(previous);
            }
        }
    }

    private TieredFanProcessing() {
    }

    public static int minTier(FanProcessingType type) {
        if (type instanceof AllFanProcessingTypes.BlastingType) {
            return GreateConfig.fanMinTier(0);
        }
        if (type instanceof AllFanProcessingTypes.SmokingType) {
            return GreateConfig.fanMinTier(1);
        }
        if (type instanceof AllFanProcessingTypes.SplashingType) {
            return GreateConfig.fanMinTier(2);
        }
        if (type instanceof AllFanProcessingTypes.HauntingType) {
            return GreateConfig.fanMinTier(3);
        }
        return 0;
    }

    public static boolean typeEnabled(FanProcessingType type, int fanTier) {
        if (fanTier < 0 || !GreateConfig.fanTierGate()) {
            return true;
        }
        return minTier(type) <= fanTier;
    }

    public static boolean canProcess(ItemStack stack, int fanTier) {
        if (fanTier < 0 || !GreateConfig.fanTierGate() || stack.isEmpty()) {
            return true;
        }
        return itemTier(stack) <= fanTier;
    }

    public static int itemTier(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        return ITEM_TIER_CACHE.computeIfAbsent(stack.getItem(), TieredFanProcessing::lookupItemTier);
    }

    private static int lookupItemTier(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        if (id == null) {
            return 0;
        }
        String namespace = id.getNamespace();
        if (!Greate.MOD_ID.equals(namespace) && !GreateMaterials.GT_MODID.equals(namespace)) {
            return 0;
        }
        String path = id.getPath();
        for (String material : MATERIAL_IDS_DESC) {
            if (path.startsWith(material + "_")
                    || path.endsWith("_" + material)
                    || path.contains("_" + material + "_")) {
                return tierOf(material);
            }
        }
        return 0;
    }

    private static int tierOf(String materialId) {
        for (GreateTiers.TierMaterial material : GreateTiers.MATERIALS) {
            if (material.id().equals(materialId)) {
                return material.tier();
            }
        }
        return 0;
    }
}
