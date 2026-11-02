package electrolyte.greate.content.kinetics.base;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredBlock;
import electrolyte.greate.registry.GreatePartialModels;
import net.minecraft.core.Direction;

public final class TieredSingleAxisRotatingVisual {

    public static <T extends KineticBlockEntity> SingleAxisRotatingVisual<T> millstoneCog(
            VisualizationContext context, T blockEntity, float partialTick) {
        int tier = ((ITieredBlock) blockEntity.getBlockState().getBlock()).getTier();
        return new SingleAxisRotatingVisual<>(context, blockEntity, partialTick,
                Models.partial(GreatePartialModels.MILLSTONE_INNER[tier]));
    }

    public static <T extends KineticBlockEntity> SingleAxisRotatingVisual<T> pumpCog(
            VisualizationContext context, T blockEntity, float partialTick) {
        int tier = ((ITieredBlock) blockEntity.getBlockState().getBlock()).getTier();
        return new SingleAxisRotatingVisual<>(context, blockEntity, partialTick, Direction.SOUTH,
                Models.partial(GreatePartialModels.PUMP_COG[tier]));
    }

    public static <T extends KineticBlockEntity> SingleAxisRotatingVisual<T> crushingWheel(
            VisualizationContext context, T blockEntity, float partialTick) {
        int tier = ((ITieredBlock) blockEntity.getBlockState().getBlock()).getTier();
        return new SingleAxisRotatingVisual<>(context, blockEntity, partialTick,
                Models.partial(GreatePartialModels.CRUSHING_WHEEL[tier]));
    }

    private TieredSingleAxisRotatingVisual() {
    }
}
