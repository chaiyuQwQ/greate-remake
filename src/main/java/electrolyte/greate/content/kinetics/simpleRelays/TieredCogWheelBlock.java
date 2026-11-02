package electrolyte.greate.content.kinetics.simpleRelays;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import electrolyte.greate.registry.GreateBlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TieredCogWheelBlock extends CogWheelBlock implements ITieredBlock {

    private final int tier;

    protected TieredCogWheelBlock(boolean large, Properties properties, int tier) {
        super(large, properties);
        this.tier = tier;
    }

    public static TieredCogWheelBlock small(Properties properties, int tier) {
        return new TieredCogWheelBlock(false, properties, tier);
    }

    public static TieredCogWheelBlock large(Properties properties, int tier) {
        return new TieredCogWheelBlock(true, properties, tier);
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
