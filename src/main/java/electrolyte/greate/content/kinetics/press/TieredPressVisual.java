package electrolyte.greate.content.kinetics.press;

import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlock;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredBlock;
import electrolyte.greate.registry.GreatePartialModels;
import net.createmod.catnip.math.AngleHelper;
import org.joml.Quaternionf;

import java.util.function.Consumer;

public class TieredPressVisual extends SingleAxisRotatingVisual<TieredMechanicalPressBlockEntity>
        implements SimpleDynamicVisual {

    private final OrientedInstance pressHead;

    public TieredPressVisual(VisualizationContext context, TieredMechanicalPressBlockEntity blockEntity,
                             float partialTick) {
        super(context, blockEntity, partialTick,
                Models.partial(GreatePartialModels.SHAFT[tierOf(blockEntity)]));

        pressHead = instancerProvider()
                .instancer(InstanceTypes.ORIENTED,
                        Models.partial(GreatePartialModels.MECHANICAL_PRESS_HEAD[tierOf(blockEntity)]))
                .createInstance();

        Quaternionf q = Axis.YP.rotationDegrees(
                AngleHelper.horizontalAngle(blockState.getValue(MechanicalPressBlock.HORIZONTAL_FACING)));
        pressHead.rotation(q);

        transformModels(partialTick);
    }

    @Override
    public void beginFrame(DynamicVisual.Context ctx) {
        transformModels(ctx.partialTick());
    }

    private void transformModels(float pt) {
        float renderedHeadOffset = getRenderedHeadOffset(pt);
        pressHead.position(getVisualPosition())
                .translatePosition(0, -renderedHeadOffset, 0)
                .setChanged();
    }

    private float getRenderedHeadOffset(float pt) {
        PressingBehaviour pressingBehaviour = blockEntity.getPressingBehaviour();
        return pressingBehaviour.getRenderedHeadOffset(pt) * pressingBehaviour.mode.headOffset;
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(pressHead);
    }

    @Override
    protected void _delete() {
        super._delete();
        pressHead.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept(pressHead);
    }

    private static int tierOf(TieredMechanicalPressBlockEntity be) {
        return ((ITieredBlock) be.getBlockState().getBlock()).getTier();
    }
}
