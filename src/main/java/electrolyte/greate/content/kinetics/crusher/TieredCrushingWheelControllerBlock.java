package electrolyte.greate.content.kinetics.crusher;

import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlockEntity;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.infrastructure.config.AllConfigs;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredBlock;
import electrolyte.greate.registry.CrushingWheels;
import electrolyte.greate.registry.GreateBlockEntityTypes;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock.VALID;

public class TieredCrushingWheelControllerBlock extends CrushingWheelControllerBlock implements ITieredBlock {

    private final int tier;

    public TieredCrushingWheelControllerBlock(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public Class<CrushingWheelControllerBlockEntity> getBlockEntityClass() {
        return CrushingWheelControllerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CrushingWheelControllerBlockEntity> getBlockEntityType() {
        return GreateBlockEntityTypes.TIERED_CRUSHING_WHEEL_CONTROLLER.get();
    }

    public TieredCrushingWheelBlock wheelBlock() {
        return CrushingWheels.WHEELS.get(tier).get();
    }

    @Override
    public void updateSpeed(BlockState state, LevelAccessor world, BlockPos pos) {
        withBlockEntityDo(world, pos, be -> {
            if (!state.getValue(VALID)) {
                if (be.crushingspeed != 0) {
                    be.crushingspeed = 0;
                    be.sendData();
                }
                return;
            }

            for (Direction direction : Iterate.directions) {
                BlockState neighbour = world.getBlockState(pos.relative(direction));

                if (neighbour.getBlock() != wheelBlock()) {
                    continue;
                }
                if (neighbour.getValue(BlockStateProperties.AXIS) == direction.getAxis()) {
                    continue;
                }
                BlockEntity adjacent = world.getBlockEntity(pos.relative(direction));
                if (!(adjacent instanceof CrushingWheelBlockEntity wheel)) {
                    continue;
                }
                be.crushingspeed = Math.abs(wheel.getSpeed() / 50f);
                be.sendData();

                wheel.award(AllAdvancements.CRUSHING_WHEEL);
                if (Math.abs(wheel.getSpeed()) > AllConfigs.server().kinetics.maxRotationSpeed.get() - 1) {
                    wheel.award(AllAdvancements.CRUSHER_MAXED);
                }

                break;
            }
        });
    }
}
