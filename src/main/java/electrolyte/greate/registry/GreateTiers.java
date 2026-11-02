package electrolyte.greate.registry;

import electrolyte.greate.GreateValues;

import java.util.List;

public final class GreateTiers {

    public record TierMaterial(String id, String enName, int tier) {

        public int maxStressCapacity() {
            return GreateValues.maxCapacity(tier);
        }
    }

    public static final List<TierMaterial> MATERIALS = List.of(
            new TierMaterial("andesite_alloy", "Andesite Alloy", 0),
            new TierMaterial("steel", "Steel", 1),
            new TierMaterial("aluminium", "Aluminium", 2),
            new TierMaterial("stainless_steel", "Stainless Steel", 3),
            new TierMaterial("titanium", "Titanium", 4),
            new TierMaterial("tungsten_steel", "Tungsten Steel", 5),
            new TierMaterial("rhodium_plated_palladium", "Rhodium Plated Palladium", 6),
            new TierMaterial("naquadah_alloy", "Naquadah Alloy", 7),
            new TierMaterial("darmstadtium", "Darmstadtium", 8),
            new TierMaterial("neutronium", "Neutronium", 9)
    );

    public static final int TIER_COUNT = MATERIALS.size();

    private GreateTiers() {
    }
}
