package electrolyte.greate.registry;

import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.gearbox.GearboxRenderer;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.fan.EncasedFanRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import electrolyte.greate.Greate;
import electrolyte.greate.content.kinetics.belt.TieredBeltBlockEntity;
import electrolyte.greate.content.kinetics.belt.TieredBeltRenderer;
import electrolyte.greate.content.kinetics.crusher.TieredCrushingWheelBlockEntity;
import electrolyte.greate.content.kinetics.crusher.TieredCrushingWheelControllerBlockEntity;
import electrolyte.greate.content.kinetics.fan.TieredEncasedFanBlockEntity;
import electrolyte.greate.content.kinetics.gearbox.TieredGearboxBlockEntity;
import electrolyte.greate.content.kinetics.mixer.TieredMechanicalMixerBlockEntity;
import electrolyte.greate.content.kinetics.millstone.TieredMillstoneBlockEntity;
import electrolyte.greate.content.kinetics.millstone.TieredMillstoneRenderer;
import electrolyte.greate.content.kinetics.press.TieredMechanicalPressBlockEntity;
import electrolyte.greate.content.kinetics.press.TieredPressRenderer;
import electrolyte.greate.content.kinetics.saw.TieredSawBlockEntity;
import electrolyte.greate.content.kinetics.saw.TieredSawRenderer;
import electrolyte.greate.content.kinetics.simpleRelays.TieredBracketedKineticBlockEntity;
import electrolyte.greate.content.fluids.pump.TieredPumpBlockEntity;
import electrolyte.greate.content.fluids.pump.TieredPumpRenderer;

public final class GreateBlockEntityTypes {

    public static final BlockEntityEntry<TieredBracketedKineticBlockEntity> TIERED_BRACKETED_KINETIC;

    public static final BlockEntityEntry<TieredMillstoneBlockEntity> TIERED_MILLSTONE;

    public static final BlockEntityEntry<TieredMechanicalPressBlockEntity> TIERED_MECHANICAL_PRESS;

    public static final BlockEntityEntry<TieredGearboxBlockEntity> TIERED_GEARBOX;

    public static final BlockEntityEntry<TieredMechanicalMixerBlockEntity> TIERED_MECHANICAL_MIXER;

    public static final BlockEntityEntry<TieredPumpBlockEntity> TIERED_MECHANICAL_PUMP;

    public static final BlockEntityEntry<TieredCrushingWheelBlockEntity> TIERED_CRUSHING_WHEEL;

    public static final BlockEntityEntry<TieredCrushingWheelControllerBlockEntity> TIERED_CRUSHING_WHEEL_CONTROLLER;

    public static final BlockEntityEntry<TieredEncasedFanBlockEntity> TIERED_ENCASED_FAN;

    public static final BlockEntityEntry<TieredSawBlockEntity> TIERED_SAW;

    public static final BlockEntityEntry<TieredBeltBlockEntity> TIERED_BELT;

    static {
        var bracketed = Greate.GT_REGISTRATE
                .blockEntity("tiered_bracketed_kinetic", TieredBracketedKineticBlockEntity::new);
        Shafts.ALL.forEach(bracketed::validBlock);
        Cogwheels.SMALL.forEach(bracketed::validBlock);
        Cogwheels.LARGE.forEach(bracketed::validBlock);
        TIERED_BRACKETED_KINETIC = bracketed

                .renderer(() -> BracketedKineticBlockEntityRenderer::new)
                .register();

        var millstone = Greate.GT_REGISTRATE.blockEntity("tiered_millstone", TieredMillstoneBlockEntity::new);
        Millstones.ALL.forEach(millstone::validBlock);

        TIERED_MILLSTONE = millstone
                .renderer(() -> TieredMillstoneRenderer::new)
                .register();

        var press = Greate.GT_REGISTRATE.blockEntity("tiered_mechanical_press",
                TieredMechanicalPressBlockEntity::new);
        MechanicalPresses.ALL.forEach(press::validBlock);

        TIERED_MECHANICAL_PRESS = press
                .renderer(() -> TieredPressRenderer::new)
                .register();

        var gearbox = Greate.GT_REGISTRATE.blockEntity("tiered_gearbox", TieredGearboxBlockEntity::new);
        Gearboxes.ALL.forEach(gearbox::validBlock);
        TIERED_GEARBOX = gearbox
                .renderer(() -> GearboxRenderer::new)
                .register();

        var mixer = Greate.GT_REGISTRATE.blockEntity("tiered_mechanical_mixer",
                TieredMechanicalMixerBlockEntity::new);
        MechanicalMixers.ALL.forEach(mixer::validBlock);
        TIERED_MECHANICAL_MIXER = mixer
                .renderer(() -> MechanicalMixerRenderer::new)
                .register();

        var pump = Greate.GT_REGISTRATE.blockEntity("tiered_mechanical_pump", TieredPumpBlockEntity::new);
        MechanicalPumps.ALL.forEach(pump::validBlock);
        TIERED_MECHANICAL_PUMP = pump
                .renderer(() -> TieredPumpRenderer::new)
                .register();

        var crushingWheel = Greate.GT_REGISTRATE.blockEntity("tiered_crushing_wheel",
                TieredCrushingWheelBlockEntity::new);
        CrushingWheels.WHEELS.forEach(crushingWheel::validBlock);
        TIERED_CRUSHING_WHEEL = crushingWheel
                .renderer(() -> KineticBlockEntityRenderer::new)
                .register();

        var crushingWheelController = Greate.GT_REGISTRATE.blockEntity("tiered_crushing_wheel_controller",
                TieredCrushingWheelControllerBlockEntity::new);
        CrushingWheels.CONTROLLERS.forEach(crushingWheelController::validBlock);
        TIERED_CRUSHING_WHEEL_CONTROLLER = crushingWheelController.register();

        var fan = Greate.GT_REGISTRATE.blockEntity("tiered_encased_fan", TieredEncasedFanBlockEntity::new);
        EncasedFans.ALL.forEach(fan::validBlock);
        TIERED_ENCASED_FAN = fan
                .renderer(() -> EncasedFanRenderer::new)
                .register();

        var saw = Greate.GT_REGISTRATE.blockEntity("tiered_mechanical_saw", TieredSawBlockEntity::new);
        Saws.ALL.forEach(saw::validBlock);
        TIERED_SAW = saw
                .renderer(() -> TieredSawRenderer::new)
                .register();

        var belt = Greate.GT_REGISTRATE.blockEntity("tiered_belt", TieredBeltBlockEntity::new);
        Belts.ALL.forEach(belt::validBlock);
        TIERED_BELT = belt
                .renderer(() -> TieredBeltRenderer::new)
                .register();
    }

    public static void register() {
    }

    private GreateBlockEntityTypes() {
    }
}
