package electrolyte.greate.foundation.kinetics;

import com.simibubi.create.api.stress.BlockStressValues;
import electrolyte.greate.infrastructure.config.GreateConfig;
import net.minecraft.world.level.block.Block;

import java.util.function.DoubleSupplier;

public final class GreateStressValues {

    public static double millstoneImpact(int tier) {
        return GreateConfig.machineImpact(GreateConfig.Machine.MILLSTONE, tier);
    }

    public static double pressImpact(int tier) {
        return GreateConfig.machineImpact(GreateConfig.Machine.PRESS, tier);
    }

    public static double mixerImpact(int tier) {
        return GreateConfig.machineImpact(GreateConfig.Machine.MIXER, tier);
    }

    public static double pumpImpact(int tier) {
        return GreateConfig.machineImpact(GreateConfig.Machine.PUMP, tier);
    }

    public static double crushingWheelImpact(int tier) {
        return GreateConfig.machineImpact(GreateConfig.Machine.CRUSHING_WHEEL, tier);
    }

    public static double fanImpact(int tier) {
        return GreateConfig.machineImpact(GreateConfig.Machine.FAN, tier);
    }

    public static double sawImpact(int tier) {
        return GreateConfig.machineImpact(GreateConfig.Machine.SAW, tier);
    }

    public static void registerImpact(Block block, DoubleSupplier impact) {
        BlockStressValues.IMPACTS.register(block, impact);
    }

    private GreateStressValues() {
    }
}
