package electrolyte.greate.content.kinetics.fan;

import com.gregtechceu.gtceu.data.recipe.GTRecipeTypes;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import electrolyte.greate.foundation.recipe.GreateGtRecipeBridge;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TieredSplashingType extends AllFanProcessingTypes.SplashingType {

    private static final int PRIORITY = 401;

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    @Override
    public boolean canProcess(ItemStack stack, Level level) {
        if (super.canProcess(stack, level)) {
            return true;
        }
        int tier = TieredFanProcessing.currentTier();
        return tier >= 0 && !wash(stack, level, tier).isEmpty();
    }

    @Override
    public @Nullable List<ItemStack> process(ItemStack stack, Level level) {

        List<ItemStack> create = super.process(stack, level);
        if (create != null) {
            return create;
        }
        int tier = TieredFanProcessing.currentTier();
        if (tier < 0) {

            return null;
        }
        List<ItemStack> results = wash(stack, level, tier);
        return results.isEmpty() ? null : results;
    }

    public static int requiredTier(ItemStack stack, Level level) {
        GreateGtRecipeBridge.Projection projection = find(stack, level);
        return projection == null ? -1 : projection.recipeTier();
    }

    private static @Nullable GreateGtRecipeBridge.Projection find(ItemStack stack, Level level) {
        if (stack.isEmpty() || level == null) {
            return null;
        }
        GreateGtRecipeBridge.Projection best = null;
        for (GreateGtRecipeBridge.Projection projection
                : GreateGtRecipeBridge.projections(level.getRecipeManager(), GTRecipeTypes.ORE_WASHER_RECIPES)) {
            if (!projection.matches(stack) || projection.inputCount() != 1) {
                continue;
            }
            if (best == null || projection.recipeTier() < best.recipeTier()) {
                best = projection;
            }
        }
        return best;
    }

    public static List<ItemStack> wash(ItemStack stack, Level level, int fanTier) {
        GreateGtRecipeBridge.Projection best = find(stack, level);
        if (best == null || best.recipeTier() > fanTier) {
            return List.of();
        }
        RandomSource random = level.getRandom();
        List<ItemStack> results = new ArrayList<>();
        for (ProcessingOutput output : best.outputsAt(fanTier)) {
            ItemStack rolled = output.rollOutput(random);
            if (!rolled.isEmpty()) {
                results.add(rolled);
            }
        }
        return results;
    }
}
