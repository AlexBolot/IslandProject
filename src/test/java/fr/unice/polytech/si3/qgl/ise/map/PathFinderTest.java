package fr.unice.polytech.si3.qgl.ise.map;

import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.Biome;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.RawResource;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static fr.unice.polytech.si3.qgl.ise.parsing.externalresources.ExtResSelector.bundle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PathFinderTest {

    private IslandMap map;

    @Before
    public void init() {
        map = new IslandMap();

        Map<Biome, Double> biomesPercentage1 = new HashMap<>();
        biomesPercentage1.put(biome("ALPINE"), 100d);
        biomesPercentage1.put(biome("GLACIER"), 100d);

        Map<Biome, Double> biomesPercentage2 = new HashMap<>();
        biomesPercentage2.put(biome("ALPINE"), 100d);
        biomesPercentage2.put(biome("TUNDRA"), 100d);

        Map<Biome, Double> biomesPercentage3 = new HashMap<>();
        biomesPercentage3.put(biome("TAIGA"), 100d);
        biomesPercentage3.put(biome("TUNDRA"), 100d);

        Tile tile1 = new Tile(biomesPercentage1);
        Tile tile2 = new Tile(biomesPercentage2);
        Tile tile3 = new Tile(biomesPercentage3);

        map.addTile(new Coordinates(23, -40), tile1);
        map.addTile(new Coordinates(13, 39), tile2);
        map.addTile(new Coordinates(15, -34), tile3);

        map.addCreek(new Coordinates(30, 4), "id0");
        map.addCreek(new Coordinates(30, -28), "id1");
        map.addCreek(new Coordinates(10, 40), "id2");
        map.addCreek(new Coordinates(-11, 11), "id3");

        map.setShip(new Coordinates(0, 0));
    }

    @Test
    public void calculateDistanceTest() {
        assertEquals(0, PathFinder.calculateDistance(new Coordinates(0, 0), new Coordinates(0, 0)), 0);
        assertEquals(5, PathFinder.calculateDistance(new Coordinates(0, 3), new Coordinates(4, 0)), 0);
        assertEquals(5, PathFinder.calculateDistance(new Coordinates(0, -3), new Coordinates(-4, 0)), 0);
        assertEquals(1.41421356, PathFinder.calculateDistance(new Coordinates(0, 1), new Coordinates(1, 0)), 0.00000001);
        assertEquals(93.14504817756, PathFinder.calculateDistance(new Coordinates(-30, -13), new Coordinates(-6, 77)), 0.00000000001);
    }

    @Test
    public void findNearestTileOfBiomeTest() {
        assertNull(PathFinder.findNearestTileOfBiome(map, new Coordinates(0, 0), biome("SHRUBLAND")));
        assertEquals(new Coordinates(15, -34), PathFinder.findNearestTileOfBiome(map, new Coordinates(-20, -26), biome("TAIGA")));
        assertEquals(new Coordinates(15, -34), PathFinder.findNearestTileOfBiome(map, new Coordinates(0, 0), biome("TUNDRA")));
        assertEquals(new Coordinates(13, 39), PathFinder.findNearestTileOfBiome(map, new Coordinates(0, 0), biome("ALPINE")));
        assertEquals(new Coordinates(23, -40), PathFinder.findNearestTileOfBiome(map, new Coordinates(39, -45), biome("GLACIER")));
    }

    @Test
    public void findNearestTileOfResourceTest() {
        assertNull(PathFinder.findNearestTileOfResource(map, new Coordinates(24, -42), rawRes("FRUITS")));
        assertNull(PathFinder.findNearestTileOfResource(map, new Coordinates(-17, 2), rawRes("FISH")));
        assertEquals(new Coordinates(15, -34), PathFinder.findNearestTileOfResource(map, new Coordinates(6, -14), rawRes("WOOD")));
        map.getTile(new Coordinates(15, -34)).setExplored(true);
        assertEquals(new Coordinates(13, 39), PathFinder.findNearestTileOfResource(map, new Coordinates(0, 0), rawRes("FUR")));
        assertEquals(new Coordinates(13, 39), PathFinder.findNearestTileOfResource(map, new Coordinates(0, 0), rawRes("FLOWER")));
        map.getTile(new Coordinates(13, 39)).setExplored(true);
    }

    @Test
    public void findBestCreekTest() {
        assertEquals("", PathFinder.findBestCreek(map, new ArrayList<>(Arrays.asList(rawRes("FRUITS"), rawRes("FISH")))));
        assertEquals("id1", PathFinder.findBestCreek(map, new ArrayList<>(Arrays.asList(rawRes("WOOD"), rawRes("SUGAR_CANE")))));
        assertEquals("id2", PathFinder.findBestCreek(map, new ArrayList<>(Arrays.asList(rawRes("FLOWER"), rawRes("ORE")))));
        assertEquals("id2", PathFinder.findBestCreek(map, new ArrayList<>(Arrays.asList(rawRes("FLOWER"), rawRes("ORE"), rawRes("WOOD"), rawRes("FUR")))));
    }

    //region --- utils for lisibility ---
    private static RawResource rawRes(String name) {
        return bundle().getRawRes(name);
    }

    private static Biome biome(String name) {
        return bundle().getBiome(name);
    }
    //endregion

}
