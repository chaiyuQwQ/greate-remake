package electrolyte.greate.content.kinetics.mixer;

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlock;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredBlock;
import electrolyte.greate.registry.GreateBlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TieredMechanicalMixerBlock extends MechanicalMixerBlock implements ITieredBlock {

    private final int tier;

    public TieredMechanicalMixerBlock(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public Class<MechanicalMixerBlockEntity> getBlockEntityClass() {
        return MechanicalMixerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MechanicalMixerBlockEntity> getBlockEntityType() {
        return GreateBlockEntityTypes.TIERED_MECHANICAL_MIXER.get();
    }
}
