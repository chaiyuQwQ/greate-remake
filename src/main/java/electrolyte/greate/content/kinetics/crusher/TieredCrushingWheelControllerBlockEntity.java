package electrolyte.greate.content.kinetics.crusher;

import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredBlock;
import electrolyte.greate.foundation.recipe.GreateCrushingRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.Optional;

public class TieredCrushingWheelControllerBlockEntity extends CrushingWheelControllerBlockEntity {

    public TieredCrushingWheelControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public int getTier() {
        BlockState state = getBlockState();
        return state.getBlock() instanceof ITieredBlock tiered ? tiered.getTier() : -1;
    }

    @Override
    public Optional<RecipeHolder<StandardProcessingRecipe<RecipeWrapper>>> findRecipe() {
        if (level == null) {
            return Optional.empty();
        }
        ItemStack input = inventory.getStackInSlot(0);
        GreateCrushingRecipes.Resolution resolution =
                GreateCrushingRecipes.resolve(level, input, getTier());

        if (resolution.blocked()) {
            return Optional.empty();
        }
        if (resolution.found()) {
            @SuppressWarnings("unchecked")
            RecipeHolder<StandardProcessingRecipe<RecipeWrapper>> holder =
                    (RecipeHolder<StandardProcessingRecipe<RecipeWrapper>>) (RecipeHolder<?>) resolution.recipe();
            return Optional.of(holder);
        }

        return super.findRecipe();
    }
}
