package fr.unice.polytech.si3.qgl.ise.map;

import fr.unice.polytech.si3.qgl.ise.CraftedContract;
import fr.unice.polytech.si3.qgl.ise.RawContract;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.Biome;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.RawResource;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static fr.unice.polytech.si3.qgl.ise.parsing.externalresources.ExtResSelector.bundle;
import static org.junit.Assert.assertEquals;

public class ForecasterTest {

    private IslandMap map;

    @Before
    public void init() {
        map = new IslandMap();

        Map<Biome, Double> biomesPercentage1 = new HashMap<>();
        biomesPercentage1.put(bundle().getBiome("ALPINE"), 100d);
        biomesPercentage1.put(bundle().getBiome("GLACIER"), 100d);

        Map<Biome, Double> biomesPercentage2 = new HashMap<>();
        biomesPercentage2.put(bundle().getBiome("ALPINE"), 100d);
        biomesPercentage2.put(bundle().getBiome("TUNDRA"), 100d);

        Map<Biome, Double> biomesPercentage3 = new HashMap<>();
        biomesPercentage3.put(bundle().getBiome("TAIGA"), 100d);
        biomesPercentage3.put(bundle().getBiome("TUNDRA"), 100d);

        Tile tile1 = new Tile(biomesPercentage1);
        Tile tile2 = new Tile(biomesPercentage2);
        Tile tile3 = new Tile(biomesPercentage3);

        map.addTile(new Coordinates(23, -40), tile1);
        map.addTile(new Coordinates(13, 39), tile2);
        map.addTile(new Coordinates(15, -34), tile3);
    }

    @Test
    public void estimateResourcesTest() {
        Map<RawResource, Double> foretoldQuantities = Forecaster.estimateResources(map);
        assertEquals(6.0, foretoldQuantities.get(bundle().getRawRes("ORE")), 0.0);
        assertEquals(40.0, foretoldQuantities.get(bundle().getRawRes("WOOD")), 0.0);
        assertEquals(10.0, foretoldQuantities.get(bundle().getRawRes("FUR")), 0.0);
        assertEquals(0.1, foretoldQuantities.get(bundle().getRawRes("FLOWER")), 0.0);
    }

    @Test
    public void estimateCostTest() {
        assertEquals(1286.613347776192, Forecaster.estimateCost(new RawContract(bundle().getRawRes("WOOD"), 1000)), 0.00000000001);
        assertEquals(321.6533369440481, Forecaster.estimateCost(new CraftedContract(bundle().getCraftedRes("PLANK"), 1000)), 0.00000000001);
        assertEquals(22259.8401121574, Forecaster.estimateCost(new CraftedContract(bundle().getCraftedRes("GLASS"), 200)), 0.00000000001);
    }
}
