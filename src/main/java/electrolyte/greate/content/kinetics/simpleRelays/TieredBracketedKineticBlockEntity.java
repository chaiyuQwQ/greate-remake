package electrolyte.greate.content.kinetics.simpleRelays;

import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TieredBracketedKineticBlockEntity extends BracketedKineticBlockEntity
        implements ITieredKineticBlockEntity {

    public TieredBracketedKineticBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
