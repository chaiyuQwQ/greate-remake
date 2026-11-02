package electrolyte.greate.content.kinetics.belt;

import com.gregtechceu.gtceu.api.material.ChemicalHelper;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.BeltSlicer.Feedback;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractShaftBlock;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.content.schematics.requirement.ItemRequirement.ItemUseType;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import electrolyte.greate.content.kinetics.belt.item.TieredBeltConnectorItem;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredBlock;
import electrolyte.greate.content.kinetics.simpleRelays.TieredShaftBlock;
import electrolyte.greate.registry.Belts;
import electrolyte.greate.registry.GreateBlockEntityTypes;
import electrolyte.greate.registry.GreateMaterials;
import electrolyte.greate.registry.GreateTagPrefixes;
import electrolyte.greate.registry.GreateTiers;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Belt without an item form: placed by {@link TieredBeltConnectorItem} between two shafts of
 * the same tier, and dropping the connector (plus a shaft where a pulley sits) when broken.
 */
public class TieredBeltBlock extends BeltBlock implements ITieredBlock, ITieredBelt {

    private final int tier;
    private final Material beltMaterial;

    public TieredBeltBlock(Properties properties, int tier, Material beltMaterial) {
        super(properties);
        this.tier = tier;
        this.beltMaterial = beltMaterial;
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public Material getBeltMaterial() {
        return beltMaterial;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos,
                                       Player player) {
        return ChemicalHelper.get(GreateTagPrefixes.beltConnector, beltMaterial);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = new ArrayList<>(2);
        drops.add(ChemicalHelper.get(GreateTagPrefixes.beltConnector, beltMaterial));
        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof TieredBeltBlockEntity belt && belt.hasPulley()) {
            drops.add(shaftStack());
        }
        return drops;
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity blockEntity) {
        List<ItemStack> required = new ArrayList<>(2);
        if (state.getValue(PART) != BeltPart.MIDDLE) {
            required.add(shaftStack());
        }
        if (state.getValue(PART) == BeltPart.START) {
            required.add(ChemicalHelper.get(GreateTagPrefixes.beltConnector, beltMaterial));
        }
        if (required.isEmpty()) {
            return ItemRequirement.NONE;
        }
        return new ItemRequirement(ItemUseType.CONSUME, required);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown() || !player.mayBuild()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (stack.getItem() instanceof TieredBeltConnectorItem connector) {
            if (connector.getBeltMaterial() == beltMaterial) {
                return TieredBeltSlicer.useConnector(state, level, pos, player, hand, hitResult, new Feedback());
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (Block.byItem(stack.getItem()) instanceof TieredShaftBlock shaftBlock) {
            if (shaftBlock.getTier() != tier) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (state.getValue(PART) != BeltPart.MIDDLE) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            }
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            KineticBlockEntity.switchToBlockState(level, pos, state.setValue(PART, BeltPart.PULLEY));
            return ItemInteractionResult.SUCCESS;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public net.minecraft.world.InteractionResult onWrenched(BlockState state, UseOnContext context) {

        if (state.getValue(PART) == BeltPart.PULLEY) {
            Level world = context.getLevel();
            if (!world.isClientSide) {
                KineticBlockEntity.switchToBlockState(world, context.getClickedPos(),
                        state.setValue(PART, BeltPart.MIDDLE));
                Player player = context.getPlayer();
                if (player != null && !player.isCreative()) {
                    player.getInventory().placeItemBackInInventory(shaftStack());
                }
            }
            return net.minecraft.world.InteractionResult.SUCCESS;
        }

        return super.onWrenched(state, context);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);

        if (level.isClientSide) {
            return;
        }
        if (state.getBlock() == newState.getBlock()) {
            return;
        }
        if (isMoving) {
            return;
        }

        for (boolean forward : Iterate.trueAndFalse) {
            BlockPos currentPos = nextSegmentPosition(state, pos, forward);
            if (currentPos == null) {
                continue;
            }
            BlockState currentState = level.getBlockState(currentPos);
            if (!(currentState.getBlock() instanceof TieredBeltBlock)) {
                continue;
            }

            boolean hasPulley = false;
            BlockEntity be = level.getBlockEntity(currentPos);
            if (be instanceof TieredBeltBlockEntity belt) {
                if (belt.isController()) {
                    belt.getInventory().ejectAll();
                }
                hasPulley = belt.hasPulley();
            }

            level.removeBlockEntity(currentPos);
            BlockState shaftState = ChemicalHelper.getBlock(GreateTagPrefixes.shaft, tierMaterial())
                    .defaultBlockState().setValue(AbstractShaftBlock.AXIS, getRotationAxis(currentState));
            level.setBlock(currentPos, ProperWaterloggedBlock.withWater(level,
                    hasPulley ? shaftState : Blocks.AIR.defaultBlockState(), currentPos), Block.UPDATE_ALL);
            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, currentPos, Block.getId(currentState));
        }
    }

    @Override
    public BlockEntityType<? extends TieredBeltBlockEntity> getBlockEntityType() {
        return GreateBlockEntityTypes.TIERED_BELT.get();
    }

    public Material tierMaterial() {
        return GreateMaterials.forTier(GreateTiers.MATERIALS.get(tier).id());
    }

    public ItemStack shaftStack() {
        return ChemicalHelper.get(GreateTagPrefixes.shaft, tierMaterial());
    }

    public int maxLength() {
        return Belts.maxLength(tier);
    }

    public boolean usesPolymer(Material material) {
        return beltMaterial == material;
    }
}
