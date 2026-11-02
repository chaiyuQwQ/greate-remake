package electrolyte.greate.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import electrolyte.greate.GreateValues;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredBlock;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(value = KineticNetwork.class, remap = false)
public abstract class MixinKineticNetwork {

    @Shadow(remap = false)
    public Map<KineticBlockEntity, Float> members;

    @ModifyReturnValue(method = "calculateCapacity", at = @At("RETURN"), remap = false)
    private float greate$clampToTierCapacity(float originalCapacity) {
        float tierCapacity = Float.MAX_VALUE;
        for (KineticBlockEntity be : members.keySet()) {

            if (be == null || be.isRemoved()) {
                continue;
            }
            Block block = be.getBlockState().getBlock();
            if (block instanceof ITieredBlock tiered) {
                tierCapacity = Math.min(tierCapacity, GreateValues.maxCapacity(tiered.getTier()));
            }
        }
        return Math.min(originalCapacity, tierCapacity);
    }
}
