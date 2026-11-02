package electrolyte.greate.content.kinetics.press;

import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredKineticBlockEntity;
import electrolyte.greate.foundation.recipe.GreateMillingRecipes;
import electrolyte.greate.foundation.recipe.GreatePressingRecipes;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class TieredMechanicalPressBlockEntity extends MechanicalPressBlockEntity
        implements ITieredKineticBlockEntity {

    private ScrollValueBehaviour targetCircuit;

    public TieredMechanicalPressBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        targetCircuit = new ScrollValueBehaviour(
                Component.translatable("greate_remake.tooltip.circuit_number"), this, new CircuitValueBoxTransform());
        targetCircuit.between(0, 32);
        behaviours.add(targetCircuit);
    }

    public int getCircuitNumber() {
        return targetCircuit == null ? -1 : targetCircuit.getValue();
    }

    @Override
    public Optional<RecipeHolder<PressingRecipe>> getRecipe(ItemStack item) {
        return Optional.ofNullable(GreatePressingRecipes.find(level, item, getTier(), getCircuitNumber()));
    }

    @Override
    protected boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> recipe) {
        if (recipe.value() instanceof TieredPressingRecipe tiered) {

            return GreateMillingRecipes.circuitMatches(tiered.getCircuitNumber(), getCircuitNumber())
                    && tiered.getRecipeTier() <= getTier();
        }
        return super.matchStaticFilters(recipe);
    }

    private class CircuitValueBoxTransform extends ValueBoxTransform.Sided {

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 9f, 15.5f);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            if (direction.getAxis().isVertical()) {
                return false;
            }
            return !((TieredMechanicalPressBlock) state.getBlock())
                    .hasShaftTowards(level, getBlockPos(), state, direction);
        }
    }
}
