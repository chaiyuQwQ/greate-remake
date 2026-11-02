package electrolyte.greate.content.kinetics.gearbox;

import com.simibubi.create.content.kinetics.gearbox.GearboxBlockEntity;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TieredGearboxBlockEntity extends GearboxBlockEntity implements ITieredKineticBlockEntity {

    public TieredGearboxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
