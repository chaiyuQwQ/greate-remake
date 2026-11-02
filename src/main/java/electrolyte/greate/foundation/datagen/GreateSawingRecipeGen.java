package electrolyte.greate.foundation.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.gregtechceu.gtceu.api.material.ChemicalHelper;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import electrolyte.greate.Greate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.gregtechceu.gtceu.api.GTValues.EV;
import static com.gregtechceu.gtceu.api.GTValues.LV;
import static com.gregtechceu.gtceu.api.GTValues.MV;

public class GreateSawingRecipeGen implements DataProvider {

    private record RecipeDef(Material material, int count, int duration, int tier) {
    }

    private final PackOutput output;

    public GreateSawingRecipeGen(PackOutput output) {
        this.output = output;
    }

    private static List<RecipeDef> recipes() {
        List<RecipeDef> recipes = new ArrayList<>();
        recipes.add(new RecipeDef(GTMaterials.Steel, 2, 50, LV));
        recipes.add(new RecipeDef(GTMaterials.Aluminium, 2, 50, MV));
        recipes.add(new RecipeDef(GTMaterials.Titanium, 2, 50, EV));
        return recipes;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (RecipeDef def : recipes()) {
            ItemStack input = ChemicalHelper.get(TagPrefix.ingot, def.material());
            ItemStack result = ChemicalHelper.get(TagPrefix.rod, def.material());
            if (input.isEmpty() || result.isEmpty()) {
                Greate.LOGGER.warn("[Greate] datagen: 跳过锯切配方，GT 物品为空：{}", def.material().getName());
                continue;
            }
            ResourceLocation inputId = BuiltInRegistries.ITEM.getKey(input.getItem());
            ResourceLocation outputId = BuiltInRegistries.ITEM.getKey(result.getItem());
            String name = outputId.getPath() + "_from_" + inputId.getPath();
            Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                    .resolve(Greate.MOD_ID + "/recipe/sawing/" + name + ".json");
            futures.add(DataProvider.saveStable(cache, buildJson(inputId, outputId, def), path));
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private static JsonObject buildJson(ResourceLocation inputId, ResourceLocation outputId, RecipeDef def) {
        JsonObject json = new JsonObject();
        json.addProperty("type", Greate.MOD_ID + ":sawing");

        JsonArray ingredients = new JsonArray();
        JsonObject ingredient = new JsonObject();
        ingredient.addProperty("item", inputId.toString());
        ingredients.add(ingredient);
        json.add("ingredients", ingredients);

        JsonArray results = new JsonArray();
        JsonObject result = new JsonObject();
        result.addProperty("id", outputId.toString());
        result.addProperty("count", def.count());
        results.add(result);
        json.add("results", results);

        json.addProperty("processing_time", def.duration());
        json.addProperty("recipe_tier", def.tier());
        return json;
    }

    @Override
    public String getName() {
        return "Greate Sawing Recipes";
    }
}
