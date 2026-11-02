package electrolyte.greate;

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
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.ItemStack;

public class GreateDisplayItems implements CreativeModeTab.DisplayItemsGenerator {

    @Override
    public void accept(ItemDisplayParameters parameters, Output output) {
        for (var entry : Shafts.ALL) {
            output.accept(entry.asStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        for (var entry : Cogwheels.all()) {
            output.accept(entry.asStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        for (var entry : Millstones.ALL) {
            output.accept(entry.asStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        for (var entry : MechanicalPresses.ALL) {
            output.accept(entry.asStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        for (var entry : Gearboxes.ALL) {
            output.accept(entry.asStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        for (var entry : VerticalGearboxes.ALL) {
            output.accept(new ItemStack(entry.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        for (var entry : MechanicalMixers.ALL) {
            output.accept(entry.asStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        for (var entry : MechanicalPumps.ALL) {
            output.accept(entry.asStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        for (var entry : CrushingWheels.WHEELS) {
            output.accept(entry.asStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        for (var entry : EncasedFans.ALL) {
            output.accept(entry.asStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        for (var entry : Saws.ALL) {
            output.accept(entry.asStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        for (var entry : Belts.CONNECTOR_ITEMS) {
            output.accept(new ItemStack(entry.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}
