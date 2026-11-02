package electrolyte.greate.infrastructure.config;

import electrolyte.greate.Greate;
import electrolyte.greate.GreateValues;
import electrolyte.greate.registry.GreateTiers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Greate.MOD_ID)
public final class GreateConfig {

    public static final ModConfigSpec SPEC;

    private static final int TIERS = GreateTiers.TIER_COUNT;

    public enum Machine {
        MILLSTONE("millstone", 0.5),
        PRESS("press", 1.0),
        MIXER("mixer", 0.5),
        PUMP("pump", 0.5),
        CRUSHING_WHEEL("crushing_wheel", 0.5),
        FAN("fan", 0.5),
        SAW("saw", 1.0);

        private final String key;
        private final double defaultBase;

        Machine(String key, double defaultBase) {
            this.key = key;
            this.defaultBase = defaultBase;
        }

        public String key() {
            return key;
        }

        public double defaultBase() {
            return defaultBase;
        }
    }

    private static final Machine[] MACHINES = Machine.values();

    private static final String[] BELT_POLYMERS = {
            "rubber", "silicone_rubber", "polyethylene", "polytetrafluoroethylene", "polybenzimidazole"
    };
    private static final int BELT_LENGTH_DEFAULT = 20;
    private static final int BELT_LENGTH_MIN = 5;

    private static final ModConfigSpec.IntValue[] MAX_CAPACITY = new ModConfigSpec.IntValue[TIERS];
    private static final ModConfigSpec.DoubleValue[] MACHINE_BASE = new ModConfigSpec.DoubleValue[MACHINES.length];
    private static final ModConfigSpec.IntValue[] BELT_MAX_LENGTH = new ModConfigSpec.IntValue[BELT_POLYMERS.length];
    private static final ModConfigSpec.BooleanValue HARD_CREATE_RECIPES;

    public static final String[] FAN_TYPE_KEYS = {"blasting", "smoking", "splashing", "haunting"};
    private static final int[] FAN_MIN_TIER_DEFAULT = {0, 0, 1, 2};
    private static final ModConfigSpec.BooleanValue FAN_TIER_GATE;
    private static final ModConfigSpec.IntValue[] FAN_MIN_TIER = new ModConfigSpec.IntValue[FAN_TYPE_KEYS.length];

    private static final int[] CAPACITY_CACHE = new int[TIERS];
    private static final double[] MACHINE_BASE_CACHE = new double[MACHINES.length];
    private static final int[] BELT_LENGTH_CACHE = new int[BELT_POLYMERS.length];
    private static boolean HARD_CREATE_RECIPES_CACHE;
    private static boolean FAN_TIER_GATE_CACHE;
    private static final int[] FAN_MIN_TIER_CACHE = new int[FAN_TYPE_KEYS.length];

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment(
                "Maximum stress (SU) a kinetic block of each tier can support.",
                "网络容量取「网络内最低等级方块」的值；超出的应力会让机器停转。",
                "默认值 = 8 * 4^tier（8 / 32 / 128 / … / 2097152）。")
                .push("max_capacity");
        for (int tier = 0; tier < TIERS; tier++) {
            String key = GreateTiers.MATERIALS.get(tier).id();
            MAX_CAPACITY[tier] = builder
                    .comment(GreateTiers.MATERIALS.get(tier).enName() + " tier (index " + tier + ")")
                    .defineInRange(key, GreateValues.formulaMaxCapacity(tier), 1, Integer.MAX_VALUE);
        }
        builder.pop();

        builder.comment(
                "Stress impact (SU per RPM) baseline of each tiered machine at tier 0.",
                "实际值 = 基准值 × (tier + 1)；例如磨石 0.5 → 0.5 / 1.0 / … / 5.0，压床 1.0 → 1.0 … 10.0。")
                .push("machine_impact");
        for (int i = 0; i < MACHINES.length; i++) {
            Machine machine = MACHINES[i];
            MACHINE_BASE[i] = builder
                    .comment(machine.key() + " (tier 0 baseline)")
                    .defineInRange(machine.key(), machine.defaultBase(), 0.0, 1024.0);
        }
        builder.pop();

        builder.comment(
                "Maximum belt length (in blocks) for each polymer type.",
                "与 Create 的 maxBeltLength 语义一致：连接器两端轴的距离不能超过该值。",
                "默认 20，最小 5。")
                .push("belt_length");
        for (int i = 0; i < BELT_POLYMERS.length; i++) {
            BELT_MAX_LENGTH[i] = builder
                    .comment(BELT_POLYMERS[i] + " belt (covers tiers " + (i * 2) + " and " + (i * 2 + 1) + ")")
                    .defineInRange(BELT_POLYMERS[i], BELT_LENGTH_DEFAULT, BELT_LENGTH_MIN, 1000);
        }
        builder.pop();

        builder.comment(
                "Fan processing tier gate (P2 第七台补完).",
                "min_tier_<type>：该种风处理要求的最低风扇等级（类型轴）。",
                "另有一条自动生效的材料轴：被处理物品所属材料等级必须 ≤ 风扇等级。",
                "Create 原生封装风扇没有等级（tier = -1），两条轴都不限制它，行为与未装本 mod 时一致。")
                .push("fan");
        FAN_TIER_GATE = builder
                .comment("Master switch: false → 所有风扇等级都做全类型风处理（回到旧行为）。")
                .define("tier_gate", true);
        for (int i = 0; i < FAN_TYPE_KEYS.length; i++) {
            FAN_MIN_TIER[i] = builder
                    .comment("Minimum fan tier for " + FAN_TYPE_KEYS[i] + " (0 = 任何等级都能用)")
                    .defineInRange("min_tier_" + FAN_TYPE_KEYS[i], FAN_MIN_TIER_DEFAULT[i], 0, TIERS - 1);
        }
        builder.pop();

        builder.comment(
                "Hard mode: remove Create's kinetic recipes that Greate replaces",
                "(shaft / cogwheel / large cogwheel / gearbox / vertical gearbox / millstone /",
                "press / mixer / pump / saw / encased fan / belt connector / crushing wheel).",
                "默认 false：保留 Create 原版配方，避免破坏 Create 自身的进度；",
                "开启后 Create 的这些方块只能通过 Greate 的分级版本获得（彻底 Greg 化）。")
                .push("recipes");
        HARD_CREATE_RECIPES = builder.define("hard_create_recipes", false);
        builder.pop();

        SPEC = builder.build();

        for (int tier = 0; tier < TIERS; tier++) {
            CAPACITY_CACHE[tier] = GreateValues.formulaMaxCapacity(tier);
        }
        for (int i = 0; i < MACHINES.length; i++) {
            MACHINE_BASE_CACHE[i] = MACHINES[i].defaultBase();
        }
        for (int i = 0; i < BELT_POLYMERS.length; i++) {
            BELT_LENGTH_CACHE[i] = BELT_LENGTH_DEFAULT;
        }
        HARD_CREATE_RECIPES_CACHE = false;
        FAN_TIER_GATE_CACHE = true;
        for (int i = 0; i < FAN_TYPE_KEYS.length; i++) {
            FAN_MIN_TIER_CACHE[i] = FAN_MIN_TIER_DEFAULT[i];
        }
    }

    public static int maxCapacity(int tier) {
        return tier >= 0 && tier < TIERS ? CAPACITY_CACHE[tier] : GreateValues.formulaMaxCapacity(tier);
    }

    public static int beltLength(int polymerIndex) {
        return polymerIndex >= 0 && polymerIndex < BELT_LENGTH_CACHE.length
                ? BELT_LENGTH_CACHE[polymerIndex]
                : BELT_LENGTH_DEFAULT;
    }

    public static boolean hardCreateRecipes() {
        return HARD_CREATE_RECIPES_CACHE;
    }

    public static boolean fanTierGate() {
        return FAN_TIER_GATE_CACHE;
    }

    public static int fanMinTier(int typeIndex) {
        return typeIndex >= 0 && typeIndex < FAN_MIN_TIER_CACHE.length ? FAN_MIN_TIER_CACHE[typeIndex] : 0;
    }

    public static double machineImpact(Machine machine, int tier) {
        double base = MACHINE_BASE_CACHE[machine.ordinal()];
        return base * (tier + 1);
    }

    private static void refresh() {
        if (!SPEC.isLoaded()) {
            return;
        }
        for (int tier = 0; tier < TIERS; tier++) {
            CAPACITY_CACHE[tier] = MAX_CAPACITY[tier].get();
        }
        for (int i = 0; i < MACHINES.length; i++) {
            MACHINE_BASE_CACHE[i] = MACHINE_BASE[i].get();
        }
        for (int i = 0; i < BELT_POLYMERS.length; i++) {
            BELT_LENGTH_CACHE[i] = BELT_MAX_LENGTH[i].get();
        }
        HARD_CREATE_RECIPES_CACHE = HARD_CREATE_RECIPES.get();
        FAN_TIER_GATE_CACHE = FAN_TIER_GATE.get();
        for (int i = 0; i < FAN_TYPE_KEYS.length; i++) {
            FAN_MIN_TIER_CACHE[i] = FAN_MIN_TIER[i].get();
        }
        Greate.LOGGER.info("[Greate] 配置已加载：capacity[0]={}, capacity[9]={}, millstoneBase={}, pressBase={}, beltLength[0]={}",
                CAPACITY_CACHE[0], CAPACITY_CACHE[TIERS - 1],
                MACHINE_BASE_CACHE[Machine.MILLSTONE.ordinal()], MACHINE_BASE_CACHE[Machine.PRESS.ordinal()],
                BELT_LENGTH_CACHE[0]);
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SPEC) {
            refresh();
        }
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            refresh();
        }
    }

    private GreateConfig() {
    }
}
