package electrolyte.greate.content.kinetics.belt.item;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractShaftBlock;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.tterrag.registrate.util.entry.BlockEntry;
import electrolyte.greate.content.kinetics.belt.ITieredBelt;
import electrolyte.greate.content.kinetics.belt.TieredBeltBlock;
import electrolyte.greate.content.kinetics.simpleRelays.TieredBracketedKineticBlockEntity;
import electrolyte.greate.content.kinetics.simpleRelays.TieredShaftBlock;
import electrolyte.greate.infrastructure.config.GreateConfig;
import electrolyte.greate.registry.Belts;
import electrolyte.greate.registry.GreateMaterials;
import electrolyte.greate.registry.GreateTagPrefixes;
import electrolyte.greate.registry.GreateTiers;
import electrolyte.greate.registry.Shafts;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TieredBeltConnectorItem extends Item implements ITieredBelt {

    private final Material beltMaterial;
    private final int polymerIndex;

    public TieredBeltConnectorItem(Properties properties, Material beltMaterial, int polymerIndex) {
        super(properties);
        this.beltMaterial = beltMaterial;
        this.polymerIndex = polymerIndex;
    }

    @Override
    public Material getBeltMaterial() {
        return beltMaterial;
    }

    public int getPolymerIndex() {
        return polymerIndex;
    }

    public int getMaxLength() {
        return GreateConfig.beltLength(polymerIndex);
    }

    public List<BlockEntry<TieredShaftBlock>> getValidShafts() {
        List<BlockEntry<TieredShaftBlock>> result = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            int tier = polymerIndex * 2 + i;
            if (tier >= GreateTiers.TIER_COUNT) {
                continue;
            }
            Material material = GreateMaterials.forTier(GreateTiers.MATERIALS.get(tier).id());
            if (material == null) {
                continue;
            }
            BlockEntry<TieredShaftBlock> entry = Shafts.TABLE.get(GreateTagPrefixes.shaft, material);
            if (entry != null) {
                result.add(entry);
            }
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.translatable("greate_remake.tooltip.belt_maxlength", getMaxLength())
                .withStyle(ChatFormatting.GRAY));

        List<BlockEntry<TieredShaftBlock>> shafts = getValidShafts();
        if (shafts.size() == 2) {
            MutableComponent first = Component.translatable(shafts.get(0).get().getDescriptionId())
                    .withStyle(Style.EMPTY.withColor(GTValues.VC[shafts.get(0).get().getTier()]));
            MutableComponent second = Component.translatable(shafts.get(1).get().getDescriptionId())
                    .withStyle(Style.EMPTY.withColor(GTValues.VC[shafts.get(1).get().getTier()]));
            tooltip.add(Component.translatable("greate_remake.tooltip.belt_usable", first, second)
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        ItemStack heldStack = context.getItemInHand();
        if (player != null && player.isShiftKeyDown()) {
            heldStack.remove(AllDataComponents.BELT_FIRST_SHAFT);
            return InteractionResult.SUCCESS;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        boolean validAxis = validateAxis(level, pos);
        if (level.isClientSide) {
            return validAxis ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }

        BlockPos firstPulley = null;
        if (heldStack.has(AllDataComponents.BELT_FIRST_SHAFT)) {
            firstPulley = heldStack.get(AllDataComponents.BELT_FIRST_SHAFT);
            if (!validateAxis(level, firstPulley)
                    || !firstPulley.closerThan(pos, getMaxLength() * 2)) {
                heldStack.remove(AllDataComponents.BELT_FIRST_SHAFT);
            }
        }

        if (!validAxis || player == null) {
            return InteractionResult.FAIL;
        }

        if (heldStack.has(AllDataComponents.BELT_FIRST_SHAFT)) {
            if (!canConnect(level, firstPulley, pos, heldStack)) {
                return InteractionResult.FAIL;
            }
            if (firstPulley != null && !firstPulley.equals(pos)) {
                createBelts(level, firstPulley, pos);
                AllAdvancements.BELT.awardTo(player);
                if (!player.isCreative()) {
                    context.getItemInHand().shrink(1);
                }
            }

            if (!context.getItemInHand().isEmpty()) {
                heldStack.remove(AllDataComponents.BELT_FIRST_SHAFT);
                player.getCooldowns().addCooldown(this, 5);
            }
            return InteractionResult.SUCCESS;
        }

        heldStack.set(AllDataComponents.BELT_FIRST_SHAFT, pos);
        player.getCooldowns().addCooldown(this, 5);
        return InteractionResult.SUCCESS;
    }

    public void createBelts(Level level, BlockPos start, BlockPos end) {
        level.playSound(null, BlockPos.containing(VecHelper.getCenterOf(start.offset(end)).scale(0.5F)),
                SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.5F, 1F);

        BeltSlope slope = getSlopeBetween(start, end);
        Direction facing = getFacingFromTo(start, end);
        BlockPos diff = end.subtract(start);
        if (diff.getX() == diff.getZ()) {
            facing = Direction.get(facing.getAxisDirection(),
                    level.getBlockState(start).getValue(BlockStateProperties.AXIS) == Axis.X ? Axis.Z : Axis.X);
        }

        int tier = ((TieredShaftBlock) level.getBlockState(start).getBlock()).getTier();
        BlockState defaultBelt = Belts.ALL.get(tier).getDefaultState();

        List<BlockPos> beltsToCreate = getBeltChainBetween(start, end, slope, facing);
        boolean failed = false;
        for (BlockPos pos : beltsToCreate) {
            BlockState existingState = level.getBlockState(pos);
            if (existingState.getDestroySpeed(level, pos) == -1) {
                failed = true;
                break;
            }

            BeltPart part = pos.equals(start) ? BeltPart.START : pos.equals(end) ? BeltPart.END : BeltPart.MIDDLE;
            BlockState shaftState = level.getBlockState(pos);
            boolean pulley = shaftState.getBlock() instanceof TieredShaftBlock;
            if (part == BeltPart.MIDDLE && pulley) {
                part = BeltPart.PULLEY;
            }
            if (pulley && shaftState.getValue(AbstractShaftBlock.AXIS) == Axis.Y) {
                slope = BeltSlope.SIDEWAYS;
            }
            if (!existingState.canBeReplaced()) {
                level.destroyBlock(pos, false);
            }
            KineticBlockEntity.switchToBlockState(level, pos, ProperWaterloggedBlock.withWater(level, defaultBelt
                    .setValue(BeltBlock.SLOPE, slope)
                    .setValue(BeltBlock.PART, part)
                    .setValue(BeltBlock.HORIZONTAL_FACING, facing), pos));
        }

        if (!failed) {
            return;
        }

        for (BlockPos pos : beltsToCreate) {
            if (level.getBlockState(pos).getBlock() == defaultBelt.getBlock()) {
                level.destroyBlock(pos, false);
            }
        }
    }

    private static Direction getFacingFromTo(BlockPos start, BlockPos end) {
        Axis beltAxis = start.getX() == end.getX() ? Axis.Z : Axis.X;
        BlockPos diff = end.subtract(start);
        AxisDirection dir;
        if (diff.getX() == 0 && diff.getZ() == 0) {
            dir = diff.getY() > 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE;
        } else {
            dir = beltAxis.choose(diff.getX(), 0, diff.getZ()) > 0
                    ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE;
        }
        return Direction.get(dir, beltAxis);
    }

    private static BeltSlope getSlopeBetween(BlockPos start, BlockPos end) {
        BlockPos diff = end.subtract(start);
        if (diff.getY() != 0) {
            if (diff.getZ() != 0 || diff.getX() != 0) {
                return diff.getY() > 0 ? BeltSlope.UPWARD : BeltSlope.DOWNWARD;
            }
            return BeltSlope.VERTICAL;
        }
        return BeltSlope.HORIZONTAL;
    }

    private static List<BlockPos> getBeltChainBetween(BlockPos start, BlockPos end, BeltSlope slope, Direction dir) {
        List<BlockPos> positions = new LinkedList<>();
        int limit = 1000;
        BlockPos current = start;
        do {
            positions.add(current);
            if (slope == BeltSlope.VERTICAL) {
                current = current.above(dir.getAxisDirection() == AxisDirection.POSITIVE ? 1 : -1);
                continue;
            }
            current = current.relative(dir);
            if (slope != BeltSlope.HORIZONTAL) {
                current = current.above(slope == BeltSlope.UPWARD ? 1 : -1);
            }
        } while (!current.equals(end) && limit-- > 0);
        positions.add(end);
        return positions;
    }

    public static boolean canConnect(Level level, BlockPos first, BlockPos second, ItemStack heldStack) {
        return connectFailureReason(level, first, second, heldStack) == null;
    }

    public static String connectFailureReason(Level level, BlockPos first, BlockPos second, ItemStack heldStack) {
        if (first == null || second == null) {
            return "missing endpoint position";
        }
        if (!level.isLoaded(first) || !level.isLoaded(second)) {
            return "endpoint chunk not loaded";
        }
        if (!(heldStack.getItem() instanceof TieredBeltConnectorItem connector)) {
            return "held item is not a tiered belt connector";
        }
        if (!second.closerThan(first, connector.getMaxLength())) {
            return "distance " + Math.sqrt(second.distSqr(first)) + " > max length " + connector.getMaxLength();
        }

        BlockState firstState = level.getBlockState(first);
        BlockState secondState = level.getBlockState(second);
        if (!(firstState.getBlock() instanceof TieredShaftBlock)) {
            return "first endpoint is not a tiered shaft: " + firstState;
        }
        if (!secondState.hasProperty(BlockStateProperties.AXIS)) {
            return "second endpoint has no AXIS property: " + secondState;
        }
        Axis shaftAxis = firstState.getValue(BlockStateProperties.AXIS);
        if (shaftAxis != secondState.getValue(BlockStateProperties.AXIS)) {
            return "shaft axes differ: " + shaftAxis + " vs " + secondState.getValue(BlockStateProperties.AXIS);
        }

        BlockPos diff = second.subtract(first);
        int x = diff.getX();
        int y = diff.getY();
        int z = diff.getZ();
        int sames = ((Math.abs(x) == Math.abs(y)) ? 1 : 0)
                + ((Math.abs(y) == Math.abs(z)) ? 1 : 0)
                + ((Math.abs(z) == Math.abs(x)) ? 1 : 0);
        if (shaftAxis.choose(x, y, z) != 0) {
            return "endpoints differ along the shaft axis (" + shaftAxis + "), diff=" + diff;
        }
        if (sames != 1) {
            return "endpoints are not on a straight line, diff=" + diff + " sames=" + sames;
        }
        if (shaftAxis == Axis.Y && x != 0 && z != 0) {
            return "vertical shaft belt cannot run diagonally";
        }

        BlockEntity be = level.getBlockEntity(first);
        BlockEntity be2 = level.getBlockEntity(second);
        if (!(be instanceof TieredBracketedKineticBlockEntity kbe)) {
            return "first BE is not a tiered kinetic BE: " + (be == null ? "null" : be.getClass().getName());
        }
        if (!(be2 instanceof TieredBracketedKineticBlockEntity kbe2)) {
            return "second BE is not a tiered kinetic BE: " + (be2 == null ? "null" : be2.getClass().getName());
        }
        if (firstState.getBlock() != secondState.getBlock()) {
            return "endpoint shaft blocks differ: " + firstState.getBlock() + " vs " + secondState.getBlock();
        }

        List<Block> shafts = new ArrayList<>();
        connector.getValidShafts().forEach(s -> shafts.add(s.get()));
        if (!shafts.contains(firstState.getBlock())) {
            return "first shaft " + firstState.getBlock() + " is not in connector's valid list " + shafts;
        }
        if (!shafts.contains(secondState.getBlock())) {
            return "second shaft " + secondState.getBlock() + " is not in connector's valid list " + shafts;
        }

        float speed = kbe.getTheoreticalSpeed();
        float speed2 = kbe2.getTheoreticalSpeed();
        if (Math.signum(speed) != Math.signum(speed2) && speed != 0 && speed2 != 0) {
            return "endpoint shafts spin in opposite directions: " + speed + " vs " + speed2;
        }

        BlockPos step = BlockPos.containing(Math.signum(x), Math.signum(y), Math.signum(z));
        int limit = 1000;
        for (BlockPos pos = first.offset(step); !pos.equals(second) && limit-- > 0; pos = pos.offset(step)) {
            BlockState blockState = level.getBlockState(pos);
            if (blockState.getBlock() instanceof TieredShaftBlock
                    && blockState.getValue(AbstractSimpleShaftBlock.AXIS) == shaftAxis) {
                if (firstState.getBlock() != blockState.getBlock()) {
                    return "obstructed by a different-tier shaft at " + pos + ": " + blockState.getBlock();
                }
                continue;
            }
            if (!blockState.canBeReplaced()) {
                return "obstructed at " + pos + " by " + blockState;
            }
        }
        return null;
    }

    public static boolean validateAxis(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return false;
        }
        return level.getBlockState(pos).getBlock() instanceof TieredShaftBlock;
    }
}
