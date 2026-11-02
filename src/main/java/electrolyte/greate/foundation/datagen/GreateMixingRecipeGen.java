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
import net.minecraft.world.item.Items;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.gregtechceu.gtceu.api.GTValues.HV;
import static com.gregtechceu.gtceu.api.GTValues.LV;
import static com.gregtechceu.gtceu.api.GTValues.MV;

public class GreateMixingRecipeGen implements DataProvider {

    private record RecipeDef(Material inputA, int countA, java.util.function.Supplier<ItemStack> inputB,
                             ItemStack output, int outputCount, String heat, int duration, int tier) {
    }

    private final PackOutput output;

    public GreateMixingRecipeGen(PackOutput output) {
        this.output = output;
    }

    private static List<RecipeDef> recipes() {
        List<RecipeDef> recipes = new ArrayList<>();

        recipes.add(new RecipeDef(GTMaterials.Iron, 2, () -> new ItemStack(Items.COAL),
                ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Steel), 1, "heated", 200, LV));

        recipes.add(new RecipeDef(GTMaterials.Copper, 2,
                () -> ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Zinc),
                ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Brass), 3, "none", 160, MV));

        recipes.add(new RecipeDef(GTMaterials.Titanium, 1,
                () -> ChemicalHelper.get(TagPrefix.dust, GTMaterials.Carbon),
                ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Steel), 1, "heated", 300, HV));
        return recipes;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (RecipeDef def : recipes()) {
            ItemStack inputA = ChemicalHelper.get(TagPrefix.ingot, def.inputA());
            ItemStack inputB = def.inputB().get();
            ItemStack result = def.output();
            if (inputA.isEmpty() || inputB.isEmpty() || result.isEmpty()) {
                Greate.LOGGER.warn("[Greate] datagen: 跳过搅拌配方，GT 物品为空（{}）", def.inputA().getName());
                continue;
            }
            ResourceLocation aId = BuiltInRegistries.ITEM.getKey(inputA.getItem());
            ResourceLocation bId = BuiltInRegistries.ITEM.getKey(inputB.getItem());
            ResourceLocation outId = BuiltInRegistries.ITEM.getKey(result.getItem());
            String name = outId.getPath() + "_from_" + aId.getPath() + "_and_" + bId.getPath();
            Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                    .resolve(Greate.MOD_ID + "/recipe/mixing/" + name + ".json");
            futures.add(DataProvider.saveStable(cache, buildJson(inputA, def.countA(), inputB, result, def), path));
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private static JsonObject buildJson(ItemStack inputA, int countA, ItemStack inputB, ItemStack result,
                                        RecipeDef def) {
        JsonObject json = new JsonObject();
        json.addProperty("type", Greate.MOD_ID + ":mixing");

        JsonArray ingredients = new JsonArray();
        JsonObject a = new JsonObject();
        a.addProperty("item", BuiltInRegistries.ITEM.getKey(inputA.getItem()).toString());
        a.addProperty("count", countA);
        ingredients.add(a);
        JsonObject b = new JsonObject();
        b.addProperty("item", BuiltInRegistries.ITEM.getKey(inputB.getItem()).toString());
        ingredients.add(b);
        json.add("ingredients", ingredients);

        JsonArray results = new JsonArray();
        JsonObject out = new JsonObject();
        out.addProperty("id", BuiltInRegistries.ITEM.getKey(result.getItem()).toString());
        out.addProperty("count", def.outputCount());
        results.add(out);
        json.add("results", results);

        json.addProperty("heat_requirement", def.heat());
        json.addProperty("processing_time", def.duration());
        json.addProperty("recipe_tier", def.tier());
        return json;
    }

    @Override
    public String getName() {
        return "Greate Mixing Recipes";
    }
}
