package electrolyte.greate.registry;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import electrolyte.greate.Greate;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public final class GreatePartialModels {

    public static final PartialModel[] SHAFT = new PartialModel[GreateTiers.TIER_COUNT];

    public static final PartialModel[] COGWHEEL = new PartialModel[GreateTiers.TIER_COUNT];

    public static final PartialModel[] COGWHEEL_SHAFT = new PartialModel[GreateTiers.TIER_COUNT];

    public static final PartialModel[] COGWHEEL_SHAFTLESS = new PartialModel[GreateTiers.TIER_COUNT];

    public static final PartialModel[] LARGE_COGWHEEL_SHAFTLESS = new PartialModel[GreateTiers.TIER_COUNT];

    public static final PartialModel[] MILLSTONE_INNER = new PartialModel[GreateTiers.TIER_COUNT];

    public static final PartialModel[] MECHANICAL_PRESS_HEAD = new PartialModel[GreateTiers.TIER_COUNT];

    public static final PartialModel[] SHAFT_HALF = new PartialModel[GreateTiers.TIER_COUNT];

    public static final PartialModel[] MIXER_HEAD = new PartialModel[GreateTiers.TIER_COUNT];

    public static final PartialModel[] MIXER_POLE = new PartialModel[GreateTiers.TIER_COUNT];

    public static final PartialModel[] PUMP_COG = new PartialModel[GreateTiers.TIER_COUNT];

    public static final PartialModel[] CRUSHING_WHEEL = new PartialModel[GreateTiers.TIER_COUNT];

    public static final PartialModel[] FAN_PROP = new PartialModel[GreateTiers.TIER_COUNT];

    public static final PartialModel[][] BELT_SURFACE =
            new PartialModel[Belts.POLYMER_IDS.length][9];

    public static final PartialModel[] BELT_PULLEY = new PartialModel[GreateTiers.TIER_COUNT];

    public static final PartialModel[] BELT_OVERLAY = new PartialModel[9];

    public static final String[] BELT_SURFACE_ORDER = {
            "belt_start", "belt_middle", "belt_end",
            "belt_start_bottom", "belt_middle_bottom", "belt_end_bottom",
            "belt_diagonal_start", "belt_diagonal_middle", "belt_diagonal_end"
    };

    public static final PartialModel[] SAW_BLADE_HORIZONTAL_ACTIVE = new PartialModel[GreateTiers.TIER_COUNT];
    public static final PartialModel[] SAW_BLADE_HORIZONTAL_REVERSED = new PartialModel[GreateTiers.TIER_COUNT];
    public static final PartialModel[] SAW_BLADE_HORIZONTAL_INACTIVE = new PartialModel[GreateTiers.TIER_COUNT];
    public static final PartialModel[] SAW_BLADE_VERTICAL_ACTIVE = new PartialModel[GreateTiers.TIER_COUNT];
    public static final PartialModel[] SAW_BLADE_VERTICAL_REVERSED = new PartialModel[GreateTiers.TIER_COUNT];
    public static final PartialModel[] SAW_BLADE_VERTICAL_INACTIVE = new PartialModel[GreateTiers.TIER_COUNT];

    static {
        for (int tier = 0; tier < GreateTiers.TIER_COUNT; tier++) {
            String material = GreateTiers.MATERIALS.get(tier).id();
            SHAFT[tier] = block(material + "_shaft");
            COGWHEEL[tier] = block(material + "_cogwheel");
            COGWHEEL_SHAFT[tier] = block(material + "_cogwheel_shaft");
            COGWHEEL_SHAFTLESS[tier] = block(material + "_cogwheel_shaftless");
            LARGE_COGWHEEL_SHAFTLESS[tier] = block("large_" + material + "_cogwheel_shaftless");
            MILLSTONE_INNER[tier] = block(material + "_millstone_inner");
            MECHANICAL_PRESS_HEAD[tier] = block(material + "_mechanical_press_head");
            SHAFT_HALF[tier] = block(material + "_shaft_half");
            MIXER_HEAD[tier] = block(material + "_mechanical_mixer_head");
            MIXER_POLE[tier] = block(material + "_mechanical_mixer_pole");
            PUMP_COG[tier] = block(material + "_mechanical_pump_cog");
            CRUSHING_WHEEL[tier] = block(material + "_crushing_wheel");
            FAN_PROP[tier] = block(material + "_encased_fan_propeller");
            SAW_BLADE_HORIZONTAL_ACTIVE[tier] = block(material + "_mechanical_saw_blade_horizontal_active");
            SAW_BLADE_HORIZONTAL_REVERSED[tier] = block(material + "_mechanical_saw_blade_horizontal_reversed");
            SAW_BLADE_HORIZONTAL_INACTIVE[tier] = block(material + "_mechanical_saw_blade_horizontal_inactive");
            SAW_BLADE_VERTICAL_ACTIVE[tier] = block(material + "_mechanical_saw_blade_vertical_active");
            SAW_BLADE_VERTICAL_REVERSED[tier] = block(material + "_mechanical_saw_blade_vertical_reversed");
            SAW_BLADE_VERTICAL_INACTIVE[tier] = block(material + "_mechanical_saw_blade_vertical_inactive");
        }

        for (int polymer = 0; polymer < Belts.POLYMER_IDS.length; polymer++) {
            String polymerId = Belts.POLYMER_IDS[polymer];
            for (int i = 0; i < BELT_SURFACE_ORDER.length; i++) {
                BELT_SURFACE[polymer][i] = block(polymerId + "_" + BELT_SURFACE_ORDER[i]);
            }
        }
        for (int tier = 0; tier < GreateTiers.TIER_COUNT; tier++) {
            String material = GreateTiers.MATERIALS.get(tier).id();
            String polymerId = Belts.POLYMER_IDS[tier / 2];
            BELT_PULLEY[tier] = block(polymerId + "_belt_" + material + "_pulley");
        }
        for (int i = 0; i < BELT_SURFACE_ORDER.length; i++) {
            BELT_OVERLAY[i] = block("belt_overlay_" + BELT_SURFACE_ORDER[i].substring("belt_".length()));
        }
    }

    public static List<ResourceLocation> allModelIds() {
        List<ResourceLocation> ids = new ArrayList<>(19 * GreateTiers.TIER_COUNT);
        for (int tier = 0; tier < GreateTiers.TIER_COUNT; tier++) {
            String material = GreateTiers.MATERIALS.get(tier).id();
            ids.add(Greate.id("block/" + material + "_shaft"));
            ids.add(Greate.id("block/" + material + "_cogwheel"));
            ids.add(Greate.id("block/" + material + "_cogwheel_shaft"));
            ids.add(Greate.id("block/" + material + "_cogwheel_shaftless"));
            ids.add(Greate.id("block/large_" + material + "_cogwheel_shaftless"));
            ids.add(Greate.id("block/" + material + "_millstone_inner"));
            ids.add(Greate.id("block/" + material + "_mechanical_press_head"));
            ids.add(Greate.id("block/" + material + "_shaft_half"));
            ids.add(Greate.id("block/" + material + "_mechanical_mixer_head"));
            ids.add(Greate.id("block/" + material + "_mechanical_mixer_pole"));
            ids.add(Greate.id("block/" + material + "_mechanical_pump_cog"));
            ids.add(Greate.id("block/" + material + "_crushing_wheel"));
            ids.add(Greate.id("block/" + material + "_encased_fan_propeller"));
            ids.add(Greate.id("block/" + material + "_mechanical_saw_blade_horizontal_active"));
            ids.add(Greate.id("block/" + material + "_mechanical_saw_blade_horizontal_reversed"));
            ids.add(Greate.id("block/" + material + "_mechanical_saw_blade_horizontal_inactive"));
            ids.add(Greate.id("block/" + material + "_mechanical_saw_blade_vertical_active"));
            ids.add(Greate.id("block/" + material + "_mechanical_saw_blade_vertical_reversed"));
            ids.add(Greate.id("block/" + material + "_mechanical_saw_blade_vertical_inactive"));
        }
        for (String polymerId : Belts.POLYMER_IDS) {
            for (String suffix : BELT_SURFACE_ORDER) {
                ids.add(Greate.id("block/" + polymerId + "_" + suffix));
            }
        }
        for (int tier = 0; tier < GreateTiers.TIER_COUNT; tier++) {
            String material = GreateTiers.MATERIALS.get(tier).id();
            String polymerId = Belts.POLYMER_IDS[tier / 2];
            ids.add(Greate.id("block/" + polymerId + "_belt_" + material + "_pulley"));
        }
        for (String suffix : BELT_SURFACE_ORDER) {
            ids.add(Greate.id("block/belt_overlay_" + suffix.substring("belt_".length())));
        }
        return ids;
    }

    public static void register() {
    }

    private static PartialModel block(String path) {
        return PartialModel.of(Greate.id("block/" + path));
    }

    private GreatePartialModels() {
    }
}
