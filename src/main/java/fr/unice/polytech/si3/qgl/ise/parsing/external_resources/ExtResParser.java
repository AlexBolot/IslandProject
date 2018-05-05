package fr.unice.polytech.si3.qgl.ise.parsing.external_resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class ExtResParser {

    //region --------------- Attributes ---------------

    //private static final Logger logger = getLogger(ExtResParser.class);
    private static ExtResBundle EXT_RES_BUNDLE = new ExtResBundle();
    private boolean errorFlag = false;
    //endregion

    //region --------------- Constructor ---------------

    //endregion

    //region --------------- Getters and Setters ---------------

    public boolean raisedFlag() {
        return errorFlag;
    }

    public static ExtResBundle ResBundle() {
        return EXT_RES_BUNDLE;
    }
    //endregion

    //region --------------- Parsing Methods ---------------

    public void parse(String jsonContent) {
        errorFlag = false;
        EXT_RES_BUNDLE = new ExtResBundle();

        try {
            JSONObject data = new JSONObject(jsonContent);

            JSONArray jsonRawRes = data.getJSONArray("raw-resources");
            JSONObject jsonCraftedRes = data.getJSONObject("crafted-resources");
            JSONObject jsonBiomes = data.getJSONObject("biomes");

            parseRawResources(jsonRawRes);
            parseCraftedResources(jsonCraftedRes);
            parseBiomes(jsonBiomes);

        } catch (Exception exeption) {
            //logger.info(exeption.getMessage());
            exeption.printStackTrace();
            errorFlag = true;
        }
    }

    private void parseRawResources(JSONArray jsonRawRes) {
        jsonRawRes.forEach(rawResName -> {
            assertFalse(String.valueOf(rawResName).trim().isEmpty(), "Raw Resource's Name is empty");
            assertFalse(ResBundle().hasRawRes((String) rawResName), "Raw Resource already exists");

            ResBundle().addRawRes(new RawResource((String) rawResName));
        });
    }

    private void parseCraftedResources(JSONObject jsonCraftedRes) {
        jsonCraftedRes.keySet().forEach(craftedResName -> {
            assertFalse(craftedResName.trim().isEmpty(), "Crafted Resource's Name is empty");
            assertFalse(ResBundle().hasCraftedRes(craftedResName), "CraftedResource already exists");

            HashMap<RawResource, Double> recipe = new HashMap<>();

            JSONObject jsonRecipe = jsonCraftedRes.getJSONObject(craftedResName);
            jsonRecipe.keySet().forEach(rawResName -> {
                assertFalse(String.valueOf(rawResName).trim().isEmpty(), "Raw Resource is empty");
                assertTrue(ResBundle().hasRawRes(rawResName), "Raw Resource doesn't exist");

                recipe.put(new RawResource(rawResName), jsonRecipe.getDouble(rawResName));
            });

            ResBundle().addCraftedRes(new CraftedResource(craftedResName, recipe));
        });
    }

    private void parseBiomes(JSONObject jsonBiomes) {
        jsonBiomes.keySet().forEach(biomeName -> {
            assertFalse(biomeName.trim().isEmpty(), "Biome' Name is empty");
            assertFalse(ResBundle().hasBiome(biomeName), "Biome already exists");

            ArrayList<RawResource> possibleRes = new ArrayList<>();

            JSONArray jsonPossibleRes = jsonBiomes.getJSONArray(biomeName);
            jsonPossibleRes.forEach(rawResName -> {
                assertFalse(String.valueOf(rawResName).trim().isEmpty(), "Raw Resource is empty");
                assertTrue(ResBundle().hasRawRes((String) rawResName), "Raw Resource doesn't exist");

                possibleRes.add(new RawResource((String) rawResName));
            });

            ResBundle().addBiome(new Biome(biomeName, possibleRes));
        });
    }

    //endregion

    //region --------------- Validator Methods ---------------

    private void assertTrue(boolean bool, String message) {
        if (!bool) throw new JSONException(message);
    }

    private void assertFalse(boolean bool, String message) {
        if (bool) throw new JSONException(message);
    }

    //endregion

    public static class ExtResBundle {

        //region --------------- Attributes ---------------

        private List<Biome> biomes = new ArrayList<>();
        private List<RawResource> rawResources = new ArrayList<>();
        private List<CraftedResource> craftedResources = new ArrayList<>();

        //endregion

        //region --------------- Getters and Setters ---------------

        public List<Biome> getBiomes() {
            return biomes;
        }

        public List<CraftedResource> getCraftedResources() {
            return craftedResources;
        }

        public List<RawResource> getRawResources() {
            return rawResources;
        }

        //endregion

        //region --------------- Contains Methods ---------------

        public boolean hasBiome(String biomeName) {
            return biomes.stream().anyMatch(biome -> biome.isSame(biomeName));
        }

        public boolean hasRawRes(String rawResName) {
            return rawResources.stream().anyMatch(rawRes -> rawRes.isSame(rawResName));
        }

        public boolean hasCraftedRes(String craftedResName) {
            return craftedResources.stream().anyMatch(craftedRes -> craftedRes.isSame(craftedResName));
        }

        //endregion

        //region --------------- Adders ---------------

        private void addRawRes(RawResource rawRes) {
            rawResources.add(rawRes);
        }

        private void addCraftedRes(CraftedResource craftedRes) {
            craftedResources.add(craftedRes);
        }

        private void addBiome(Biome biome) {
            biomes.add(biome);
        }

        //endregion
    }

}
