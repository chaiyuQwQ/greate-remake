package electrolyte.greate.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessing;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import electrolyte.greate.content.kinetics.fan.TieredFanProcessing;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredKineticBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

/**
 * Carries the fan tier into Create's fan-processing calls, which only receive the stack.
 *
 * <p>The tier flows through {@link TieredFanProcessing#withTier}; {@code -1} means unknown
 * (a vanilla Create fan) and every gated branch stays permissive. On the belt/handler path
 * the call happens inside a lambda, so the callback is wrapped instead of the call itself.</p>
 */
@Mixin(value = AirCurrent.class, remap = false)
public abstract class MixinAirCurrent {

    @Shadow
    @Final
    public IAirCurrentSource source;

    private static final String CAN_PROCESS_ITEM = "Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessing;"
            + "canProcess(Lnet/minecraft/world/entity/item/ItemEntity;"
            + "Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;)Z";

    private static final String APPLY_PROCESSING_ENTITY = "Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessing;"
            + "applyProcessing(Lnet/minecraft/world/entity/item/ItemEntity;"
            + "Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;)Z";

    private static final String HANDLE_ALL_ITEMS = "Lcom/simibubi/create/content/kinetics/belt/behaviour/"
            + "TransportedItemStackHandlerBehaviour;"
            + "handleProcessingOnAllItems(Ljava/util/function/Function;)V";

    @WrapOperation(method = "tickAffectedEntities",
            at = @At(value = "INVOKE", target = CAN_PROCESS_ITEM, ordinal = 0), remap = false)
    private boolean greate$gateItemEntity(ItemEntity entity, FanProcessingType type, Operation<Boolean> original) {
        int tier = greate$fanTier();
        if (tier < 0) {
            return original.call(entity, type);
        }

        return TieredFanProcessing.withTier(tier,
                () -> TieredFanProcessing.canProcess(entity.getItem(), tier) && original.call(entity, type));
    }

    @WrapOperation(method = "tickAffectedEntities",
            at = @At(value = "INVOKE", target = APPLY_PROCESSING_ENTITY, ordinal = 0), remap = false)
    private boolean greate$applyItemEntity(ItemEntity entity, FanProcessingType type, Operation<Boolean> original) {
        int tier = greate$fanTier();
        if (tier < 0) {
            return original.call(entity, type);
        }
        return TieredFanProcessing.withTier(tier, () -> original.call(entity, type));
    }

    @WrapOperation(method = "tickAffectedHandlers",
            at = @At(value = "INVOKE", target = HANDLE_ALL_ITEMS, ordinal = 0), remap = false)
    private void greate$gateTransportedItems(TransportedItemStackHandlerBehaviour handler,
                                             Function<TransportedItemStack, TransportedResult> callback,
                                             Operation<Void> original) {
        int tier = greate$fanTier();
        if (tier < 0) {
            original.call(handler, callback);
            return;
        }
        original.call(handler, (Function<TransportedItemStack, TransportedResult>) transported ->
                TieredFanProcessing.canProcess(transported.stack, tier)
                        ? TieredFanProcessing.withTier(tier, () -> callback.apply(transported))
                        : TransportedResult.doNothing());
    }

    private int greate$fanTier() {
        return source instanceof ITieredKineticBlockEntity tiered ? tiered.getTier() : -1;
    }
}
