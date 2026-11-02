package electrolyte.greate.foundation.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.gregtechceu.gtceu.api.material.ChemicalHelper;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import com.simibubi.create.AllItems;
import electrolyte.greate.Greate;
import electrolyte.greate.registry.Millstones;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.gregtechceu.gtceu.api.GTValues.HV;
import static com.gregtechceu.gtceu.api.GTValues.MV;

public class GreateMillingRecipeGen implements DataProvider {

    private static final int DEFAULT_DURATION = 100;

    private record RecipeDef(Item input, Material outputMaterial, TagPrefix outputPrefix, int tier, int duration,
                             int circuitNumber) {
        RecipeDef(Item input, Material outputMaterial, TagPrefix outputPrefix, int tier, int duration) {
            this(input, outputMaterial, outputPrefix, tier, duration, -1);
        }
    }

    private final PackOutput output;

    public GreateMillingRecipeGen(PackOutput output) {
        this.output = output;
    }

    private static List<RecipeDef> recipes() {
        List<RecipeDef> recipes = new ArrayList<>();
        recipes.add(new RecipeDef(AllItems.ZINC_INGOT.get(), GTMaterials.Zinc, TagPrefix.dust, MV, DEFAULT_DURATION));
        recipes.add(new RecipeDef(AllItems.BRASS_INGOT.get(), GTMaterials.Brass, TagPrefix.dust, HV, DEFAULT_DURATION));
        recipes.add(new RecipeDef(AllItems.ANDESITE_ALLOY.get(), GTMaterials.WroughtIron, TagPrefix.dust, HV, 120));

        recipes.add(new RecipeDef(Millstones.ALL.get(1).get().asItem(), GTMaterials.Steel, TagPrefix.dust,
                MV, 120, 3));
        return recipes;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (RecipeDef def : recipes()) {
            ItemStack result = ChemicalHelper.get(def.outputPrefix(), def.outputMaterial());
            if (result.isEmpty()) {
                Greate.LOGGER.warn("[Greate] datagen: 跳过研磨配方，GT 物品为空：{} {}",
                        def.outputPrefix().name, def.outputMaterial().getName());
                continue;
            }
            ResourceLocation inputId = BuiltInRegistries.ITEM.getKey(def.input());
            ResourceLocation outputId = BuiltInRegistries.ITEM.getKey(result.getItem());
            String name = outputId.getPath() + "_from_" + inputId.getPath();
            Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                    .resolve(Greate.MOD_ID + "/recipe/milling/" + name + ".json");
            futures.add(DataProvider.saveStable(cache, buildJson(inputId, result, def), path));
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private static JsonObject buildJson(ResourceLocation inputId, ItemStack result, RecipeDef def) {
        JsonObject json = new JsonObject();
        json.addProperty("type", Greate.MOD_ID + ":milling");

        JsonArray ingredients = new JsonArray();
        JsonObject ingredient = new JsonObject();
        ingredient.addProperty("item", inputId.toString());
        ingredients.add(ingredient);
        json.add("ingredients", ingredients);

        JsonArray results = new JsonArray();
        JsonObject resultJson = new JsonObject();
        resultJson.addProperty("id", BuiltInRegistries.ITEM.getKey(result.getItem()).toString());
        resultJson.addProperty("count", result.getCount());
        resultJson.addProperty("chance", 1.0F);
        results.add(resultJson);
        json.add("results", results);

        json.addProperty("processing_time", def.duration());
        json.addProperty("recipe_tier", def.tier());
        if (def.circuitNumber() >= 0) {
            json.addProperty("circuit_number", def.circuitNumber());
        }
        return json;
    }

    @Override
    public String getName() {
        return "Greate Milling Recipes";
    }
}
