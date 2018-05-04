package fr.unice.polytech.si3.qgl.ise.parsing;

import fr.unice.polytech.si3.qgl.ise.Explorer;
import fr.unice.polytech.si3.qgl.ise.parsing.external_resources.Biome;
import fr.unice.polytech.si3.qgl.ise.parsing.external_resources.CraftedResource;
import fr.unice.polytech.si3.qgl.ise.parsing.external_resources.RawResource;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static org.apache.logging.log4j.LogManager.getLogger;

public class ExtResParser {

    private static final Logger logger = getLogger(Explorer.class);

    private ArrayList<Biome> biomes;
    private ArrayList<RawResource> rawResources;
    private ArrayList<CraftedResource> craftedResources;

    private boolean errorFlag;

    public ExtResParser(String jsonContent) {
        try {
            errorFlag = false;
            biomes = new ArrayList<>();
            rawResources = new ArrayList<>();
            craftedResources = new ArrayList<>();

            JSONObject data = new JSONObject(jsonContent);

            JSONArray jsonRawRes = data.getJSONArray("raw-resources");
            JSONArray jsonCraftedRes = data.getJSONArray("crafted-resources");
            JSONObject jsonBiomes = data.getJSONObject("biomes");
            JSONObject jsonRecipes = data.getJSONObject("recipes");

            parseRawResources(jsonRawRes);
            parseCraftedResources(jsonCraftedRes, jsonRecipes);
            parseBiomes(jsonBiomes);

            biomes.forEach(System.out::println);

        } catch (Exception exeption) {
            logger.info(exeption.getMessage());
            exeption.printStackTrace();
            errorFlag = true;
        }
    }


    public ArrayList<Biome> getBiomes() {
        return biomes;
    }

    public ArrayList<CraftedResource> getCraftedResources() {
        return craftedResources;
    }

    public ArrayList<RawResource> getRawResources() {
        return rawResources;
    }

    public boolean raisedFlag() {
        return errorFlag;
    }

    private void parseRawResources(JSONArray jsonRawRes) {
        jsonRawRes.forEach(rawResName -> rawResources.add(new RawResource((String) rawResName)));
    }

    private void parseCraftedResources(JSONArray jsonCraftedRes, JSONObject jsonRecipes) {
        jsonCraftedRes.forEach(craftedResName -> {
            HashMap<RawResource, Double> recipe = new HashMap<>();

            JSONObject jsonRecipe = jsonRecipes.getJSONObject((String) craftedResName);
            jsonRecipe.keySet().forEach(rawResName -> recipe.put(new RawResource(rawResName), jsonRecipe.getDouble(rawResName)));

            craftedResources.add(new CraftedResource((String) craftedResName, recipe));
        });
    }

    private void parseBiomes(JSONObject jsonBiomes) {
        jsonBiomes.keySet().forEach(biomeName -> {
            ArrayList<RawResource> possibleRes = new ArrayList<>();

            JSONArray jsonPossibleRes = jsonBiomes.getJSONArray(biomeName);
            jsonPossibleRes.forEach(rawResName -> possibleRes.add(new RawResource((String) rawResName)));

            biomes.add(new Biome(biomeName, possibleRes));
        });
    }
}
