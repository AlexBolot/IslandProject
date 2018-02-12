package fr.unice.polytech.si3.qgl.ise.map;

import fr.unice.polytech.si3.qgl.ise.enums.Abundance;
import fr.unice.polytech.si3.qgl.ise.enums.Biome;
import fr.unice.polytech.si3.qgl.ise.enums.Exploitability;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;
import org.junit.Before;
import org.junit.Test;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TileTest {
    private List<Tile> tiles;
    private Map<Biome, Double> biomesPercentage;
    private Map<RawResource, Tuple2<Abundance, Exploitability>> resourcesStats;

    @Before
    public void init() {
        biomesPercentage = new HashMap<>();
        biomesPercentage.put(Biome.ALPINE, 30.8);
        biomesPercentage.put(Biome.GLACIER, 69.2);

        resourcesStats = new HashMap<>();

        resourcesStats.put(RawResource.ORE, new Tuple2<>(Abundance.HIGH, Exploitability.HARSH));
        resourcesStats.put(RawResource.WOOD, new Tuple2<>(Abundance.LOW, Exploitability.EASY));

        tiles = new ArrayList<>();
        tiles.add(new Tile());                              //tiles.get(0)
        tiles.add(new Tile(biomesPercentage));              //tiles.get(1)
        tiles.add(new Tile(biomesPercentage));              //tiles.get(2)
        tiles.add(new Tile(biomesPercentage));              //tiles.get(3)
        tiles.get(3).setResourcesStats(resourcesStats);
    }

    @Test
    public void equalsTest() {
        assertEquals(tiles.get(0), new Tile());
        assertNotEquals(tiles.get(1), new Tile());

        assertEquals(tiles.get(1), new Tile(biomesPercentage));
        assertNotEquals(tiles.get(2), new Tile());

        Tile tile2 = new Tile(biomesPercentage);
        tile2.addBiomesPercentage(biomesPercentage);

        assertEquals(tiles.get(2), tile2);
        assertNotEquals(tiles.get(3), tile2);

        Tile tile3 = new Tile(biomesPercentage);
        tile3.addBiomesPercentage(biomesPercentage);
        tile3.setResourcesStats(resourcesStats);

        assertEquals(tiles.get(3), tile3);
        assertNotEquals(tiles.get(0), tile3);
    }

    @Test
    public void hashCodeTest() {
        assertEquals(tiles.get(0).hashCode(), new Tile().hashCode());
        assertNotEquals(tiles.get(1).hashCode(), new Tile().hashCode());

        assertEquals(tiles.get(1).hashCode(), new Tile(biomesPercentage).hashCode());
        assertNotEquals(tiles.get(2).hashCode(), new Tile().hashCode());

        Tile tile2 = new Tile(biomesPercentage);
        tile2.addBiomesPercentage(biomesPercentage);

        assertEquals(tiles.get(2).hashCode(), tile2.hashCode());
        assertNotEquals(tiles.get(3).hashCode(), tile2.hashCode());

        Tile tile3 = new Tile(biomesPercentage);
        tile3.addBiomesPercentage(biomesPercentage);
        tile3.setResourcesStats(resourcesStats);

        assertEquals(tiles.get(3).hashCode(), tile3.hashCode());
        assertNotEquals(tiles.get(0).hashCode(), tile3.hashCode());
    }
}
