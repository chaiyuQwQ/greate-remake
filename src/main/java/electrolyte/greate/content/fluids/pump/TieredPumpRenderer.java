package electrolyte.greate.content.fluids.pump;

import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredBlock;
import electrolyte.greate.registry.GreatePartialModels;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class TieredPumpRenderer extends KineticBlockEntityRenderer<TieredPumpBlockEntity> {

    public TieredPumpRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(TieredPumpBlockEntity be, BlockState state) {
        int tier = ((ITieredBlock) state.getBlock()).getTier();
        return CachedBuffers.partialFacing(GreatePartialModels.PUMP_COG[tier], state);
    }
}
