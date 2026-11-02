package electrolyte.greate.content.kinetics.press;

import com.simibubi.create.content.kinetics.press.MechanicalPressBlock;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredBlock;
import electrolyte.greate.registry.GreateBlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TieredMechanicalPressBlock extends MechanicalPressBlock implements ITieredBlock {

    private final int tier;

    public TieredMechanicalPressBlock(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public Class<MechanicalPressBlockEntity> getBlockEntityClass() {
        return MechanicalPressBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MechanicalPressBlockEntity> getBlockEntityType() {
        return GreateBlockEntityTypes.TIERED_MECHANICAL_PRESS.get();
    }
}
