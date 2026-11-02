package electrolyte.greate.content.kinetics.saw;

import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TieredSawBlockEntity extends SawBlockEntity implements ITieredKineticBlockEntity {

    public TieredSawBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
