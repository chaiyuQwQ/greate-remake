package electrolyte.greate.content.kinetics.belt;

import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.transport.BeltMovementHandler;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Create's belt entity hard-codes {@code AllBlocks.BELT} in {@code tick()} and
 * {@code hasPulley()}; both must be overridden or the belt stops moving. Its item handler is
 * registered in {@code GreateCapabilities}.
 */
public class TieredBeltBlockEntity extends BeltBlockEntity implements ITieredKineticBlockEntity {

    public TieredBeltBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {

        super.tick();

        if (!(level.getBlockState(worldPosition).getBlock() instanceof TieredBeltBlock)) {
            return;
        }

        initializeItemHandler();

        if (!isController()) {
            return;
        }

        invalidateRenderBoundingBox();
        getInventory().tick();

        if (getSpeed() == 0) {
            return;
        }

        if (passengers == null) {
            passengers = new HashMap<>();
        }
        List<Entity> toRemove = new ArrayList<>();
        passengers.forEach((entity, info) -> {
            boolean canBeTransported = BeltMovementHandler.canBeTransported(entity);
            boolean leftTheBelt = info.getTicksSinceLastCollision()
                    > (getBlockState().getValue(BeltBlock.SLOPE) != com.simibubi.create.content.kinetics.belt.BeltSlope.HORIZONTAL ? 3 : 1);
            if (!canBeTransported || leftTheBelt) {
                toRemove.add(entity);
                return;
            }
            info.tick();
            BeltMovementHandler.transportEntity(this, entity, info);
        });
        toRemove.forEach(passengers::remove);
    }

    @Override
    public boolean hasPulley() {
        if (!(getBlockState().getBlock() instanceof TieredBeltBlock)) {
            return false;
        }
        return getBlockState().getValue(BeltBlock.PART) != BeltPart.MIDDLE;
    }

    public @Nullable IItemHandler getBeltItemHandler() {
        if (!BeltBlock.canTransportObjects(getBlockState())) {
            return null;
        }
        if (!isRemoved() && itemHandler == null) {
            initializeItemHandler();
        }
        return itemHandler;
    }

    public boolean hasItemHandler() {
        return itemHandler != null;
    }

    public @Nullable IItemHandler itemHandlerForDebug() {
        return itemHandler;
    }

    public boolean hasTransportedItems() {
        BeltBlockEntity controller = getControllerBE();
        return controller != null && controller.getInventory() != null
                && !controller.getInventory().getTransportedItems().isEmpty();
    }

    public int beltLengthForDebug() {
        return beltLength;
    }

    public List<ItemStack> transportedStacks() {
        BeltBlockEntity controller = getControllerBE();
        if (controller == null || controller.getInventory() == null) {
            return List.of();
        }
        return controller.getInventory().getTransportedItems().stream()
                .map(t -> t.stack)
                .toList();
    }
}
