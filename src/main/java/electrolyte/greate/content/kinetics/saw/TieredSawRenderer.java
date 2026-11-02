package electrolyte.greate.content.kinetics.saw;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.saw.SawBlock;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.content.kinetics.saw.SawRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredBlock;
import electrolyte.greate.registry.GreatePartialModels;
import electrolyte.greate.registry.GreateTiers;
import electrolyte.greate.registry.Shafts;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class TieredSawRenderer extends SawRenderer {

    public TieredSawRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderBlade(SawBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light) {
        BlockState state = be.getBlockState();
        int tier = tierOf(state);
        float speed = be.getSpeed();
        boolean rotate = false;
        PartialModel blade;

        if (SawBlock.isHorizontal(state)) {
            if (speed > 0) {
                blade = GreatePartialModels.SAW_BLADE_HORIZONTAL_ACTIVE[tier];
            } else if (speed < 0) {
                blade = GreatePartialModels.SAW_BLADE_HORIZONTAL_REVERSED[tier];
            } else {
                blade = GreatePartialModels.SAW_BLADE_HORIZONTAL_INACTIVE[tier];
            }
        } else {
            if (speed > 0) {
                blade = GreatePartialModels.SAW_BLADE_VERTICAL_ACTIVE[tier];
            } else if (speed < 0) {
                blade = GreatePartialModels.SAW_BLADE_VERTICAL_REVERSED[tier];
            } else {
                blade = GreatePartialModels.SAW_BLADE_VERTICAL_INACTIVE[tier];
            }
            if (state.getValue(SawBlock.AXIS_ALONG_FIRST_COORDINATE)) {
                rotate = true;
            }
        }

        SuperByteBuffer superBuffer = CachedBuffers.partialFacing(blade, state);
        if (rotate) {
            superBuffer.rotateCentered(AngleHelper.rad(90), Direction.UP);
        }
        superBuffer.color(0xFFFFFF)
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
    }

    @Override
    protected SuperByteBuffer getRotatedModel(KineticBlockEntity be) {
        BlockState state = be.getBlockState();
        int tier = tierOf(state);
        if (state.getValue(FACING).getAxis().isHorizontal()) {
            return CachedBuffers.partialFacing(GreatePartialModels.SHAFT_HALF[tier],
                    state.rotate(be.getLevel(), be.getBlockPos(), Rotation.CLOCKWISE_180));
        }
        return CachedBuffers.block(KineticBlockEntityRenderer.KINETIC_BLOCK, getRenderedBlockState(be));
    }

    @Override
    protected BlockState getRenderedBlockState(KineticBlockEntity be) {
        BlockState shaft = Shafts.ALL.get(tierOf(be.getBlockState())).get().defaultBlockState();
        return shaft.setValue(BlockStateProperties.AXIS, KineticBlockEntityRenderer.getRotationAxisOf(be));
    }

    private static int tierOf(BlockState state) {
        int tier = state.getBlock() instanceof ITieredBlock tiered ? tiered.getTier() : 0;
        return Math.clamp(tier, 0, GreateTiers.TIER_COUNT - 1);
    }
}
