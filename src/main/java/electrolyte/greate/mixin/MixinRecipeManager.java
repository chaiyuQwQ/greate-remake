package electrolyte.greate.mixin;

import com.google.gson.JsonElement;
import electrolyte.greate.foundation.recipe.GreateRecipeRemoval;
import electrolyte.greate.infrastructure.config.GreateConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = RecipeManager.class, priority = 1099)
public abstract class MixinRecipeManager {

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;"
            + "Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At("HEAD"), remap = false)
    private void greate$removeHardCreateRecipes(Map<ResourceLocation, JsonElement> recipes,
                                                ResourceManager resourceManager, ProfilerFiller profiler,
                                                CallbackInfo ci) {
        if (GreateConfig.hardCreateRecipes()) {
            GreateRecipeRemoval.apply(recipes::remove);
        }
    }
}
