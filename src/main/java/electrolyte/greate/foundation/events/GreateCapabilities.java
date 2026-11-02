package electrolyte.greate.foundation.events;

import electrolyte.greate.Greate;
import electrolyte.greate.registry.GreateBlockEntityTypes;
import net.minecraft.core.Direction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = Greate.MOD_ID)
public final class GreateCapabilities {

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                GreateBlockEntityTypes.TIERED_MILLSTONE.get(),
                (be, side) -> be.capability);

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                GreateBlockEntityTypes.TIERED_CRUSHING_WHEEL_CONTROLLER.get(),
                (be, side) -> be.inventory);

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                GreateBlockEntityTypes.TIERED_BELT.get(),
                (be, side) -> be.getBeltItemHandler());

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                GreateBlockEntityTypes.TIERED_SAW.get(),
                (be, side) -> side != Direction.DOWN ? be.inventory : null);
    }

    private GreateCapabilities() {
    }
}
