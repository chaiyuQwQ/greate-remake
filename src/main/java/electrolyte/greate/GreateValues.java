package electrolyte.greate;

import electrolyte.greate.infrastructure.config.GreateConfig;
import net.minecraft.ChatFormatting;

public final class GreateValues {

    public static final String[] SN = {"ULS", "LS", "MS", "HS", "ES", "IS", "LuS", "ZPMS", "US", "UHS"};

    public static final String[] SNF = {
            ChatFormatting.DARK_GRAY + "ULS",
            ChatFormatting.GRAY + "LS",
            ChatFormatting.AQUA + "MS",
            ChatFormatting.GOLD + "HS",
            ChatFormatting.DARK_PURPLE + "ES",
            ChatFormatting.BLUE + "IS",
            ChatFormatting.LIGHT_PURPLE + "LuS",
            ChatFormatting.RED + "ZPMS",
            ChatFormatting.DARK_AQUA + "US",
            ChatFormatting.DARK_RED + "UHS",
    };

    public static final String[] STRESS_NAMES = {
            "Ultra Low Stress",
            "Low Stress",
            "Medium Stress",
            "High Stress",
            "Extreme Stress",
            "Insane Stress",
            "Ludicrous Stress",
            "ZPM Stress",
            "Ultimate Stress",
            "Ultra High Stress",
    };

    public static int maxCapacity(int tier) {
        return GreateConfig.maxCapacity(tier);
    }

    public static int formulaMaxCapacity(int tier) {
        return 8 << (tier * 2);
    }

    public static String shortName(int tier) {
        return tier >= 0 && tier < SN.length ? SN[tier] : "?";
    }

    private GreateValues() {
    }
}
