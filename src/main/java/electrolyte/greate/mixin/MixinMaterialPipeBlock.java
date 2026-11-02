package electrolyte.greate.mixin;

import com.gregtechceu.gtceu.api.block.MaterialPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MaterialPipeBlock.class, remap = false)
public class MixinMaterialPipeBlock {

    @Inject(method = "tinted", at = @At("RETURN"), cancellable = true, remap = false)
    private void greate$addTintAlpha(BlockState state, BlockAndTintGetter level, BlockPos pos, int index,
                                     CallbackInfoReturnable<Integer> cir) {
        int color = cir.getReturnValue();
        if (color != -1) {
            cir.setReturnValue(color | 0xFF000000);
        }
    }
}
