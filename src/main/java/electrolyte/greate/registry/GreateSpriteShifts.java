package electrolyte.greate.registry;

import electrolyte.greate.Greate;
import electrolyte.greate.content.kinetics.belt.TieredBeltBlock;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SpriteShifter;
import net.minecraft.world.item.DyeColor;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GreateSpriteShifts {

    public static final Map<TieredBeltBlock, List<SpriteShiftEntry>> BELT_SPRITES = new HashMap<>();
    public static final Map<TieredBeltBlock, Map<DyeColor, SpriteShiftEntry>> DYED_BELTS = new HashMap<>();
    public static final Map<TieredBeltBlock, Map<DyeColor, SpriteShiftEntry>> DYED_DIAGONAL_BELTS =
            new HashMap<>();

    public static void populate(TieredBeltBlock belt) {
        String beltMaterial = Belts.POLYMER_IDS[beltsPolymerIndex(belt)];
        BELT_SPRITES.put(belt, List.of(
                get(beltMaterial + "/belt", beltMaterial + "/belt_scroll"),
                get(beltMaterial + "/belt_offset", beltMaterial + "/belt_scroll"),
                get(beltMaterial + "/belt_diagonal", beltMaterial + "/belt_diagonal_scroll"),
                get("belt_overlay/empty", "belt_overlay/empty_scroll")));

        EnumMap<DyeColor, SpriteShiftEntry> colorMap = new EnumMap<>(DyeColor.class);
        EnumMap<DyeColor, SpriteShiftEntry> diagonalMap = new EnumMap<>(DyeColor.class);
        for (DyeColor color : DyeColor.values()) {
            String id = color.getSerializedName();
            colorMap.put(color, get("belt_overlay/empty", "belt_overlay/" + id));
            diagonalMap.put(color, get("belt_overlay/empty", "belt_overlay/" + id + "_diagonal"));
        }
        DYED_BELTS.put(belt, colorMap);
        DYED_DIAGONAL_BELTS.put(belt, diagonalMap);
    }

    private static int beltsPolymerIndex(TieredBeltBlock belt) {
        return belt.getTier() / 2;
    }

    private static SpriteShiftEntry get(String originalLocation, String targetLocation) {
        return SpriteShifter.get(Greate.id("block/" + originalLocation), Greate.id("block/" + targetLocation));
    }

    private GreateSpriteShifts() {
    }
}
