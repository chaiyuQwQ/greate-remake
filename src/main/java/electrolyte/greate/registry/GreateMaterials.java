package electrolyte.greate.registry;

import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.material.material.info.MaterialIconSet;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import net.minecraft.resources.ResourceLocation;

import static electrolyte.greate.Greate.id;

public final class GreateMaterials {

    public static final String GT_MODID = "gtceu";
    public static Material ANDESITE_ALLOY;

    public static void register() {
        ANDESITE_ALLOY = new Material.Builder(id("andesite_alloy"))
                .color(0xC8B58C)
                .secondaryColor(0xABC7B5)
                .iconSet(MaterialIconSet.DULL)
                .buildAndRegister();
    }

    public static final String[] GT_TIER_IDS = {
            "steel",
            "aluminium",
            "stainless_steel",
            "titanium",
            "tungsten_steel",
            "rhodium_plated_palladium",
            "naquadah_alloy",
            "darmstadtium",
            "neutronium"
    };

    public static Material forTier(String id) {
        return switch (id) {
            case "andesite_alloy" -> ANDESITE_ALLOY;
            case "steel" -> GTMaterials.Steel;
            case "aluminium" -> GTMaterials.Aluminium;
            case "stainless_steel" -> GTMaterials.StainlessSteel;
            case "titanium" -> GTMaterials.Titanium;
            case "tungsten_steel" -> GTMaterials.TungstenSteel;
            case "rhodium_plated_palladium" -> GTMaterials.RhodiumPlatedPalladium;
            case "naquadah_alloy" -> GTMaterials.NaquadahAlloy;
            case "darmstadtium" -> GTMaterials.Darmstadtium;
            case "neutronium" -> GTMaterials.Neutronium;
            default -> null;
        };
    }

    public static ResourceLocation gtId(String path) {
        return ResourceLocation.fromNamespaceAndPath(GT_MODID, path);
    }

    private GreateMaterials() {
    }
}
