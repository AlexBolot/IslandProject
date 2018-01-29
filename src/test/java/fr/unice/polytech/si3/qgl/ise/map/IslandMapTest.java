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

import static org.junit.Assert.*;

public class IslandMapTest {

    private IslandMap islandMap;
    private List<Tile> tiles;

    @Before
    public void init() {
        islandMap = new IslandMap();

        List<Biome> possibleBiomes = new ArrayList<>();
        possibleBiomes.add(Biome.ALPINE);
        possibleBiomes.add(Biome.GLACIER);

        Map<Biome, Double> biomesPercentage = new HashMap<>();
        biomesPercentage.put(Biome.ALPINE, 30.8);
        biomesPercentage.put(Biome.GLACIER, 69.2);

        Map<RawResource, Tuple2<Abundance, Exploitability>> resourcesStats = new HashMap<>();

        resourcesStats.put(RawResource.ORE, new Tuple2<>(Abundance.HIGH, Exploitability.HARSH));
        resourcesStats.put(RawResource.WOOD, new Tuple2<>(Abundance.LOW, Exploitability.EASY));

        tiles = new ArrayList<>();
        tiles.add(new Tile());
        tiles.add(new Tile(possibleBiomes));
        tiles.add(new Tile(possibleBiomes));
        tiles.get(2).setBiomesPercentage(biomesPercentage);
        tiles.add(new Tile(possibleBiomes));
        tiles.get(3).setBiomesPercentage(biomesPercentage);
        tiles.get(3).setResourcesStats(resourcesStats);
    }

    @Test
    public void addTileTest() {
        islandMap.addTile(new Coordinates(0, 0), tiles.get(0));
        assertEquals(tiles.get(0), islandMap.getTile(new Coordinates(0, 0)));
        assertNotEquals(tiles.get(1), islandMap.getTile(new Coordinates(0, 0)));

        islandMap.addTile(new Coordinates(-4668, 489861), tiles.get(1));
        assertEquals(tiles.get(1), islandMap.getTile(new Coordinates(-4668, 489861)));
        assertNotEquals(tiles.get(0), islandMap.getTile(new Coordinates(-4668, 489861)));

        islandMap.addTile(new Coordinates(-4668, 489861), tiles.get(2));
        assertEquals(tiles.get(2), islandMap.getTile(new Coordinates(-4668, 489861)));
        assertNotEquals(tiles.get(1), islandMap.getTile(new Coordinates(-4668, 489861)));

        islandMap.addTile(new Coordinates(0, 0), tiles.get(3));
        assertEquals(tiles.get(3), islandMap.getTile(new Coordinates(0, 0)));
        assertNotEquals(tiles.get(0), islandMap.getTile(new Coordinates(0, 0)));
    }

    @Test
    public void addCreekTest() {
        islandMap.addCreek(new Coordinates(0, 0), "id0");
        islandMap.addCreek(new Coordinates(0, 0), "id1");
        islandMap.addCreek(new Coordinates(4986, -49876), "id2");
        islandMap.addCreek(new Coordinates(4, 1), "id3");
        islandMap.addCreek(new Coordinates(-11, -489), "id4");

        assertEquals(4, islandMap.getCreeks().size());
        assertEquals("id1", islandMap.getCreeks().get(new Coordinates(0, 0)));
        assertEquals("id2", islandMap.getCreeks().get(new Coordinates(4986, -49876)));
        assertEquals("id3", islandMap.getCreeks().get(new Coordinates(4, 1)));
        assertEquals("id4", islandMap.getCreeks().get(new Coordinates(-11, -489)));
    }

    @Test
    public void addSiteTest() {
        islandMap.addSite(new Coordinates(0, 0), "id0");
        islandMap.addSite(new Coordinates(0, 0), "id1");
        islandMap.addSite(new Coordinates(4986, -49876), "id2");
        islandMap.addSite(new Coordinates(4, 1), "id3");
        islandMap.addSite(new Coordinates(-11, -489), "id4");

        assertEquals(4, islandMap.getSites().size());
        assertEquals("id1", islandMap.getSites().get(new Coordinates(0, 0)));
        assertEquals("id2", islandMap.getSites().get(new Coordinates(4986, -49876)));
        assertEquals("id3", islandMap.getSites().get(new Coordinates(4, 1)));
        assertEquals("id4", islandMap.getSites().get(new Coordinates(-11, -489)));
    }

    @Test
    public void testGetTileWhenExists() {
        Tile expected = new Tile();
        islandMap.addTile(new Coordinates(50, 50), expected);
        assertTrue("The returned Tile must be a reference, not a copy",
                expected == islandMap.getTile(new Coordinates(50, 50)));
    }

    @Test
    public void testGetTileWhenNotExists() {
        Tile expected = islandMap.getTile(new Coordinates(1337, 1337));
        assertTrue("If the tile has never been created, the getter must set it before returning it",
                expected == islandMap.getTile(new Coordinates(1337, 1337)) && expected != null);
    }
}
