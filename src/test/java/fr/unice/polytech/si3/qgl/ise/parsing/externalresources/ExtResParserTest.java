package fr.unice.polytech.si3.qgl.ise.parsing.externalresources;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static fr.unice.polytech.si3.qgl.ise.parsing.externalresources.ExtResParser.ExtResBundle;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.*;

public class ExtResParserTest {

    private final ArrayList<RawResource> rawResources = new ArrayList<>();
    private ArrayList<CraftedResource> craftedResources = new ArrayList<>();
    private ArrayList<Biome> biomes = new ArrayList<>();

    private JSONObject mainJSON;
    private JSONArray rawResourcesJSON;
    private JSONObject craftedResourcesJSON;
    private JSONObject biomesJSON;

    @Test
    public void canParseDefault() {
        try {
            String fullPath;
            URL path = ExtResParserTest.class.getClassLoader().getResource("external-resources.json");

            if (path == null) throw new FileNotFoundException("Path is null");

            fullPath = path.getFile();

            File file = new File(fullPath);

            if (file.isDirectory()) throw new IllegalStateException(fullPath + " is not a File");

            Scanner sc = new Scanner(file).useDelimiter("you'd never type that, would you \\?");
            ExtResParser extResParser = new ExtResParser();

            if (sc.hasNext()) extResParser.parse(sc.next());
            if (extResParser.raisedFlag()) Assert.fail("Flag was raised");

        } catch (Exception e) {
            Assert.fail("Exception was raised: " + e.getMessage());
        }
    }

    @Test
    public void raisedFlag() {
        ExtResParser parser = new ExtResParser();

        //No need of a real JSON since we simulate broken JSON

        parser.parse("Always code as if the guy who " +
                "ends up maintaining your code " +
                "will be a violent psychopath who " +
                "knows where you live.\n" +
                "Martin Golding\n");

        assertTrue(parser.raisedFlag());
    }

    @Test
    public void parse_Right() {

        generateMainJSON();

        for (int i = 0; i < 500; i++) {
            ExtResParser parser = new ExtResParser();
            ExtResBundle bundle = parser.parse(mainJSON.toString());

            sortAll(bundle);

            assertEquals(bundle.getRawResources(), rawResources);
            assertEquals(bundle.getCraftedResources(), craftedResources);
            assertEquals(bundle.getBiomes(), biomes);
        }
    }

    @Test
    public void parse_Boundaries() {
        generateRawResJSON(0);
        generateCraftedResJSON(0, 0);
        generateBiomesJSON(0, 0);

        mainJSON = new JSONObject();

        mainJSON.put("raw-resources", rawResourcesJSON);
        mainJSON.put("crafted-resources", craftedResourcesJSON);
        mainJSON.put("biomes", biomesJSON);

        ExtResParser parser = new ExtResParser();
        ExtResBundle bundle = parser.parse(mainJSON.toString());

        assertEquals(bundle.getRawResources(), rawResources);
        assertEquals(bundle.getCraftedResources(), craftedResources);
        assertEquals(bundle.getBiomes(), biomes);
    }

    @Test
    public void clearBundle() {
        generateMainJSON();

        ExtResParser parser = new ExtResParser();
        ExtResBundle bundle = parser.parse(mainJSON.toString());

        assertFalse(bundle.getRawResources().isEmpty());
        assertFalse(bundle.getCraftedResources().isEmpty());
        assertFalse(bundle.getBiomes().isEmpty());

        bundle.clear();

        assertTrue(bundle.getRawResources().isEmpty());
        assertTrue(bundle.getCraftedResources().isEmpty());
        assertTrue(bundle.getBiomes().isEmpty());
    }

    @Test
    public void containsAllLight() {

        ExtResBundle[] extResBundles = generate_3_Lightly_Different_Bundles();

        ExtResBundle bundle1 = extResBundles[0];
        ExtResBundle bundle2 = extResBundles[1];
        ExtResBundle bundle3 = extResBundles[2];

        assertTrue(bundle1.containsAllLight(bundle1)); //Since bundle1 contains itself (thanks capt. obvious)
        assertTrue(bundle1.containsAllLight(bundle2)); //Since bundle1 contains bundle2's values
        assertFalse(bundle2.containsAllLight(bundle1)); //Since bundle2 doesn't contain all bundle2's values
        assertFalse(bundle1.containsAllLight(bundle3)); //Since bundle1 and bundle3 are different -> not comparable
    }

    @Test
    public void compareLight() {
        ExtResBundle[] extResBundles = generate_3_Lightly_Different_Bundles();

        ExtResBundle bundle1 = extResBundles[0];
        ExtResBundle bundle2 = extResBundles[1];
        ExtResBundle bundle3 = extResBundles[2];

        assertEquals(0, bundle1.compareLight(bundle1)); //Since bundle1 contains itself (thanks capt. obvious)
        assertEquals(1, bundle1.compareLight(bundle2)); //Since bundle1 contains bundle2's values
        assertEquals(-1, bundle2.compareLight(bundle1)); //Since bundle2 doesn't contain all bundle2's values
        assertEquals(-2, bundle1.compareLight(bundle3)); //Since bundle1 and bundle3 are different -> not comparable
    }

    @Test
    public void containsAllDeep() {

        ExtResBundle[] extResBundles = generate_4_Deeply_Different_Bundles();

        ExtResBundle bundle1 = extResBundles[0];
        ExtResBundle bundle2 = extResBundles[1];
        ExtResBundle bundle3 = extResBundles[2];
        ExtResBundle bundle4 = extResBundles[3];

        assertTrue(bundle1.containsAllDeep(bundle1)); //Since bundle1 contains itself (thanks capt. obvious)
        assertTrue(bundle1.containsAllDeep(bundle2)); //Since bundle1 contains bundle2's values
        assertFalse(bundle2.containsAllDeep(bundle1)); //Since bundle2 doesn't contain all bundle2's values
        assertFalse(bundle1.containsAllDeep(bundle3)); //Since bundle1 and bundle3 are different (crafted -> same res but diff amount) -> not comparable
        assertFalse(bundle1.containsAllDeep(bundle4)); //Since bundle1 and bundle3 are different (crafted -> diff resources) -> not comparable
    }

    @Test
    public void compareDeep() {

        ExtResBundle[] extResBundles = generate_4_Deeply_Different_Bundles();

        ExtResBundle bundle1 = extResBundles[0];
        ExtResBundle bundle2 = extResBundles[1];
        ExtResBundle bundle3 = extResBundles[2];
        ExtResBundle bundle4 = extResBundles[3];

        assertEquals(0, bundle1.compareDeep(bundle1)); //Since bundle1 contains itself (thanks capt. obvious)
        assertEquals(1, bundle1.compareDeep(bundle2)); //Since bundle1 contains bundle2's values
        assertEquals(-1, bundle2.compareDeep(bundle1)); //Since bundle2 doesn't contain all bundle2's values
        assertEquals(-2, bundle1.compareDeep(bundle3)); //Since bundle1 and bundle3 are different (crafted -> same res but diff amount) -> not comparable
        assertEquals(-2, bundle1.compareDeep(bundle4)); //Since bundle1 and bundle3 are different (crafted -> diff resources) -> not comparable
    }

    //region ------------ utils ---------------

    private void generateMainJSON() {
        generateRawResJSON(randomInt());
        generateCraftedResJSON(randomInt(), randomInt());
        generateBiomesJSON(randomInt(), randomInt());

        mainJSON = new JSONObject();

        mainJSON.put("raw-resources", rawResourcesJSON);
        mainJSON.put("crafted-resources", craftedResourcesJSON);
        mainJSON.put("biomes", biomesJSON);
    }

    private void generateRawResJSON(int amount) {
        rawResources.clear();
        rawResourcesJSON = new JSONArray();

        for (int i = 0; i < amount; i++) {
            RawResource rawRes = new RawResource(randomString());
            rawResources.add(rawRes);
            rawResourcesJSON.put(rawRes.getName());

        }
    }

    private void generateCraftedResJSON(int amountCrafted, int amountRaw) {
        craftedResources.clear();
        craftedResourcesJSON = new JSONObject();

        for (int i = 0; i < amountCrafted; i++) {
            String craftedName;
            HashMap<RawResource, Double> recipe = new HashMap<>();
            JSONObject recipeJSON = new JSONObject();

            do {
                craftedName = randomString();
            } while (craftedResources.contains(new CraftedResource(craftedName, new HashMap<>())));

            for (int j = 0; j < amountRaw; j++) {
                RawResource rawRes = findAny(rawResources);
                Double amount = (double) randomInt();

                recipe.put(rawRes, amount);
                recipeJSON.put(rawRes.getName(), amount);
            }

            craftedResources.add(new CraftedResource(craftedName, recipe));
            craftedResourcesJSON.put(craftedName, recipeJSON);
        }
    }

    private void generateBiomesJSON(int amountBiomes, int amountRaw) {
        biomes.clear();
        biomesJSON = new JSONObject();

        for (int i = 0; i < amountBiomes; i++) {
            String biomeName;
            ArrayList<RawResource> possibleRawRes = new ArrayList<>();
            JSONArray possibleRawResJSON = new JSONArray();

            do {
                biomeName = randomString();
            } while (biomes.contains(new Biome(biomeName, new ArrayList<>())));

            for (int j = 0; j < amountRaw; j++) {
                RawResource rawRes = findAny(rawResources);

                possibleRawRes.add(rawRes);
                possibleRawResJSON.put(rawRes.getName());
            }

            biomes.add(new Biome(biomeName, possibleRawRes));
            biomesJSON.put(biomeName, possibleRawResJSON);
        }
    }

    private String randomString() {
        return randomUUID().toString().replace("-", "");
    }

    private int randomInt() {
        return new Random().nextInt(10) + 5;
    }

    private <T> T findAny(List<T> list) {
        return list.get(new Random().nextInt(list.size()));
    }

    private <T> void sortByHashCode(List<T> list) {
        list.sort(Comparator.comparing(T::hashCode));
    }

    private void sortAll(ExtResBundle bundle) {
        sortByHashCode(bundle.getRawResources());
        sortByHashCode(bundle.getCraftedResources());
        sortByHashCode(bundle.getBiomes());

        sortByHashCode(rawResources);
        sortByHashCode(craftedResources);
        sortByHashCode(biomes);
    }

    private HashMap<RawResource, Double> createRecipe(RawResource[] rawResources, double[] amounts) {
        return IntStream.range(0, rawResources.length - 1).boxed().collect(Collectors.toMap(i -> rawResources[i], i -> amounts[i], (a, b) -> b, HashMap::new));
    }

    private ExtResBundle[] generate_3_Lightly_Different_Bundles() {

        RawResource wood = new RawResource("WOOD");
        RawResource fur = new RawResource("FUR");
        RawResource ore = new RawResource("ORE");
        RawResource flower = new RawResource("FLOWER");
        RawResource quartz = new RawResource("QUARTZ");
        RawResource fish = new RawResource("FISH");

        CraftedResource glass = new CraftedResource("GLASS", createRecipe(new RawResource[]{wood, quartz}, new double[]{10, 5}));
        CraftedResource ingot = new CraftedResource("INGOT", createRecipe(new RawResource[]{wood, ore}, new double[]{5, 5}));
        CraftedResource leather = new CraftedResource("LEATHER", createRecipe(new RawResource[]{fur}, new double[]{3}));

        Biome lake = new Biome("LAKE", Collections.singletonList(fish));
        Biome beach = new Biome("BEACH", Collections.singletonList(quartz));
        Biome mangrove = new Biome("MANGROVE", Arrays.asList(wood, flower));

        ExtResBundle bundle1 = new ExtResBundle();
        ExtResBundle bundle2 = new ExtResBundle();
        ExtResBundle bundle3 = new ExtResBundle();

        bundle1.addRawRes(wood, fur, ore, flower, quartz);
        bundle2.addRawRes(wood, fur, ore);
        bundle3.addRawRes(wood, fur, ore, flower, fish);

        bundle1.addCraftedRes(glass, leather);
        bundle2.addCraftedRes(glass);
        bundle3.addCraftedRes(glass, ingot);

        bundle1.addBiome(lake, mangrove);
        bundle2.addBiome(lake);
        bundle3.addBiome(lake, beach);

        return new ExtResBundle[]{bundle1, bundle2, bundle3};
    }

    private ExtResBundle[] generate_4_Deeply_Different_Bundles() {

        RawResource wood = new RawResource("WOOD");
        RawResource fur = new RawResource("FUR");
        RawResource ore = new RawResource("ORE");
        RawResource flower = new RawResource("FLOWER");
        RawResource quartz = new RawResource("QUARTZ");
        RawResource fish = new RawResource("FISH");

        CraftedResource glass1 = new CraftedResource("GLASS", createRecipe(new RawResource[]{wood, quartz}, new double[]{10, 5}));
        CraftedResource glass2 = new CraftedResource("GLASS", createRecipe(new RawResource[]{wood, quartz}, new double[]{12, 3}));
        CraftedResource glass3 = new CraftedResource("GLASS", createRecipe(new RawResource[]{wood}, new double[]{10}));
        CraftedResource ingot = new CraftedResource("INGOT", createRecipe(new RawResource[]{wood, ore}, new double[]{5, 5}));
        CraftedResource leather = new CraftedResource("LEATHER", createRecipe(new RawResource[]{fur}, new double[]{3}));

        Biome lake = new Biome("LAKE", Collections.singletonList(fish));
        Biome beach = new Biome("BEACH", Collections.singletonList(quartz));
        Biome mangrove = new Biome("MANGROVE", Arrays.asList(wood, flower));

        ExtResBundle bundle1 = new ExtResBundle();
        ExtResBundle bundle2 = new ExtResBundle();
        ExtResBundle bundle3 = new ExtResBundle();
        ExtResBundle bundle4 = new ExtResBundle();

        bundle1.addRawRes(wood, fur, ore, flower, quartz);
        bundle2.addRawRes(wood, fur, ore);
        bundle3.addRawRes(wood, fur, ore, flower, fish);
        bundle4.addRawRes(wood, fur, ore, flower, fish);

        bundle1.addCraftedRes(glass1, leather);
        bundle2.addCraftedRes(glass1);
        bundle3.addCraftedRes(glass2, ingot);
        bundle4.addCraftedRes(glass3, ingot);

        bundle1.addBiome(lake, mangrove);
        bundle2.addBiome(lake);
        bundle3.addBiome(lake, beach);
        bundle4.addBiome(lake, beach);

        return new ExtResBundle[]{bundle1, bundle2, bundle3, bundle4};
    }
    //endregion
}