package electrolyte.greate.content.kinetics.fan;

import com.simibubi.create.content.kinetics.fan.EncasedFanBlock;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredBlock;
import electrolyte.greate.registry.GreateBlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TieredEncasedFanBlock extends EncasedFanBlock implements ITieredBlock {

    private final int tier;

    public TieredEncasedFanBlock(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public Class<EncasedFanBlockEntity> getBlockEntityClass() {
        return EncasedFanBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends EncasedFanBlockEntity> getBlockEntityType() {
        return GreateBlockEntityTypes.TIERED_ENCASED_FAN.get();
    }
}
