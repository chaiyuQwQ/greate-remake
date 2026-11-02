package electrolyte.greate.content.kinetics.millstone;

import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import electrolyte.greate.registry.GreatePartialModels;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class TieredMillstoneRenderer extends KineticBlockEntityRenderer<TieredMillstoneBlockEntity> {

    public TieredMillstoneRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(TieredMillstoneBlockEntity be, BlockState state) {
        return CachedBuffers.partial(GreatePartialModels.MILLSTONE_INNER[be.getTier()], state);
    }
}
