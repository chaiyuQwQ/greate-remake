package electrolyte.greate.foundation.events;

import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import electrolyte.greate.Greate;
import electrolyte.greate.content.kinetics.base.TieredSingleAxisRotatingVisual;
import electrolyte.greate.content.kinetics.belt.TieredBeltVisual;
import electrolyte.greate.content.kinetics.belt.item.TieredBeltConnectorHandler;
import electrolyte.greate.content.kinetics.fan.TieredFanVisual;
import electrolyte.greate.content.kinetics.gearbox.TieredGearboxVisual;
import electrolyte.greate.content.kinetics.mixer.TieredMixerVisual;
import electrolyte.greate.content.kinetics.press.TieredPressVisual;
import electrolyte.greate.content.kinetics.saw.TieredSawVisual;
import electrolyte.greate.content.kinetics.simpleRelays.TieredBracketedKineticBlockEntityVisual;
import electrolyte.greate.infrastructure.ponder.GreatePonderScenes;
import electrolyte.greate.registry.Belts;
import electrolyte.greate.registry.GreateBlockEntityTypes;
import electrolyte.greate.registry.GreatePartialModels;
import electrolyte.greate.registry.GreateSpriteShifts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = Greate.MOD_ID, value = Dist.CLIENT)
public final class GreateClientEvents {

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        GreatePartialModels.register();
        for (var id : GreatePartialModels.allModelIds()) {
            event.register(ModelResourceLocation.standalone(id));
        }
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

        GreatePartialModels.register();

        net.createmod.ponder.foundation.PonderIndex.addPlugin(
                new electrolyte.greate.infrastructure.ponder.GreatePonderPlugin());

        event.enqueueWork(() -> {

            Belts.ALL.forEach(entry -> GreateSpriteShifts.populate(entry.get()));

            SimpleBlockEntityVisualizer
                    .builder(GreateBlockEntityTypes.TIERED_BRACKETED_KINETIC.get())
                    .factory(TieredBracketedKineticBlockEntityVisual::create)

                    .skipVanillaRender(be -> true)
                    .apply();

            SimpleBlockEntityVisualizer
                    .builder(GreateBlockEntityTypes.TIERED_MILLSTONE.get())
                    .factory(TieredSingleAxisRotatingVisual::millstoneCog)
                    .skipVanillaRender(be -> true)
                    .apply();

            SimpleBlockEntityVisualizer
                    .builder(GreateBlockEntityTypes.TIERED_MECHANICAL_PRESS.get())
                    .factory(TieredPressVisual::new)
                    .skipVanillaRender(be -> true)
                    .apply();

            SimpleBlockEntityVisualizer
                    .builder(GreateBlockEntityTypes.TIERED_GEARBOX.get())
                    .factory(TieredGearboxVisual::new)
                    .skipVanillaRender(be -> true)
                    .apply();

            SimpleBlockEntityVisualizer
                    .builder(GreateBlockEntityTypes.TIERED_MECHANICAL_MIXER.get())
                    .factory(TieredMixerVisual::new)
                    .skipVanillaRender(be -> true)
                    .apply();

            SimpleBlockEntityVisualizer
                    .builder(GreateBlockEntityTypes.TIERED_MECHANICAL_PUMP.get())
                    .factory(TieredSingleAxisRotatingVisual::pumpCog)
                    .skipVanillaRender(be -> true)
                    .apply();

            SimpleBlockEntityVisualizer
                    .builder(GreateBlockEntityTypes.TIERED_CRUSHING_WHEEL.get())
                    .factory(TieredSingleAxisRotatingVisual::crushingWheel)
                    .skipVanillaRender(be -> true)
                    .apply();

            SimpleBlockEntityVisualizer
                    .builder(GreateBlockEntityTypes.TIERED_ENCASED_FAN.get())
                    .factory(TieredFanVisual::new)
                    .skipVanillaRender(be -> true)
                    .apply();

            SimpleBlockEntityVisualizer
                    .builder(GreateBlockEntityTypes.TIERED_BELT.get())
                    .factory(TieredBeltVisual::new)
                    .skipVanillaRender(be -> !be.shouldRenderNormally())
                    .apply();

            SimpleBlockEntityVisualizer
                    .builder(GreateBlockEntityTypes.TIERED_SAW.get())
                    .factory(TieredSawVisual::new)
                    .skipVanillaRender(be -> false)
                    .apply();
        });
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {
            TieredBeltConnectorHandler.tick();
        }
        if (!ponderCoverageLogged) {

            ponderCoverageLogged = true;
            GreatePonderScenes.logCoverage();
        }
    }

    private static boolean ponderCoverageLogged;

    private GreateClientEvents() {
    }
}
