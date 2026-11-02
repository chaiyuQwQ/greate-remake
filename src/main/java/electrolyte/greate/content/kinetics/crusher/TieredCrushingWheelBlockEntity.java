package electrolyte.greate.content.kinetics.crusher;

import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlockEntity;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TieredCrushingWheelBlockEntity extends CrushingWheelBlockEntity
        implements ITieredKineticBlockEntity {

    public TieredCrushingWheelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
