package electrolyte.greate.content.kinetics.gearbox;

import com.simibubi.create.content.kinetics.base.IRotate;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredBlock;
import net.createmod.catnip.data.Iterate;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TieredVerticalGearboxItem extends BlockItem {

    @Nullable
    private String descriptionId;

    public TieredVerticalGearboxItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public String getDescriptionId() {
        if (descriptionId == null) {
            descriptionId = Util.makeDescriptionId("item", BuiltInRegistries.ITEM.getKey(this));
        }
        return descriptionId;
    }

    @Override
    public void registerBlocks(Map<Block, Item> blockToItemMap, Item item) {
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player,
                                                ItemStack stack, BlockState state) {
        Axis inferred = null;
        for (Direction direction : Iterate.horizontalDirections) {
            BlockPos neighbourPos = pos.relative(direction);
            BlockState neighbour = level.getBlockState(neighbourPos);
            if (!(neighbour.getBlock() instanceof IRotate rotate)) {
                continue;
            }
            if (!rotate.hasShaftTowards(level, neighbourPos, neighbour, direction.getOpposite())) {
                continue;
            }
            if (inferred != null && inferred != direction.getAxis()) {
                inferred = null;
                break;
            }
            inferred = direction.getAxis();
        }

        Axis axis;
        if (inferred == null) {
            axis = player == null ? Axis.Z : player.getDirection().getClockWise().getAxis();
        } else {
            axis = inferred == Axis.X ? Axis.Z : Axis.X;
        }
        level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.AXIS, axis));
        return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
    }

    public int getTier() {
        return getBlock() instanceof ITieredBlock tiered ? tiered.getTier() : -1;
    }
}
