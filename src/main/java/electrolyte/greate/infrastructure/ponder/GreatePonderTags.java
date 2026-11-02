package electrolyte.greate.infrastructure.ponder;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.RegistryEntry;
import electrolyte.greate.registry.Belts;
import electrolyte.greate.registry.Cogwheels;
import electrolyte.greate.registry.EncasedFans;
import electrolyte.greate.registry.Gearboxes;
import electrolyte.greate.registry.MechanicalPumps;
import electrolyte.greate.registry.Saws;
import electrolyte.greate.registry.Shafts;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class GreatePonderTags {

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        addAll(helper, AllCreatePonderTags.KINETIC_RELAYS, Shafts.ALL);
        addAll(helper, AllCreatePonderTags.KINETIC_RELAYS, Cogwheels.SMALL);
        addAll(helper, AllCreatePonderTags.KINETIC_RELAYS, Cogwheels.LARGE);
        addAll(helper, AllCreatePonderTags.KINETIC_RELAYS, Gearboxes.ALL);

        addAll(helper, AllCreatePonderTags.KINETIC_APPLIANCES, EncasedFans.ALL);
        addAll(helper, AllCreatePonderTags.KINETIC_APPLIANCES, MechanicalPumps.ALL);
        addAll(helper, AllCreatePonderTags.CONTRAPTION_ACTOR, Saws.ALL);
        addAll(helper, AllCreatePonderTags.FLUIDS, MechanicalPumps.ALL);

        for (var connector : Belts.CONNECTOR_ITEMS) {
            helper.addToTag(AllCreatePonderTags.KINETIC_RELAYS, AllCreatePonderTags.LOGISTICS)
                    .add(connector.getId());
        }
    }

    private static void addAll(PonderTagRegistrationHelper<ResourceLocation> helper, ResourceLocation tag,
                               List<? extends RegistryEntry<?, ?>> group) {
        var builder = helper.addToTag(tag);
        for (RegistryEntry<?, ?> entry : group) {
            builder.add(entry.getId());
        }
    }

    private GreatePonderTags() {
    }
}
