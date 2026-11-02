package electrolyte.greate.infrastructure.ponder;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.simibubi.create.infrastructure.ponder.scenes.BeltScenes;
import com.simibubi.create.infrastructure.ponder.scenes.FanScenes;
import com.simibubi.create.infrastructure.ponder.scenes.KineticsScenes;
import com.simibubi.create.infrastructure.ponder.scenes.MechanicalSawScenes;
import com.simibubi.create.infrastructure.ponder.scenes.ProcessingScenes;
import com.simibubi.create.infrastructure.ponder.scenes.fluid.PumpScenes;
import com.tterrag.registrate.util.entry.RegistryEntry;
import electrolyte.greate.Greate;
import electrolyte.greate.registry.Belts;
import electrolyte.greate.registry.Cogwheels;
import electrolyte.greate.registry.CrushingWheels;
import electrolyte.greate.registry.EncasedFans;
import electrolyte.greate.registry.Gearboxes;
import electrolyte.greate.registry.MechanicalMixers;
import electrolyte.greate.registry.MechanicalPresses;
import electrolyte.greate.registry.MechanicalPumps;
import electrolyte.greate.registry.Millstones;
import electrolyte.greate.registry.Saws;
import electrolyte.greate.registry.Shafts;
import electrolyte.greate.registry.VerticalGearboxes;
import com.simibubi.create.Create;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.StoryBoardEntry;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Attaches the tiered blocks to Create's existing Ponder storyboards.
 *
 * <p>Two things are easy to get wrong: storyboard ids must match Create's
 * {@code AllCreatePonderScenes}, and they must be passed as {@code Create.asResource(...)}
 * because the {@code String} overload resolves against <b>our</b> namespace and yields a
 * missing schematic (UI shows, 3D scene stays empty). Ponder also derives lang keys from the
 * registering plugin's mod id, so reused scenes resolve as {@code greate_remake.ponder.*} and are
 * translated by {@code tools/sync_ponder_lang.py}.</p>
 */
public final class GreatePonderScenes {

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<RegistryEntry<?, ?>> scenes = helper.withKeyFunction(RegistryEntry::getId);

        scenes.forComponents(Shafts.ALL)
                .addStoryBoard(Create.asResource("shaft/relay"), KineticsScenes::shaftAsRelay, AllCreatePonderTags.KINETIC_RELAYS)
                .addStoryBoard(Create.asResource("shaft/encasing"), KineticsScenes::shaftsCanBeEncased);

        scenes.forComponents(Cogwheels.SMALL)
                .addStoryBoard(Create.asResource("cog/small"), KineticsScenes::cogAsRelay, AllCreatePonderTags.KINETIC_RELAYS)
                .addStoryBoard(Create.asResource("cog/speedup"), KineticsScenes::cogsSpeedUp)
                .addStoryBoard(Create.asResource("cog/encasing"), KineticsScenes::cogwheelsCanBeEncased);
        scenes.forComponents(Cogwheels.LARGE)
                .addStoryBoard(Create.asResource("cog/speedup"), KineticsScenes::cogsSpeedUp)
                .addStoryBoard(Create.asResource("cog/large"), KineticsScenes::largeCogAsRelay, AllCreatePonderTags.KINETIC_RELAYS)
                .addStoryBoard(Create.asResource("cog/encasing"), KineticsScenes::cogwheelsCanBeEncased);

        scenes.forComponents(Gearboxes.ALL)
                .addStoryBoard(Create.asResource("gearbox"), KineticsScenes::gearbox, AllCreatePonderTags.KINETIC_RELAYS);
        scenes.forComponents(VerticalGearboxes.ALL)
                .addStoryBoard(Create.asResource("gearbox"), KineticsScenes::gearbox, AllCreatePonderTags.KINETIC_RELAYS);

        scenes.forComponents(Belts.CONNECTOR_ITEMS)
                .addStoryBoard(Create.asResource("belt/connect"), BeltScenes::beltConnector, AllCreatePonderTags.KINETIC_RELAYS)
                .addStoryBoard(Create.asResource("belt/directions"), BeltScenes::directions)
                .addStoryBoard(Create.asResource("belt/transport"), BeltScenes::transport, AllCreatePonderTags.LOGISTICS)
                .addStoryBoard(Create.asResource("belt/encasing"), BeltScenes::beltsCanBeEncased)

                .addStoryBoard(Greate.id("belt_tier"), GreateTierScenes::beltTier,
                        AllCreatePonderTags.KINETIC_RELAYS);

        scenes.forComponents(EncasedFans.ALL)
                .addStoryBoard(Create.asResource("fan/direction"), FanScenes::direction, AllCreatePonderTags.KINETIC_APPLIANCES)
                .addStoryBoard(Create.asResource("fan/processing"), FanScenes::processing)

                .addStoryBoard(Greate.id("fan_tier"), GreateTierScenes::fanTier,
                        AllCreatePonderTags.KINETIC_APPLIANCES);

        scenes.forComponents(Millstones.ALL)
                .addStoryBoard(Create.asResource("millstone"), ProcessingScenes::millstone);
        scenes.forComponents(CrushingWheels.WHEELS)
                .addStoryBoard(Create.asResource("crushing_wheel"), ProcessingScenes::crushingWheels);
        scenes.forComponents(MechanicalMixers.ALL)
                .addStoryBoard(Create.asResource("mechanical_mixer/mixing"), ProcessingScenes::mixing);
        scenes.forComponents(MechanicalPresses.ALL)
                .addStoryBoard(Create.asResource("mechanical_press/pressing"), ProcessingScenes::pressing)
                .addStoryBoard(Create.asResource("mechanical_press/compacting"), ProcessingScenes::compacting);

        scenes.forComponents(Saws.ALL)
                .addStoryBoard(Create.asResource("mechanical_saw/processing"), MechanicalSawScenes::processing,
                        AllCreatePonderTags.KINETIC_APPLIANCES)
                .addStoryBoard(Create.asResource("mechanical_saw/breaker"), MechanicalSawScenes::treeCutting)
                .addStoryBoard(Create.asResource("mechanical_saw/contraption"), MechanicalSawScenes::contraption,
                        AllCreatePonderTags.CONTRAPTION_ACTOR);

        scenes.forComponents(MechanicalPumps.ALL)
                .addStoryBoard(Create.asResource("mechanical_pump/flow"), PumpScenes::flow,
                        AllCreatePonderTags.FLUIDS, AllCreatePonderTags.KINETIC_APPLIANCES)
                .addStoryBoard(Create.asResource("mechanical_pump/speed"), PumpScenes::speed);
    }

    public static void logCoverage() {
        var access = net.createmod.ponder.foundation.PonderIndex.getSceneAccess();
        var resourceManager = net.minecraft.client.Minecraft.getInstance().getResourceManager();

        int entries = 0;
        int missingStructure = 0;
        for (var registered : access.getRegisteredEntries()) {
            StoryBoardEntry entry = registered.getValue();
            if (!Greate.MOD_ID.equals(entry.getComponent()
                    .getNamespace())) {
                continue;
            }
            entries++;
            var schematic = entry.getSchematicLocation();
            var file = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                    schematic.getNamespace(), "ponder/" + schematic.getPath() + ".nbt");
            if (resourceManager.getResource(file).isEmpty()) {
                missingStructure++;
                if (missingStructure <= 5) {
                    Greate.LOGGER.warn("[Greate] Ponder 场景缺少结构文件: {}（组件 {}）", file, entry.getComponent());
                }
            }
        }
        if (missingStructure > 0) {
            Greate.LOGGER.error("[Greate] Ponder 有 {} 条场景找不到结构文件 → 游戏里只有 UI、没有 3D 场景。"
                    + "检查 addStoryBoard 是否误用了字符串重载（会拼成 greate_remake: 命名空间）。", missingStructure);
        } else {
            Greate.LOGGER.info("[Greate] Ponder 覆盖: {} 条场景挂载，结构文件全部命中", entries);
        }
    }

    private GreatePonderScenes() {
    }
}
