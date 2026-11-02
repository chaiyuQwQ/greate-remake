package electrolyte.greate.foundation.recipe;

import com.simibubi.create.Create;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Consumer;

public final class GreateRecipeRemoval {

    private static final List<ResourceLocation> CREATE_KINETIC_RECIPES = List.of(
            Create.asResource("crafting/kinetics/shaft"),
            Create.asResource("crafting/kinetics/cogwheel"),
            Create.asResource("crafting/kinetics/large_cogwheel"),
            Create.asResource("crafting/kinetics/large_cogwheel_from_little"),
            Create.asResource("crafting/kinetics/gearbox"),
            Create.asResource("crafting/kinetics/gearbox_from_conversion"),
            Create.asResource("crafting/kinetics/vertical_gearbox"),
            Create.asResource("crafting/kinetics/vertical_gearbox_from_conversion"),
            Create.asResource("crafting/kinetics/millstone"),
            Create.asResource("crafting/kinetics/mechanical_press"),
            Create.asResource("crafting/kinetics/mechanical_mixer"),
            Create.asResource("crafting/kinetics/mechanical_pump"),
            Create.asResource("crafting/kinetics/mechanical_saw"),
            Create.asResource("crafting/kinetics/encased_fan"),
            Create.asResource("crafting/kinetics/belt_connector"),
            Create.asResource("mechanical_crafting/crushing_wheel")
    );

    public static void apply(Consumer<ResourceLocation> remove) {
        CREATE_KINETIC_RECIPES.forEach(remove);
    }

    public static int count() {
        return CREATE_KINETIC_RECIPES.size();
    }

    private GreateRecipeRemoval() {
    }
}
