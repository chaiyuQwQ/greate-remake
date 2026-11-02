package electrolyte.greate;

import com.gregtechceu.gtceu.api.registry.GTRegistries;
import electrolyte.greate.registry.Belts;
import electrolyte.greate.registry.Cogwheels;
import electrolyte.greate.registry.CrushingWheels;
import electrolyte.greate.registry.EncasedFans;
import electrolyte.greate.registry.Gearboxes;
import electrolyte.greate.registry.GreateBlockEntityTypes;
import electrolyte.greate.registry.GreateMaterials;
import electrolyte.greate.registry.GreateTagPrefixes;
import electrolyte.greate.registry.MechanicalMixers;
import electrolyte.greate.registry.MechanicalPresses;
import electrolyte.greate.registry.MechanicalPumps;
import electrolyte.greate.registry.Millstones;
import electrolyte.greate.registry.Saws;
import electrolyte.greate.registry.Shafts;
import electrolyte.greate.registry.VerticalGearboxes;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = Greate.MOD_ID)
public final class GreateRegistration {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRegister(RegisterEvent event) {
        if (GTRegistries.TAG_PREFIX_REGISTRY.equals(event.getRegistryKey())) {

            GreateTagPrefixes.init();
        } else if (GTRegistries.MATERIAL_REGISTRY.equals(event.getRegistryKey())) {

            GreateMaterials.register();
            Shafts.register();
            Cogwheels.register();
            Millstones.register();
            MechanicalPresses.register();
            Gearboxes.register();

            VerticalGearboxes.register();
            MechanicalMixers.register();
            MechanicalPumps.register();
            CrushingWheels.register();
            EncasedFans.register();
            Saws.register();

            Belts.register();

            GreateBlockEntityTypes.register();
        }
    }

    private GreateRegistration() {
    }
}
