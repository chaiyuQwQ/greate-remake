package electrolyte.greate.content.fluids.pump;

import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TieredPumpBlockEntity extends PumpBlockEntity implements ITieredKineticBlockEntity {

    public TieredPumpBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
