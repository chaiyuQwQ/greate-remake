package electrolyte.greate.foundation.item;

import com.gregtechceu.gtceu.api.GTValues;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.item.KineticStats;
import electrolyte.greate.Greate;
import electrolyte.greate.GreateValues;
import electrolyte.greate.content.kinetics.simpleRelays.ITieredBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

@EventBusSubscriber(modid = Greate.MOD_ID)
public final class GreateKineticStats {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return;
        }
        Block block = blockItem.getBlock();
        if (!(block instanceof ITieredBlock tiered)) {
            return;
        }

        // The capacity line goes straight under the item name, i.e. into the same block GT prints the
        // material formula into. Leading it with an empty line leaves a dangling blank row for materials
        // without a formula (alloys such as andesite alloy), so only Create's kinetic stats get separated.
        List<Component> tooltip = event.getToolTip();
        tooltip.add(capacityLine(tiered.getTier()));

        Player player = event.getEntity();
        if (block instanceof IRotate && player != null) {
            List<Component> kinetic = KineticStats.getKineticStats(block, player);
            if (!kinetic.isEmpty()) {
                tooltip.add(CommonComponents.EMPTY);
                tooltip.addAll(kinetic);
            }
        }
    }

    private static Component capacityLine(int tier) {
        Style tierStyle = Style.EMPTY.withColor(GTValues.VC[tier]);
        return Component.translatable("greate_remake.tooltip.max_capacity",
                        Component.literal(String.valueOf(GreateValues.maxCapacity(tier))).withStyle(tierStyle),
                        Component.literal(GreateValues.shortName(tier)).withStyle(tierStyle))
                .withStyle(ChatFormatting.GRAY);
    }

    private GreateKineticStats() {
    }
}
