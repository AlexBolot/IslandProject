package fr.unice.polytech.si3.qgl.ise.parsing.externalresources;

import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.apache.logging.log4j.LogManager.getLogger;

@SuppressWarnings("WeakerAccess")
public class ExtResParser {

    private static final Logger logger = getLogger(ExtResParser.class);
    private boolean errorFlag = false;
    private final ExtResBundle bundle = new ExtResBundle();

    public boolean raisedFlag() {
        return errorFlag;
    }

    //region --------------- Parsing Methods ---------------

    public ExtResBundle parse(String jsonContent) {
        errorFlag = false;
        try {
            JSONObject data = new JSONObject(jsonContent);

            JSONArray jsonRawRes = data.getJSONArray("raw-resources");
            JSONObject jsonCraftedRes = data.getJSONObject("crafted-resources");
            JSONObject jsonBiomes = data.getJSONObject("biomes");

            parseRawResources(jsonRawRes);
            parseCraftedResources(jsonCraftedRes);
            parseBiomes(jsonBiomes);

        } catch (Exception e) {
            logger.info("Error while parsing json : " + e.getMessage());
            errorFlag = true;
        }

        return bundle;
    }

    private void parseRawResources(JSONArray jsonRawRes) {
        jsonRawRes.forEach(rawResName -> {
            assertFalse(String.valueOf(rawResName).trim().isEmpty(), "Raw Resource's Name is empty");
            assertFalse(bundle.hasRawRes((String) rawResName), "Raw Resource already exists");

            bundle.addRawRes(new RawResource((String) rawResName));
        });
    }

    private void parseCraftedResources(JSONObject jsonCraftedRes) {

        jsonCraftedRes.keySet().forEach(craftedResName -> {
            assertFalse(craftedResName.trim().isEmpty(), "Crafted Resource's Name is empty");
            assertFalse(bundle.hasCraftedRes(craftedResName), "CraftedResource already exists");

            HashMap<RawResource, Double> recipe = new HashMap<>();

            JSONObject jsonRecipe = jsonCraftedRes.getJSONObject(craftedResName);
            jsonRecipe.keySet().forEach(rawResName -> {
                assertFalse(String.valueOf(rawResName).trim().isEmpty(), "Raw Resource is empty");
                assertTrue(bundle.hasRawRes(rawResName), "Raw Resource doesn't exist");

                recipe.put(new RawResource(rawResName), jsonRecipe.getDouble(rawResName));
            });

            bundle.addCraftedRes(new CraftedResource(craftedResName, recipe));
        });
    }

    private void parseBiomes(JSONObject jsonBiomes) {
        jsonBiomes.keySet().forEach(biomeName -> {
            assertFalse(biomeName.trim().isEmpty(), "Biome' Name is empty");
            assertFalse(bundle.hasBiome(biomeName), "Biome already exists");

            ArrayList<RawResource> possibleRes = new ArrayList<>();

            JSONArray jsonPossibleRes = jsonBiomes.getJSONArray(biomeName);
            jsonPossibleRes.forEach(rawResName -> {
                assertFalse(String.valueOf(rawResName).trim().isEmpty(), "Raw Resource is empty");
                assertTrue(bundle.hasRawRes((String) rawResName), "Raw Resource doesn't exist");

                possibleRes.add(new RawResource((String) rawResName));
            });

            bundle.addBiome(new Biome(biomeName, possibleRes));
        });
    }

    //endregion

    //region --------------- Validator Methods ---------------

    @SuppressWarnings("SameParameterValue")
    private void assertTrue(boolean bool, String message) {
        if (!bool) throw new JSONException(message);
    }

    private void assertFalse(boolean bool, String message) {
        if (bool) throw new JSONException(message);
    }

    //endregion

    public static class ExtResBundle {

        private List<RawResource> rawResources = new ArrayList<>();
        private List<CraftedResource> craftedResources = new ArrayList<>();
        private List<Biome> biomes = new ArrayList<>();

        //region --------------- Getters and Setters ---------------

        public List<RawResource> getRawResources() {
            return rawResources;
        }

        public List<CraftedResource> getCraftedResources() {
            return craftedResources;
        }

        public List<Biome> getBiomes() {
            return biomes;
        }

        public RawResource getRawRes(String name) {
            return getRawResources().stream()
                    .filter(rawRes -> rawRes.sameName(name))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("This raw resource does not exist !"));
        }

        public CraftedResource getCraftedRes(String name) {
            return getCraftedResources().stream()
                    .filter(craftedRes -> craftedRes.sameName(name))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("This crafted resource does not exist !"));
        }

        public Biome getBiome(String name) {
            return getBiomes().stream()
                    .filter(biome -> biome.sameName(name))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("This biome does not exist !"));
        }

        //endregion

        //region --------------- Contains Methods ---------------

        public boolean hasBiome(String biomeName) {
            return biomes.stream().anyMatch(biome -> biome.sameName(biomeName));
        }

        public boolean hasRawRes(String rawResName) {
            return rawResources.stream().anyMatch(rawRes -> rawRes.sameName(rawResName));
        }

        public boolean hasCraftedRes(String craftedResName) {
            return craftedResources.stream().anyMatch(craftedRes -> craftedRes.sameName(craftedResName));
        }

        public boolean containsAllLight(ExtResBundle that) {
            boolean thisContainsRawRes = this.getRawResources().containsAll(that.getRawResources());
            boolean thisContainsCraftedRes = this.getCraftedResources().containsAll(that.getCraftedResources());
            boolean thisContainsBiomes = this.getBiomes().containsAll(that.getBiomes());

            return thisContainsRawRes && thisContainsCraftedRes && thisContainsBiomes;
        }

        public boolean containsAllDeep(ExtResBundle that) {
            for (CraftedResource craftedRes : that.getCraftedResources()) {

                if (!this.getCraftedResources().contains(craftedRes)) return false;

                int indexInThis = this.getCraftedResources().indexOf(craftedRes);
                CraftedResource toCompare = this.getCraftedResources().get(indexInThis);

                if (craftedRes.compareTo(toCompare) != 0) return false;
            }

            for (Biome biome : that.getBiomes()) {

                if (!this.getBiomes().contains(biome)) return false;

                int indexInThis = this.getBiomes().indexOf(biome);
                Biome toCompare = this.getBiomes().get(indexInThis);

                if (biome.compareTo(toCompare) != 0) return false;
            }

            return true;
        }

        //endregion

        //region --------------- Comparing Methods ---------------

        /**
         * Compares [that] bundle with [this], checking if bundles contain each other
         *
         * @param that ExtResBundle to compare with [this]
         * @return An int value, depending of the content of the bundles :<br>
         * 0 : [this] and [that] contain each other <br>
         * 1 : [this] contains [that] but not reciprocally <br>
         * -1 : [that] contains [this] but not reciprocally <br>
         * -2 : [this] and [that] don't contain each other
         */
        public int compareLight(ExtResBundle that) {

            boolean thisContainsThat = this.containsAllLight(that);
            boolean thatContainsThis = that.containsAllLight(this);

            if (!thisContainsThat && !thatContainsThis) return -2;
            else return Boolean.compare(thisContainsThat, thatContainsThis);
        }

        /**
         * Compares [that] bundle with [this], checking if bundles contain each other
         *
         * @param that ExtResBundle to compare with [this]
         * @return An int value, depending of the content of the bundles :<br>
         * 0 : [this] and [that] contain each other <br>
         * 1 : [this] contains [that] but not reciprocally <br>
         * -1 : [that] contains [this] but not reciprocally <br>
         * -2 : [this] and [that] don't contain each other
         */
        public int compareDeep(ExtResBundle that) {

            int compareLight = compareLight(that);
            if (compareLight != 0) return compareLight;

            boolean thisContainsThat = this.containsAllDeep(that);
            boolean thatContainsThis = that.containsAllDeep(this);

            if (!thisContainsThat && !thatContainsThis) return -2;
            else return Boolean.compare(thisContainsThat, thatContainsThis);
        }

        //endregion

        public void addRawRes(RawResource... rawRes) {
            rawResources.addAll(Arrays.asList(rawRes));
        }

        public void addCraftedRes(CraftedResource... craftedRes) {
            craftedResources.addAll(Arrays.asList(craftedRes));
        }

        public void addBiome(Biome... biome) {
            biomes.addAll(Arrays.asList(biome));
        }

        public void clear() {
            getRawResources().clear();
            getCraftedResources().clear();
            getBiomes().clear();
        }
    }

}
