package electrolyte.greate.content.kinetics.belt;

import com.gregtechceu.gtceu.api.material.material.Material;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.BeltRenderer;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import electrolyte.greate.registry.GreatePartialModels;
import electrolyte.greate.registry.GreateSpriteShifts;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Supplier;

public class TieredBeltRenderer extends BeltRenderer {

    public TieredBeltRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(BeltBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        if (!VisualizationManager.supportsVisualization(be.getLevel())) {
            BlockState blockState = be.getBlockState();
            if (!(blockState.getBlock() instanceof TieredBeltBlock beltBlock)) {
                return;
            }

            BeltSlope beltSlope = blockState.getValue(BeltBlock.SLOPE);
            BeltPart part = blockState.getValue(BeltBlock.PART);
            Direction facing = blockState.getValue(BeltBlock.HORIZONTAL_FACING);
            AxisDirection axisDirection = facing.getAxisDirection();

            boolean downward = beltSlope == BeltSlope.DOWNWARD;
            boolean upward = beltSlope == BeltSlope.UPWARD;
            boolean diagonal = downward || upward;
            boolean start = part == BeltPart.START;
            boolean end = part == BeltPart.END;
            boolean sideways = beltSlope == BeltSlope.SIDEWAYS;
            boolean alongX = facing.getAxis() == Direction.Axis.X;

            PoseStack localTransforms = new PoseStack();
            TransformStack<PoseTransformStack> msr = TransformStack.of(localTransforms);
            VertexConsumer vb = buffer.getBuffer(RenderType.cutout());
            float renderTick = AnimationTickHolder.getRenderTime(be.getLevel());

            msr.center()
                    .rotateYDegrees(AngleHelper.horizontalAngle(facing) + (upward ? 180 : 0) + (sideways ? 270 : 0))
                    .rotateZDegrees(sideways ? 90 : 0)
                    .rotateXDegrees(!diagonal && beltSlope != BeltSlope.HORIZONTAL ? 90 : 0)
                    .uncenter();

            if (downward || beltSlope == BeltSlope.VERTICAL && axisDirection == AxisDirection.POSITIVE) {
                boolean b = start;
                start = end;
                end = b;
            }

            DyeColor color = be.color.orElse(null);

            for (boolean bottom : Iterate.trueAndFalse) {
                PartialModel beltPartial = getBeltPartial(beltBlock, diagonal, start, end, bottom);
                PartialModel overlayPartial = getOverlayPartial(diagonal, start, end, bottom);

                SuperByteBuffer beltBuffer = CachedBuffers.partial(beltPartial, blockState).light(light);
                SuperByteBuffer overlayBuffer = CachedBuffers.partial(overlayPartial, blockState).light(light);

                SpriteShiftEntry spriteShift = getSpriteShiftEntry(beltBlock, diagonal, bottom);
                SpriteShiftEntry overlayShift = getDyeOverlayEntry(beltBlock, color, diagonal);

                float speed = be.getSpeed();
                if (speed != 0 || be.color.isPresent()) {
                    float time = renderTick * axisDirection.getStep();
                    if (diagonal && (downward ^ alongX) || !sideways && !diagonal && alongX
                            || sideways && axisDirection == AxisDirection.NEGATIVE) {
                        speed = -speed;
                    }

                    float scrollMult = diagonal ? 3f / 8f : 0.5f;
                    float spriteSize = spriteShift.getTarget().getV1() - spriteShift.getTarget().getV0();

                    double scroll = speed * time / (31.5 * 16) + (bottom ? 0.5 : 0.0);
                    scroll = scroll - Math.floor(scroll);
                    scroll = scroll * spriteSize * scrollMult;

                    beltBuffer.shiftUVScrolling(spriteShift, (float) scroll);
                    overlayBuffer.shiftUVScrolling(overlayShift, (float) scroll);
                }

                beltBuffer.transform(localTransforms).renderInto(ms, vb);
                overlayBuffer.transform(localTransforms).renderInto(ms, vb);

                if (diagonal) {
                    break;
                }
            }

            if (be.hasPulley()) {
                Direction dir = sideways ? Direction.UP
                        : blockState.getValue(BeltBlock.HORIZONTAL_FACING).getClockWise();

                Supplier<PoseStack> matrixStackSupplier = () -> {
                    PoseStack stack = new PoseStack();
                    TransformStack<PoseTransformStack> stacker = TransformStack.of(stack);
                    stacker.center();
                    if (dir.getAxis() == Direction.Axis.X) {
                        stacker.rotateYDegrees(90);
                    }
                    if (dir.getAxis() == Direction.Axis.Y) {
                        stacker.rotateXDegrees(90);
                    }
                    stacker.rotateXDegrees(90);
                    stacker.uncenter();
                    return stack;
                };

                SuperByteBuffer superBuffer = CachedBuffers.partialDirectional(
                        GreatePartialModels.BELT_PULLEY[beltBlock.getTier()], blockState, dir,
                        matrixStackSupplier);
                KineticBlockEntityRenderer.standardKineticRotationTransform(superBuffer, be, light)
                        .renderInto(ms, vb);
            }
        }

        renderItems(be, partialTicks, ms, buffer, light, overlay);
    }

    public static SpriteShiftEntry getSpriteShiftEntry(TieredBeltBlock block, boolean diagonal, boolean bottom) {
        List<SpriteShiftEntry> entries = GreateSpriteShifts.BELT_SPRITES.get(block);
        if (entries == null) {
            return GreateSpriteShifts.BELT_SPRITES.values().iterator().next().get(0);
        }
        if (diagonal) {
            return entries.get(2);
        }
        return bottom ? entries.get(1) : entries.get(0);
    }

    public static SpriteShiftEntry getDyeOverlayEntry(TieredBeltBlock block, DyeColor color, boolean diagonal) {
        if (color != null) {
            var map = diagonal ? GreateSpriteShifts.DYED_DIAGONAL_BELTS.get(block)
                    : GreateSpriteShifts.DYED_BELTS.get(block);
            if (map != null && map.containsKey(color)) {
                return map.get(color);
            }
        }
        List<SpriteShiftEntry> entries = GreateSpriteShifts.BELT_SPRITES.get(block);
        return entries == null ? GreateSpriteShifts.BELT_SPRITES.values().iterator().next().get(3)
                : entries.get(3);
    }

    public static PartialModel getBeltPartial(TieredBeltBlock block, boolean diagonal, boolean start, boolean end,
                                              boolean bottom) {
        return GreatePartialModels.BELT_SURFACE[block.getTier() / 2][surfaceIndex(diagonal, start, end, bottom)];
    }

    public static PartialModel getOverlayPartial(boolean diagonal, boolean start, boolean end, boolean bottom) {
        return GreatePartialModels.BELT_OVERLAY[surfaceIndex(diagonal, start, end, bottom)];
    }

    private static int surfaceIndex(boolean diagonal, boolean start, boolean end, boolean bottom) {
        if (diagonal) {
            return start ? 6 : end ? 8 : 7;
        }
        if (bottom) {
            return start ? 3 : end ? 5 : 4;
        }
        return start ? 0 : end ? 2 : 1;
    }
}
