package electrolyte.greate.foundation.compat.jei;

import com.gregtechceu.gtceu.api.GTValues;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.compat.jei.category.animations.AnimatedMixer;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import electrolyte.greate.GreateValues;
import electrolyte.greate.content.kinetics.mixer.TieredMixingRecipe;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class GreateMixingCategory extends BasinCategory {

    private final AnimatedMixer mixer = new AnimatedMixer();
    private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();

    public GreateMixingCategory(Info<BasinRecipe> info) {
        super(info, true);
    }

    @Override
    public void draw(BasinRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics,
                     double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);

        HeatCondition requiredHeat = recipe.getRequiredHeat();
        if (requiredHeat != HeatCondition.NONE) {
            heater.withHeat(requiredHeat.visualizeAsBlazeBurner())
                    .draw(graphics, getBackground().getWidth() / 2 + 3, 55);
        }
        mixer.draw(graphics, getBackground().getWidth() / 2 + 3, 34);

        if (recipe instanceof TieredMixingRecipe tiered) {
            Component text = Component.translatable("greate_remake.tooltip.recipe_tier", tierLabel(tiered.getRecipeTier()))
                    .withStyle(ChatFormatting.GRAY);
            graphics.drawString(Minecraft.getInstance().font, text, 4, 4, 0xFFFFFF, false);
        }
    }

    private static Component tierLabel(int tier) {
        return Component.literal(GreateValues.shortName(tier))
                .withStyle(Style.EMPTY.withColor(GTValues.VC[Math.clamp(tier, 0, GreateValues.SN.length - 1)]));
    }
}
