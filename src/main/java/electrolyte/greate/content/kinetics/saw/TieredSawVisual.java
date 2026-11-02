package electrolyte.greate.content.kinetics.saw;

import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.saw.SawBlock;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.InstancerProvider;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredBlock;
import electrolyte.greate.registry.GreatePartialModels;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.function.Consumer;

public class TieredSawVisual extends KineticBlockEntityVisual<TieredSawBlockEntity> {

    protected final RotatingInstance rotatingModel;

    public TieredSawVisual(VisualizationContext context, TieredSawBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        rotatingModel = shaft(instancerProvider(), blockState)
                .setup(blockEntity)
                .setPosition(getVisualPosition());
        rotatingModel.setChanged();
    }

    public static RotatingInstance shaft(InstancerProvider instancerProvider, BlockState state) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        Axis axis = facing.getAxis();
        int tier = ((ITieredBlock) state.getBlock()).getTier();
        if (axis.isHorizontal()) {
            Direction align = facing.getOpposite();
            return instancerProvider
                    .instancer(AllInstanceTypes.ROTATING, Models.partial(GreatePartialModels.SHAFT_HALF[tier]))
                    .createInstance()
                    .rotateTo(0, 0, 1, align.getStepX(), align.getStepY(), align.getStepZ());
        }
        return instancerProvider
                .instancer(AllInstanceTypes.ROTATING, Models.partial(GreatePartialModels.SHAFT[tier]))
                .createInstance()
                .rotateToFace(state.getValue(SawBlock.AXIS_ALONG_FIRST_COORDINATE) ? Axis.X : Axis.Z);
    }

    @Override
    public void update(float partialTick) {
        rotatingModel.setup(blockEntity).setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        relight(rotatingModel);
    }

    @Override
    protected void _delete() {
        rotatingModel.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(rotatingModel);
    }
}
