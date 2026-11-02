package electrolyte.greate.foundation.compat.jei;

import com.gregtechceu.gtceu.api.GTValues;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedPress;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import electrolyte.greate.GreateValues;
import electrolyte.greate.content.kinetics.press.TieredPressingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.List;

public class GreatePressingCategory extends CreateRecipeCategory<TieredPressingRecipe> {

    private final AnimatedPress press = new AnimatedPress(false);

    public GreatePressingCategory(Info<TieredPressingRecipe> info) {
        super(info);
    }

    @Override
    protected void setRecipe(IRecipeLayoutBuilder builder, TieredPressingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 51)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(recipe.getIngredients().get(0))
                .addRichTooltipCallback((view, tooltip) -> {
                    tooltip.add(Component.translatable("greate_remake.tooltip.recipe_tier", tierLabel(recipe.getRecipeTier()))
                            .withStyle(ChatFormatting.GRAY));
                    if (recipe.getCircuitNumber() >= 0) {
                        tooltip.add(Component.translatable("greate_remake.tooltip.recipe_circuit", recipe.getCircuitNumber())
                                .withStyle(ChatFormatting.AQUA));
                    }
                });

        List<ProcessingOutput> results = recipe.getRollableResults();
        int i = 0;
        for (ProcessingOutput output : results) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 131 + 19 * i, 50)
                    .setBackground(getRenderedSlot(output), -1, -1)
                    .addItemStack(output.getStack())
                    .addRichTooltipCallback(addStochasticTooltip(output));
            i++;
        }
    }

    @Override
    protected void draw(TieredPressingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics,
                        double mouseX, double mouseY) {
        AllGuiTextures.JEI_SHADOW.render(graphics, 61, 41);
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52, 54);
        press.draw(graphics, getBackground().getWidth() / 2 - 17, 22);
    }

    private static Component tierLabel(int tier) {
        return Component.literal(GreateValues.shortName(tier))
                .withStyle(Style.EMPTY.withColor(GTValues.VC[Math.clamp(tier, 0, GreateValues.SN.length - 1)]));
    }
}
