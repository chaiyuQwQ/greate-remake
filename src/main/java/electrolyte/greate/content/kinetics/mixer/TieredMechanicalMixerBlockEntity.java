package electrolyte.greate.content.kinetics.mixer;

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TieredMechanicalMixerBlockEntity extends MechanicalMixerBlockEntity
        implements ITieredKineticBlockEntity {

    public TieredMechanicalMixerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> recipe) {
        if (recipe.value() instanceof TieredMixingRecipe tiered) {

            return tiered.getCircuitNumber() == -1 && tiered.getRecipeTier() <= getTier();
        }
        return super.matchStaticFilters(recipe);
    }

    public boolean allowsInBasin(RecipeHolder<? extends Recipe<?>> recipe) {
        return matchStaticFilters(recipe);
    }
}
