package electrolyte.greate.foundation.recipe;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.ingredient.EnergyStack;
import com.gregtechceu.gtceu.api.recipe.kind.GTRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import electrolyte.greate.Greate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Projects GT recipes into Create processing recipes at runtime; nothing is written to disk.
 *
 * <p>Only single-item-input recipes (plus an optional programmed circuit) without fluid
 * output are projected. The cache is keyed per {@link RecipeManager}
 * because client and integrated server each hold one; a single shared slot would thrash and
 * re-project thousands of recipes every tick.</p>
 */
public final class GreateGtRecipeBridge {

    public record OutputSpec(ItemStack stack, int chance, int maxChance, int tierChanceBoost) {

        public ProcessingOutput atTier(int recipeTier, int machineTier) {
            int boosted = chance;
            if (machineTier > recipeTier && tierChanceBoost != 0) {
                boosted = Math.min(maxChance, chance + (machineTier - recipeTier) * tierChanceBoost);
            }
            return new ProcessingOutput(stack.copy(), Math.clamp(boosted / (float) maxChance, 0F, 1F));
        }
    }

    public record Projection(ResourceLocation id, ResourceLocation bridgeId, int recipeTier, int circuitNumber,
                             Ingredient input, int inputCount, boolean hasFluidInput, int duration,
                             List<OutputSpec> outputs) {

        public boolean matches(ItemStack stack) {
            return !stack.isEmpty() && input.test(stack);
        }

        public List<ProcessingOutput> outputsAt(int machineTier) {
            List<ProcessingOutput> results = new ArrayList<>(outputs.size());
            for (OutputSpec spec : outputs) {
                results.add(spec.atTier(recipeTier, machineTier));
            }
            return results;
        }
    }

    private static final Map<RecipeManager, Map<GTRecipeType, List<Projection>>> CACHE = new WeakHashMap<>();

    public static List<Projection> projections(RecipeManager recipeManager, GTRecipeType type) {
        synchronized (CACHE) {
            Map<GTRecipeType, List<Projection>> byType = CACHE.get(recipeManager);
            if (byType != null) {
                List<Projection> cached = byType.get(type);
                if (cached != null) {
                    return cached;
                }
            }
        }
        List<Projection> built = build(recipeManager, type);
        synchronized (CACHE) {
            CACHE.computeIfAbsent(recipeManager, manager -> new IdentityHashMap<>()).put(type, built);
        }
        return built;
    }

    private static List<Projection> build(RecipeManager recipeManager, GTRecipeType type) {
        int total = 0;
        List<Projection> projections = new ArrayList<>();
        for (RecipeHolder<?> holder : recipeManager.getAllRecipesFor(type)) {
            total++;
            try {
                Projection projection = project(holder);
                if (projection != null) {
                    projections.add(projection);
                }
            } catch (Throwable t) {

            }
        }
        List<Projection> result = List.copyOf(projections);
        Greate.LOGGER.info("[Greate][gt-bridge] {}：{} 条配方，可投影 {} 条",
                type.registryName, total, result.size());
        return result;
    }

    private static @Nullable Projection project(RecipeHolder<?> holder) {
        if (!(holder.value() instanceof GTRecipe recipe)) {
            return null;
        }

        if (!recipe.conditions.isEmpty()) {
            return null;
        }

        if (!recipe.getOutputContents(FluidRecipeCapability.CAP).isEmpty()) {
            return null;
        }
        boolean hasFluidInput = !recipe.getInputContents(FluidRecipeCapability.CAP).isEmpty();

        Content normalInput = null;
        int circuitNumber = -1;
        for (Content content : recipe.getInputContents(ItemRecipeCapability.CAP)) {
            int circuit = circuitOf(content);
            if (circuit != Integer.MIN_VALUE) {
                if (circuitNumber != -1) {
                    return null;
                }
                circuitNumber = circuit;
                continue;
            }
            if (normalInput != null) {
                return null;
            }
            normalInput = content;
        }
        if (normalInput == null) {
            return null;
        }
        Ingredient input = toIngredient(normalInput);
        if (input == null || input.isEmpty()) {
            return null;
        }
        int inputCount = countOf(normalInput);

        List<Content> euInputs = recipe.getTickInputContents(EURecipeCapability.CAP);
        if (euInputs.isEmpty()) {
            return null;
        }
        Object euContent = euInputs.get(0).getContent();
        if (!(euContent instanceof EnergyStack energyStack)) {
            return null;
        }
        int recipeTier = tierFromEU(energyStack.voltage());

        List<Content> itemOutputs = recipe.getOutputContents(ItemRecipeCapability.CAP);
        if (itemOutputs.isEmpty()) {
            return null;
        }
        List<OutputSpec> outputs = new ArrayList<>(itemOutputs.size());
        for (Content content : itemOutputs) {
            ItemStack stack = outputStack(content);
            if (stack.isEmpty()) {
                continue;
            }
            outputs.add(new OutputSpec(stack, content.chance, Math.max(1, content.maxChance),
                    content.tierChanceBoost));
        }
        if (outputs.isEmpty()) {
            return null;
        }

        ResourceLocation bridgeId = holder.id();
        ResourceLocation syntheticId = Greate.id("gt_bridge/" + bridgeId.getNamespace() + "/" + bridgeId.getPath());
        return new Projection(syntheticId, bridgeId, recipeTier, circuitNumber, input, inputCount,
                hasFluidInput, Math.max(1, recipe.duration), List.copyOf(outputs));
    }

    private static int circuitOf(Content content) {
        if (!(content.getContent() instanceof SizedIngredient sized)) {
            return Integer.MIN_VALUE;
        }
        if (!(com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredientExtensions
                .getContainedCustom(sized) instanceof com.gregtechceu.gtceu.api.recipe.ingredient.IntCircuitIngredient)) {
            return Integer.MIN_VALUE;
        }
        ItemStack[] items = sized.ingredient().getItems();
        if (items.length == 0) {
            return Integer.MIN_VALUE;
        }
        return com.gregtechceu.gtceu.common.item.behavior.IntCircuitBehaviour
                .getCircuitConfiguration(items[0]);
    }

    private static @Nullable Ingredient toIngredient(Content content) {
        Object raw = content.getContent();
        if (raw instanceof SizedIngredient sized) {
            if (com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredientExtensions
                    .getContainedCustom(sized) != null) {
                return null;
            }
            return sized.ingredient();
        }
        if (raw instanceof Ingredient ingredient) {
            return ingredient;
        }
        if (raw instanceof ItemStack stack && !stack.isEmpty()) {
            return Ingredient.of(stack);
        }
        return null;
    }

    private static int countOf(Content content) {
        if (content.getContent() instanceof SizedIngredient sized) {
            return Math.max(1, sized.count());
        }
        if (content.getContent() instanceof ItemStack stack) {
            return Math.max(1, stack.getCount());
        }
        return 1;
    }

    private static ItemStack outputStack(Content content) {
        Object raw = content.getContent();
        if (raw instanceof SizedIngredient sized) {
            ItemStack[] items = sized.ingredient().getItems();
            return items.length == 0 ? ItemStack.EMPTY : items[0].copyWithCount(Math.max(1, sized.count()));
        }
        if (raw instanceof ItemStack stack) {
            return stack.copy();
        }
        return ItemStack.EMPTY;
    }

    public static int tierFromEU(long eut) {
        for (int i = 0; i < GTValues.V.length; i++) {
            if (eut <= GTValues.V[i]) {
                return Math.min(i, 9);
            }
        }
        return 9;
    }

    private GreateGtRecipeBridge() {
    }
}
