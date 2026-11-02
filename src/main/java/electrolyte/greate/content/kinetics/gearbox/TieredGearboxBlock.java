package electrolyte.greate.content.kinetics.gearbox;

import com.simibubi.create.content.kinetics.gearbox.GearboxBlock;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlockEntity;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredBlock;
import electrolyte.greate.registry.GreateBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TieredGearboxBlock extends GearboxBlock implements ITieredBlock {

    private final int tier;

    @Nullable
    private Item verticalItem;

    public TieredGearboxBlock(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public BlockEntityType<? extends GearboxBlockEntity> getBlockEntityType() {
        return GreateBlockEntityTypes.TIERED_GEARBOX.get();
    }

    public void setVerticalItem(Item item) {
        this.verticalItem = item;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        Item item = itemFor(state);
        return item == null ? super.getDrops(state, builder) : List.of(new ItemStack(item));
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos,
                                       Player player) {
        Item item = itemFor(state);
        return item == null ? super.getCloneItemStack(state, target, level, pos, player) : new ItemStack(item);
    }

    @Nullable
    private Item itemFor(BlockState state) {
        if (state.getValue(AXIS).isVertical()) {
            return blockItem();
        }
        return verticalItem;
    }

    public boolean hasVerticalItem() {
        return verticalItem != null;
    }

    @Nullable
    private Item blockItem() {
        Item item = asItem();
        return item == net.minecraft.world.item.Items.AIR ? null : item;
    }
}
