package electrolyte.greate.registry;

import com.simibubi.create.api.registry.CreateRegistries;
import electrolyte.greate.Greate;
import electrolyte.greate.content.kinetics.fan.TieredSplashingType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = Greate.MOD_ID)
public final class GreateFanProcessingTypes {

    public static final TieredSplashingType SPLASHING = new TieredSplashingType();

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(CreateRegistries.FAN_PROCESSING_TYPE)) {
            return;
        }
        event.register(CreateRegistries.FAN_PROCESSING_TYPE, Greate.id("splashing"), () -> SPLASHING);
        Greate.LOGGER.info("[Greate] 风处理类型已注册: greate_remake:splashing（分级洗涤，priority {}）",
                SPLASHING.getPriority());
    }

    private GreateFanProcessingTypes() {
    }
}
