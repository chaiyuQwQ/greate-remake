package electrolyte.greate.content.kinetics.simpleRelays;

import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import electrolyte.greate.registry.GreatePartialModels;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public final class TieredBracketedKineticBlockEntityVisual {

    public static BlockEntityVisual<TieredBracketedKineticBlockEntity> create(
            VisualizationContext context, TieredBracketedKineticBlockEntity be, float partialTick) {
        BlockState state = be.getBlockState();
        int tier = ((ITieredBlock) state.getBlock()).getTier();

        if (ICogWheel.isLargeCog(state)) {
            return new TieredLargeCogVisual(context, be, partialTick, tier);
        }
        Model model = ICogWheel.isSmallCog(state)
                ? Models.partial(GreatePartialModels.COGWHEEL[tier])
                : Models.partial(GreatePartialModels.SHAFT[tier]);
        return new SingleAxisRotatingVisual<>(context, be, partialTick, model);
    }

    private TieredBracketedKineticBlockEntityVisual() {
    }

    public static class TieredLargeCogVisual extends SingleAxisRotatingVisual<TieredBracketedKineticBlockEntity> {

        protected final RotatingInstance additionalShaft;

        public TieredLargeCogVisual(VisualizationContext context, TieredBracketedKineticBlockEntity be,
                                    float partialTick, int tier) {
            super(context, be, partialTick, Models.partial(GreatePartialModels.LARGE_COGWHEEL_SHAFTLESS[tier]));

            Axis axis = KineticBlockEntityRenderer.getRotationAxisOf(be);
            additionalShaft = instancerProvider()
                    .instancer(AllInstanceTypes.ROTATING, Models.partial(GreatePartialModels.COGWHEEL_SHAFT[tier]))
                    .createInstance();
            additionalShaft.rotateToFace(axis)
                    .setup(be)
                    .setRotationOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, pos))
                    .setPosition(getVisualPosition())
                    .setChanged();
        }

        @Override
        public void update(float partialTick) {
            super.update(partialTick);
            additionalShaft.setup(blockEntity)
                    .setRotationOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(rotationAxis(), pos))
                    .setChanged();
        }

        @Override
        public void updateLight(float partialTick) {
            super.updateLight(partialTick);
            relight(additionalShaft);
        }

        @Override
        protected void _delete() {
            super._delete();
            additionalShaft.delete();
        }

        @Override
        public void collectCrumblingInstances(Consumer<Instance> consumer) {
            super.collectCrumblingInstances(consumer);
            consumer.accept(additionalShaft);
        }
    }
}
