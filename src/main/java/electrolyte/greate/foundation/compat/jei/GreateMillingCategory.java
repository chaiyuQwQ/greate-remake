package electrolyte.greate.foundation.compat.jei;

import com.gregtechceu.gtceu.api.GTValues;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedMillstone;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import electrolyte.greate.GreateValues;
import electrolyte.greate.content.kinetics.millstone.TieredMillingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.List;

public class GreateMillingCategory extends CreateRecipeCategory<TieredMillingRecipe> {

    private final AnimatedMillstone millstone = new AnimatedMillstone();

    public GreateMillingCategory(Info<TieredMillingRecipe> info) {
        super(info);
    }

    @Override
    protected void setRecipe(IRecipeLayoutBuilder builder, TieredMillingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 15, 9)
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
        boolean single = results.size() == 1;
        int i = 0;
        for (ProcessingOutput output : results) {
            int xOffset = i % 2 == 0 ? 0 : 19;
            int yOffset = (i / 2) * -19;

            builder.addSlot(RecipeIngredientRole.OUTPUT, single ? 139 : 133 + xOffset, 27 + yOffset)
                    .setBackground(getRenderedSlot(output), -1, -1)
                    .addItemStack(output.getStack())
                    .addRichTooltipCallback(addStochasticTooltip(output));
            i++;
        }
    }

    @Override
    protected void draw(TieredMillingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics,
                        double mouseX, double mouseY) {
        AllGuiTextures.JEI_ARROW.render(graphics, 85, 32);
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 43, 4);
        millstone.draw(graphics, 48, 27);
    }

    private static Component tierLabel(int tier) {
        return Component.literal(GreateValues.shortName(tier))
                .withStyle(Style.EMPTY.withColor(GTValues.VC[Math.clamp(tier, 0, GreateValues.SN.length - 1)]));
    }
}
