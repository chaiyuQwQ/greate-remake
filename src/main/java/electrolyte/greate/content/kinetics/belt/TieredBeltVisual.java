package electrolyte.greate.content.kinetics.belt;

import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.processing.burner.ScrollInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import electrolyte.greate.registry.GreatePartialModels;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import org.joml.Quaternionf;

import java.util.function.Consumer;

public class TieredBeltVisual extends KineticBlockEntityVisual<TieredBeltBlockEntity> {

    private static final float MAGIC_SCROLL_MULTIPLIER = 1f / (31.5f * 16f);
    private static final float SCROLL_FACTOR_DIAGONAL = 3f / 8f;
    private static final float SCROLL_FACTOR_OTHERWISE = 0.5f;
    private static final float SCROLL_OFFSET_BOTTOM = 0.5f;
    private static final float SCROLL_OFFSET_OTHERWISE = 0f;

    protected final ScrollInstance[] belts;
    protected final ScrollInstance[] overlayBelts;
    protected final RotatingInstance pulley;

    public TieredBeltVisual(VisualizationContext context, TieredBeltBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);

        BeltPart part = blockState.getValue(BeltBlock.PART);
        boolean start = part == BeltPart.START;
        boolean end = part == BeltPart.END;
        DyeColor color = blockEntity.color.orElse(null);

        boolean diagonal = blockState.getValue(BeltBlock.SLOPE).isDiagonal();
        belts = new ScrollInstance[diagonal ? 1 : 2];
        overlayBelts = new ScrollInstance[diagonal ? 1 : 2];

        for (boolean bottom : Iterate.trueAndFalse) {
            TieredBeltBlock block = (TieredBeltBlock) blockState.getBlock();
            PartialModel beltPartial = TieredBeltRenderer.getBeltPartial(block, diagonal, start, end, bottom);
            PartialModel overlayPartial = TieredBeltRenderer.getOverlayPartial(diagonal, start, end, bottom);
            SpriteShiftEntry spriteShift = TieredBeltRenderer.getSpriteShiftEntry(block, diagonal, bottom);
            SpriteShiftEntry overlayShift = TieredBeltRenderer.getDyeOverlayEntry(block, color, diagonal);

            Instancer<ScrollInstance> beltModel = instancerProvider()
                    .instancer(AllInstanceTypes.SCROLLING, Models.partial(beltPartial));
            Instancer<ScrollInstance> overlayModel = instancerProvider()
                    .instancer(AllInstanceTypes.SCROLLING, Models.partial(overlayPartial));

            belts[bottom ? 0 : 1] = setup(beltModel.createInstance(), bottom, spriteShift);
            overlayBelts[bottom ? 0 : 1] = setup(overlayModel.createInstance(), bottom, overlayShift);

            if (diagonal) {
                break;
            }
        }

        if (blockEntity.hasPulley()) {
            pulley = instancerProvider().instancer(AllInstanceTypes.ROTATING, getPulleyModel()).createInstance();
            pulley.setup(TieredBeltVisual.this.blockEntity).setPosition(getVisualPosition()).setChanged();
        } else {
            pulley = null;
        }
    }

    @Override
    public void update(float partialTick) {
        DyeColor color = blockEntity.color.orElse(null);
        TieredBeltBlock block = (TieredBeltBlock) blockState.getBlock();
        boolean diagonal = blockState.getValue(BeltBlock.SLOPE).isDiagonal();
        boolean bottom = true;
        for (ScrollInstance key : belts) {
            SpriteShiftEntry spriteShiftEntry = TieredBeltRenderer.getSpriteShiftEntry(block, diagonal, bottom);
            setup(key, bottom, spriteShiftEntry);
            bottom = false;
        }
        for (ScrollInstance key : overlayBelts) {
            SpriteShiftEntry overlayEntry = TieredBeltRenderer.getDyeOverlayEntry(block, color, diagonal);
            setup(key, bottom, overlayEntry);
        }
        if (pulley != null) {
            pulley.setup(blockEntity).setChanged();
        }
    }

    @Override
    public void updateLight(float partialTick) {
        relight(belts);
        relight(overlayBelts);
        if (pulley != null) {
            relight(pulley);
        }
    }

    @Override
    protected void _delete() {
        for (ScrollInstance key : belts) {
            key.delete();
        }
        for (ScrollInstance key : overlayBelts) {
            key.delete();
        }
        if (pulley != null) {
            pulley.delete();
        }
    }

    private Model getPulleyModel() {
        TieredBeltBlock block = (TieredBeltBlock) blockState.getBlock();
        Direction dir = getOrientation();

        return Models.partial(GreatePartialModels.BELT_PULLEY[block.getTier()], dir.getAxis(),
                (axis, poseStack) -> {
                    TransformStack<PoseTransformStack> msr = TransformStack.of(poseStack);
                    msr.center();
                    if (axis == Axis.X) {
                        msr.rotateYDegrees(90);
                    }
                    if (axis == Axis.Y) {
                        msr.rotateXDegrees(90);
                    }
                    msr.rotateXDegrees(90);
                    msr.uncenter();
                });
    }

    private Direction getOrientation() {
        Direction dir = blockState.getValue(BeltBlock.HORIZONTAL_FACING).getClockWise();
        if (blockState.getValue(BeltBlock.SLOPE) == BeltSlope.SIDEWAYS) {
            dir = Direction.UP;
        }
        return dir;
    }

    private ScrollInstance setup(ScrollInstance key, boolean bottom, SpriteShiftEntry spriteShift) {
        BeltSlope beltSlope = blockState.getValue(BeltBlock.SLOPE);
        Direction facing = blockState.getValue(BeltBlock.HORIZONTAL_FACING);
        boolean diagonal = beltSlope.isDiagonal();
        boolean sideways = beltSlope == BeltSlope.SIDEWAYS;
        boolean vertical = beltSlope == BeltSlope.VERTICAL;
        boolean upward = beltSlope == BeltSlope.UPWARD;
        boolean alongX = facing.getAxis() == Direction.Axis.X;
        boolean alongZ = facing.getAxis() == Direction.Axis.Z;
        boolean downward = beltSlope == BeltSlope.DOWNWARD;

        float speed = blockEntity.getSpeed();
        if (((facing.getAxisDirection() == AxisDirection.NEGATIVE) ^ upward)
                ^ ((alongX && !diagonal) || (alongZ && diagonal))) {
            speed = -speed;
        }
        if (sideways && (facing == Direction.SOUTH || facing == Direction.WEST)
                || (vertical && facing == Direction.EAST)) {
            speed = -speed;
        }

        float rotX = (!diagonal && beltSlope != BeltSlope.HORIZONTAL ? 90 : 0)
                + (downward ? 180 : 0) + (sideways ? 90 : 0) + (vertical && alongZ ? 180 : 0);
        float rotY = facing.toYRot() + ((diagonal ^ alongX) && !downward ? 180 : 0)
                + (sideways && alongZ ? 180 : 0) + (vertical && alongX ? 90 : 0);
        float rotZ = (sideways ? 90 : 0) + (vertical && alongX ? 90 : 0);
        Quaternionf q = new Quaternionf().rotationXYZ(rotX * Mth.DEG_TO_RAD, rotY * Mth.DEG_TO_RAD,
                rotZ * Mth.DEG_TO_RAD);
        key.setSpriteShift(spriteShift, 1f, (diagonal ? SCROLL_FACTOR_DIAGONAL : SCROLL_FACTOR_OTHERWISE))
                .position(getVisualPosition())
                .rotation(q)
                .speed(0, speed * MAGIC_SCROLL_MULTIPLIER)
                .offset(0, bottom ? SCROLL_OFFSET_BOTTOM : SCROLL_OFFSET_OTHERWISE)
                .colorRgb(RotatingInstance.colorFromBE(blockEntity))
                .setChanged();
        return key;
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        if (pulley != null) {
            consumer.accept(pulley);
        }
        for (ScrollInstance key : belts) {
            consumer.accept(key);
        }
        for (ScrollInstance key : overlayBelts) {
            consumer.accept(key);
        }
    }
}
