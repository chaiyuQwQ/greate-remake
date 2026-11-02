package electrolyte.greate.content.kinetics.press;

import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import electrolyte.greate.registry.GreatePartialModels;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class TieredPressRenderer extends KineticBlockEntityRenderer<TieredMechanicalPressBlockEntity> {

    public TieredPressRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(TieredMechanicalPressBlockEntity be, BlockState state) {
        return CachedBuffers.partial(GreatePartialModels.SHAFT[be.getTier()], state);
    }
}
