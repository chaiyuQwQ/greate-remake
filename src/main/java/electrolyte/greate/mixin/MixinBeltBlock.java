package electrolyte.greate.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Lets Create's belt logic accept any {@link BeltBlock} subclass.
 *
 * <p>Every {@code BlockEntry.has} call site needs an explicit {@code ordinal}: MixinExtras
 * only rewrites the first match otherwise, and one missed site in {@code getBeltChain} is
 * enough for {@code initBelt} to see a single-segment chain and destroy it.</p>
 */
@Mixin(value = BeltBlock.class, remap = false)
public abstract class MixinBeltBlock {

    private static final String HAS = "Lcom/tterrag/registrate/util/entry/BlockEntry;"
            + "has(Lnet/minecraft/world/level/block/state/BlockState;)Z";

    @WrapOperation(method = "canTransportObjects",
            at = @At(value = "INVOKE", target = HAS, ordinal = 0), remap = false)
    private static boolean greate$canTransportObjects(BlockEntry<?> instance, BlockState state,
                                                      Operation<Boolean> original) {
        return state.getBlock() instanceof BeltBlock;
    }

    @WrapOperation(method = "initBelt",
            at = @At(value = "INVOKE", target = HAS, ordinal = 0), remap = false)
    private static boolean greate$initBelt0(BlockEntry<?> instance, BlockState state, Operation<Boolean> original) {
        return state.getBlock() instanceof BeltBlock;
    }

    @WrapOperation(method = "initBelt",
            at = @At(value = "INVOKE", target = HAS, ordinal = 1), remap = false)
    private static boolean greate$initBelt1(BlockEntry<?> instance, BlockState state, Operation<Boolean> original) {
        return state.getBlock() instanceof BeltBlock;
    }

    @WrapOperation(method = "initBelt",
            at = @At(value = "INVOKE", target = HAS, ordinal = 2), remap = false)
    private static boolean greate$initBelt2(BlockEntry<?> instance, BlockState state, Operation<Boolean> original) {
        return state.getBlock() instanceof BeltBlock;
    }

    @WrapOperation(method = "getBeltChain",
            at = @At(value = "INVOKE", target = HAS, ordinal = 0), remap = false)
    private static boolean greate$getBeltChain0(BlockEntry<?> instance, BlockState state,
                                                Operation<Boolean> original) {
        return state.getBlock() instanceof BeltBlock;
    }

    @WrapOperation(method = "getBeltChain",
            at = @At(value = "INVOKE", target = HAS, ordinal = 1), remap = false)
    private static boolean greate$getBeltChain1(BlockEntry<?> instance, BlockState state,
                                                Operation<Boolean> original) {
        return state.getBlock() instanceof BeltBlock;
    }

    @WrapOperation(method = "updateEntityAfterFallOn",
            at = @At(value = "INVOKE", target = HAS, ordinal = 0), remap = false)
    private static boolean greate$updateEntityAfterFallOn0(BlockEntry<?> instance, BlockState state,
                                                           Operation<Boolean> original) {
        return state.getBlock() instanceof BeltBlock;
    }

    @WrapOperation(method = "updateEntityAfterFallOn",
            at = @At(value = "INVOKE", target = HAS, ordinal = 1), remap = false)
    private static boolean greate$updateEntityAfterFallOn1(BlockEntry<?> instance, BlockState state,
                                                           Operation<Boolean> original) {
        return state.getBlock() instanceof BeltBlock;
    }

    @WrapOperation(method = "isBlockCoveringBelt",
            at = @At(value = "INVOKE", target = HAS, ordinal = 0), remap = false)
    private static boolean greate$isBlockCoveringBelt(BlockEntry<?> instance, BlockState state,
                                                      Operation<Boolean> original) {

        return state.getBlock()
                instanceof com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock;
    }
}
