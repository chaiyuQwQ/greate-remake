package electrolyte.greate.content.kinetics.saw;

import com.simibubi.create.content.kinetics.saw.SawBlock;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredBlock;
import electrolyte.greate.registry.GreateBlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TieredSawBlock extends SawBlock implements ITieredBlock {

    private final int tier;

    public TieredSawBlock(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public Class<SawBlockEntity> getBlockEntityClass() {
        return SawBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SawBlockEntity> getBlockEntityType() {
        return GreateBlockEntityTypes.TIERED_SAW.get();
    }
}
