package electrolyte.greate.content.kinetics.crusher;

import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlock;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlockEntity;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredBlock;
import electrolyte.greate.registry.CrushingWheels;
import electrolyte.greate.registry.GreateBlockEntityTypes;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock.FACING;
import static com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock.VALID;

public class TieredCrushingWheelBlock extends CrushingWheelBlock implements ITieredBlock {

    private final int tier;

    public TieredCrushingWheelBlock(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public Class<CrushingWheelBlockEntity> getBlockEntityClass() {
        return CrushingWheelBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CrushingWheelBlockEntity> getBlockEntityType() {
        return GreateBlockEntityTypes.TIERED_CRUSHING_WHEEL.get();
    }

    public CrushingWheelControllerBlock controllerBlock() {
        return CrushingWheels.CONTROLLERS.get(tier).get();
    }

    public boolean isOwnController(BlockState state) {
        return state.getBlock() == controllerBlock();
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        for (Direction direction : Iterate.directions) {
            if (direction.getAxis() == state.getValue(AXIS)) {
                continue;
            }
            if (isOwnController(level.getBlockState(pos.relative(direction)))) {
                level.removeBlock(pos.relative(direction), isMoving);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void updateControllers(BlockState state, Level level, BlockPos pos, Direction side) {
        if (side.getAxis() == state.getValue(AXIS)) {
            return;
        }
        if (level == null) {
            return;
        }

        BlockPos controllerPos = pos.relative(side);
        BlockPos otherWheelPos = pos.relative(side, 2);

        BlockState controllerState = level.getBlockState(controllerPos);
        boolean controllerExists = isOwnController(controllerState);
        boolean controllerIsValid = controllerExists && controllerState.getValue(VALID);
        Direction controllerOldDirection = controllerExists ? controllerState.getValue(FACING) : null;

        boolean controllerShouldExist = false;
        boolean controllerShouldBeValid = false;
        Direction controllerNewDirection = Direction.DOWN;

        BlockState otherState = level.getBlockState(otherWheelPos);
        if (otherState.getBlock() == this) {
            controllerShouldExist = true;

            CrushingWheelBlockEntity be = getBlockEntity(level, pos);
            CrushingWheelBlockEntity otherBE = getBlockEntity(level, otherWheelPos);

            if (be != null && otherBE != null && (be.getSpeed() > 0) != (otherBE.getSpeed() > 0)
                    && be.getSpeed() != 0 && otherBE.getSpeed() != 0) {
                Axis wheelAxis = state.getValue(AXIS);
                Axis sideAxis = side.getAxis();
                int controllerADO = Math.round(Math.signum(be.getSpeed())) * side.getAxisDirection()
                        .getStep();
                Vec3 controllerDirVec = new Vec3(wheelAxis == Axis.X ? 1 : 0, wheelAxis == Axis.Y ? 1 : 0,
                        wheelAxis == Axis.Z ? 1 : 0).cross(
                                new Vec3(sideAxis == Axis.X ? 1 : 0, sideAxis == Axis.Y ? 1 : 0,
                                        sideAxis == Axis.Z ? 1 : 0));

                controllerNewDirection = Direction.getNearest(controllerDirVec.x * controllerADO,
                        controllerDirVec.y * controllerADO, controllerDirVec.z * controllerADO);

                controllerShouldBeValid = true;
            }
            if (otherState.getValue(AXIS) != state.getValue(AXIS)) {
                controllerShouldExist = false;
            }
        }

        if (!controllerShouldExist) {
            if (controllerExists) {
                level.setBlockAndUpdate(controllerPos, Blocks.AIR.defaultBlockState());
            }
            return;
        }

        CrushingWheelControllerBlock controller = controllerBlock();
        if (!controllerExists) {
            if (!controllerState.canBeReplaced()) {
                return;
            }
            level.setBlockAndUpdate(controllerPos, controller.defaultBlockState()
                    .setValue(VALID, controllerShouldBeValid)
                    .setValue(FACING, controllerNewDirection));
        } else if (controllerIsValid != controllerShouldBeValid
                || controllerOldDirection != controllerNewDirection) {
            level.setBlockAndUpdate(controllerPos, controllerState
                    .setValue(VALID, controllerShouldBeValid)
                    .setValue(FACING, controllerNewDirection));
        }

        controller.updateSpeed(level.getBlockState(controllerPos), level, controllerPos);
    }
}
