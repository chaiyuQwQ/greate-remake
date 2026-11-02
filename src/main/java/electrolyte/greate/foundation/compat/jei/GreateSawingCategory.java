package electrolyte.greate.foundation.compat.jei;

import com.gregtechceu.gtceu.api.GTValues;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import electrolyte.greate.GreateValues;
import electrolyte.greate.content.kinetics.saw.TieredSawingRecipe;
import electrolyte.greate.registry.Saws;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;

public class GreateSawingCategory extends CreateRecipeCategory<TieredSawingRecipe> {

    private final TieredAnimatedSaw saw;

    public GreateSawingCategory(Info<TieredSawingRecipe> info, TieredAnimatedSaw saw) {
        super(info);
        this.saw = saw;
    }

    @Override
    protected void setRecipe(IRecipeLayoutBuilder builder, TieredSawingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 44, 5)
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

        for (SizedFluidIngredient fluidIngredient : recipe.getFluidIngredients()) {
            addFluidSlot(builder, 28, 48, fluidIngredient);
        }

        List<ProcessingOutput> results = recipe.getRollableResults();
        int i = 0;
        for (ProcessingOutput output : results) {
            int xOffset = i % 2 == 0 ? 0 : 19;
            int yOffset = (i / 2) * -19;
            builder.addSlot(RecipeIngredientRole.OUTPUT, 118 + xOffset, 48 + yOffset)
                    .setBackground(getRenderedSlot(output), -1, -1)
                    .addItemStack(output.getStack())
                    .addRichTooltipCallback(addStochasticTooltip(output));
            i++;
        }
    }

    @Override
    protected void draw(TieredSawingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics,
                        double mouseX, double mouseY) {
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 70, 6);
        AllGuiTextures.JEI_SHADOW.render(graphics, 72 - 17, 42 + 13);
        saw.draw(graphics, 72, 42);
    }

    public static TieredAnimatedSaw animatedSaw() {
        return new TieredAnimatedSaw(Saws.ALL.isEmpty() ? null : Saws.ALL.get(0).get());
    }

    private static Component tierLabel(int tier) {
        return Component.literal(GreateValues.shortName(tier))
                .withStyle(Style.EMPTY.withColor(GTValues.VC[Math.clamp(tier, 0, GreateValues.SN.length - 1)]));
    }
}
