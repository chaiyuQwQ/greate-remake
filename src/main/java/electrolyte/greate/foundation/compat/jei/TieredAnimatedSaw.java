package electrolyte.greate.foundation.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.kinetics.saw.SawBlock;
import electrolyte.greate.content.kinetics.saw.TieredSawBlock;
import electrolyte.greate.registry.GreatePartialModels;
import electrolyte.greate.registry.Shafts;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class TieredAnimatedSaw extends AnimatedKinetics {

    private final TieredSawBlock block;

    public TieredAnimatedSaw(TieredSawBlock block) {
        this.block = block;
    }

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        if (block == null) {

            return;
        }
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(xOffset, yOffset, 0);
        poseStack.translate(0, 0, 200);
        poseStack.translate(2, 22, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        poseStack.mulPose(Axis.YP.rotationDegrees(22.5f + 90));
        int scale = 25;
        int tier = Math.clamp(block.getTier(), 0, Shafts.ALL.size() - 1);

        blockElement(Shafts.ALL.get(tier).get().defaultBlockState()
                        .setValue(BlockStateProperties.AXIS, Direction.Axis.X))
                .rotateBlock(-getCurrentAngle(), 0, 0)
                .scale(scale)
                .render(graphics);

        blockElement(block.defaultBlockState().setValue(SawBlock.FACING, Direction.UP))
                .rotateBlock(0, 0, 0)
                .scale(scale)
                .render(graphics);

        blockElement(GreatePartialModels.SAW_BLADE_VERTICAL_ACTIVE[tier])
                .rotateBlock(0, -90, -90)
                .scale(scale)
                .render(graphics);

        poseStack.popPose();
    }
}
