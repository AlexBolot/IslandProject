package fr.unice.polytech.si3.qgl.ise.parsing.externalresources;

import org.junit.Assert;
import org.junit.Test;
import scala.Tuple2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CraftedResourceTest {

    @Test
    public void requiresResource_Right() {
        for (int i = 0; i < 500; i++) {
            CraftedResource craftedResource = randomCrafted();
            craftedResource.getRecipe().keySet().stream().map(craftedResource::requiresResource).forEach(Assert::assertTrue);
        }
    }

    @Test
    public void requiresResource_Fail() {
        CraftedResource craftedResource = randomCrafted();
        String newName;

        do {
            newName = randomString();
        } while (craftedResource.getRecipe().containsKey(new RawResource(newName)));

        assertFalse(craftedResource.requiresResource(new RawResource(newName)));
    }

    @Test
    public void amountRequired_Right() {
        for (int i = 0; i < 500; i++) {
            CraftedResource craftedResource = randomCrafted();
            craftedResource.getRecipe().keySet().forEach(rawRes -> assertEquals(0, Double.compare(craftedResource.amountRequired(rawRes), craftedResource.getRecipe().get(rawRes))));
        }
    }

    @Test
    public void amountRequired_Fail() {
        CraftedResource craftedResource = randomCrafted();
        RawResource rawRes;

        do {
            rawRes = new RawResource(randomString());
        } while (craftedResource.getRecipe().containsKey(rawRes));

        assertEquals(0, Double.compare(0, craftedResource.amountRequired(rawRes)));
    }

    @Test
    public void compareTo() {

        Tuple2<String, Double> wood_5 = new Tuple2<>("WOOD", 5d);
        Tuple2<String, Double> fur_5 = new Tuple2<>("FUR", 5d);
        Tuple2<String, Double> fur_3 = new Tuple2<>("FUR", 3d);
        Tuple2<String, Double> flower_10 = new Tuple2<>("FLOWER", 10d);
        Tuple2<String, Double> quartz_1 = new Tuple2<>("QUARTZ", 1d);

        CraftedResource craft1 = new CraftedResource(randomString(), recipe(Arrays.asList(wood_5, fur_5, flower_10)));
        CraftedResource craft2 = new CraftedResource(randomString(), recipe(Arrays.asList(wood_5, fur_5, flower_10)));
        CraftedResource craft3 = new CraftedResource(randomString(), recipe(Arrays.asList(wood_5, fur_5)));
        CraftedResource craft4 = new CraftedResource(randomString(), recipe(Arrays.asList(wood_5, fur_3, flower_10)));
        CraftedResource craft5 = new CraftedResource(randomString(), recipe(Arrays.asList(wood_5, fur_5, quartz_1)));

        assertEquals(0, craft1.compareTo(craft2)); //Since craft1 and craft2 have same recipes
        assertEquals(1, craft1.compareTo(craft3)); //Since craft1 contains craft3
        assertEquals(-1, craft3.compareTo(craft1)); //Since craft3 is contained by craft1
        assertEquals(-2, craft1.compareTo(craft4)); //Since biome1 and biome4 are different (same res but diff amount) -> not comparable
        assertEquals(-2, craft1.compareTo(craft5)); //Since biome1 and biome4 are different (diff resources) -> not comparable
    }

    @Test
    public void toString_() {
        CraftedResource craftedResource = randomCrafted();
        assertEquals(craftedResource.toString(), craftedResource.getName() + " : " + craftedResource.getRecipe().entrySet());
    }

    //region ------------ utils ---------------

    private CraftedResource randomCrafted() {
        ArrayList<Tuple2<String, Double>> tuples = IntStream.range(0, randomInt()).mapToObj(i -> new Tuple2<>(randomString(), (double) randomInt())).collect(Collectors.toCollection(ArrayList::new));
        return new CraftedResource(randomString(), recipe(tuples));
    }

    private HashMap<RawResource, Double> recipe(List<Tuple2<String, Double>> tuples) {
        return tuples.stream().collect(Collectors.toMap(tuple -> new RawResource(tuple._1), tuple -> tuple._2, (a, b) -> b, HashMap::new));
    }

    private String randomString() {
        return randomUUID().toString().replace("-", "");
    }

    private int randomInt() {
        return new Random().nextInt(10);
    }

    //endregion
}