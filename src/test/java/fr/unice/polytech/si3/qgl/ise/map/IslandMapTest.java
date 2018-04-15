package fr.unice.polytech.si3.qgl.ise.map;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.*;
import fr.unice.polytech.si3.qgl.ise.parsing.Scan;
import org.junit.Before;
import org.junit.Ignore;
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

        Map<Biome, Double> biomesPercentage = new HashMap<>();
        biomesPercentage.put(Biome.ALPINE, 30.8);
        biomesPercentage.put(Biome.GLACIER, 69.2);

        Map<RawResource, Tuple2<Abundance, Exploitability>> resourcesStats = new HashMap<>();

        resourcesStats.put(RawResource.ORE, new Tuple2<>(Abundance.HIGH, Exploitability.HARSH));
        resourcesStats.put(RawResource.WOOD, new Tuple2<>(Abundance.LOW, Exploitability.EASY));

        tiles = new ArrayList<>();
        tiles.add(new Tile());
        tiles.add(new Tile(biomesPercentage));
        tiles.add(new Tile(biomesPercentage));
        tiles.add(new Tile(biomesPercentage));
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

        assertEquals(5, islandMap.getCreeks().size());
        assertEquals(new Coordinates(0, 0), islandMap.getCreeks().get("id0"));
        assertEquals(new Coordinates(4986, -49876), islandMap.getCreeks().get("id2"));
        assertEquals(new Coordinates(4, 1), islandMap.getCreeks().get("id3"));
        assertEquals(new Coordinates(-11, -489), islandMap.getCreeks().get("id4"));
    }

    @Test
    public void addSiteTest() {
        islandMap.addSite(new Coordinates(4986, -49876), "id");

        assertEquals("id", islandMap.getEmergencySite()._1);
        assertEquals(new Coordinates(4986, -49876), islandMap.getEmergencySite()._2);
    }

    @Test
    public void testGetTileWhenExists() {
        Tile expected = new Tile();
        islandMap.addTile(new Coordinates(50, 50), expected);
        assertSame("The returned Tile must be a reference, not a copy", expected, islandMap.getTile(new Coordinates(50, 50)));
    }

    @Test
    public void testGetTileWhenNotExists() {
        Tile expected = islandMap.getTile(new Coordinates(1337, 1337));
        assertTrue("If the tile has never been created, the getter must set it before returning it",
                expected == islandMap.getTile(new Coordinates(1337, 1337)) && expected != null);
    }

    @Test
    public void testTilesToUpdates() {
        List<List<Tile>> expected = islandMap.getTileToUpdateFrom(0, 0);
        assertEquals("It must be 7 layers of accuracy", 7, expected.size());
        assertEquals("The layer 1 must have 9 tiles", 9, expected.get(0).size());
        assertEquals("The layer 2 must have 12 tiles", 12, expected.get(1).size());
        assertEquals("The layer 3 must have 16 tiles", 16, expected.get(2).size());
        assertEquals("The layer 4 must have 20 tiles", 20, expected.get(3).size());
        assertEquals("The layer 5 must have 12 tiles", 12, expected.get(4).size());
        assertEquals("The layer 6 must have 8 tiles", 8, expected.get(5).size());
        assertEquals("The layer 7 must have 4 tiles", 4, expected.get(6).size());
    }

    @Test
    public void testTilesToUpdatesBiomes() {
        Drone drone = new Drone(islandMap, DroneEnums.NSEW.EAST);
        //After acknowledging, all tiles must have the biome in their list
        drone.acknowledgeScan(new Scan("{\"cost\":6,\"extras\":{\"creeks\":[],\"biomes\":[\"MANGROVE\"],\"sites\":[]},\"status\":\"OK\"}"));
        int size = 0;
        for (List<Tile> layer : islandMap.getTileToUpdateFrom(0, 0)) {
            for (Tile tile : layer) {
                assertTrue(tile.getPossibleBiomes().contains(Biome.MANGROVE));
                ++size;
            }
        }
        assertEquals("There should be 81 tiles to update", 81, size);
    }
}
