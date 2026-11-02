package electrolyte.greate.content.kinetics.simpleRelays;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface ITieredKineticBlockEntity {

    default int getTier() {
        if (this instanceof BlockEntity be) {
            BlockState state = be.getBlockState();
            if (state.getBlock() instanceof ITieredBlock tiered) {
                return tiered.getTier();
            }
        }
        return -1;
    }
}
