package electrolyte.greate.content.kinetics.simpleRelays;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import electrolyte.greate.registry.GreateBlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class TieredShaftBlock extends ShaftBlock implements ITieredBlock {

    private final int tier;

    public TieredShaftBlock(BlockBehaviour.Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return GreateBlockEntityTypes.TIERED_BRACKETED_KINETIC.get();
    }

    @Override
    public int getTier() {
        return tier;
    }
}
