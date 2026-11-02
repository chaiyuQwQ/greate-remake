package electrolyte.greate.foundation.compat.jei;

import com.gregtechceu.gtceu.api.GTValues;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedCrushingWheels;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import electrolyte.greate.GreateValues;
import electrolyte.greate.content.kinetics.crusher.TieredCrushingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.createmod.catnip.layout.LayoutHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.ArrayList;
import java.util.List;

public class GreateCrushingCategory extends CreateRecipeCategory<TieredCrushingRecipe> {

    private final AnimatedCrushingWheels crushingWheels = new AnimatedCrushingWheels();

    public GreateCrushingCategory(Info<TieredCrushingRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TieredCrushingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 51, 3)
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

        int xOffset = getBackground().getWidth() / 2;
        int yOffset = 86;
        layoutOutput(recipe).forEach(entry -> builder
                .addSlot(RecipeIngredientRole.OUTPUT, xOffset + entry.posX() + 1, yOffset + entry.posY() + 1)
                .setBackground(getRenderedSlot(entry.output()), -1, -1)
                .addItemStack(entry.output().getStack())
                .addRichTooltipCallback(addStochasticTooltip(entry.output())));
    }

    private List<LayoutEntry> layoutOutput(StandardProcessingRecipe<?> recipe) {
        List<ProcessingOutput> results = recipe.getRollableResults();
        List<LayoutEntry> positions = new ArrayList<>(results.size());

        LayoutHelper layout = LayoutHelper.centeredHorizontal(results.size(), 1, 18, 18, 1);
        for (ProcessingOutput result : results) {
            positions.add(new LayoutEntry(result, layout.getX(), layout.getY()));
            layout.next();
        }
        return positions;
    }

    @Override
    public void draw(TieredCrushingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics,
                     double mouseX, double mouseY) {
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 72, 7);
        crushingWheels.draw(graphics, 62, 59);
    }

    private static Component tierLabel(int tier) {
        return Component.literal(GreateValues.shortName(tier))
                .withStyle(Style.EMPTY.withColor(GTValues.VC[Math.clamp(tier, 0, GreateValues.SN.length - 1)]));
    }

    private record LayoutEntry(ProcessingOutput output, int posX, int posY) {
    }
}
