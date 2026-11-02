package electrolyte.greate.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredKineticBlockEntity;
import electrolyte.greate.foundation.recipe.GreateSawingRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = SawBlockEntity.class, remap = false)
public abstract class MixinSawBlockEntity {

    @Shadow(remap = false)
    public ProcessingInventory inventory;

    @Shadow(remap = false)
    private FilteringBehaviour filtering;

    @ModifyReturnValue(method = "getRecipes", at = @At("RETURN"), remap = false)
    private List<RecipeHolder<? extends Recipe<?>>> greate$applyTieredSawing(
            List<RecipeHolder<? extends Recipe<?>>> original) {
        if (!((Object) this instanceof ITieredKineticBlockEntity tiered)) {
            return original;
        }
        SawBlockEntity self = (SawBlockEntity) (Object) this;
        ItemStack input = inventory.getStackInSlot(0);
        return GreateSawingRecipes.gate(self.getLevel(), tiered.getTier(), input,
                stack -> filtering == null || filtering.test(stack), original);
    }
}
