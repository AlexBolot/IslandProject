package fr.unice.polytech.si3.qgl.ise.map;

import fr.unice.polytech.si3.qgl.ise.enums.Biome;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PathFinderTest {

    private IslandMap map;

    @Before
    public void init() {
        map = new IslandMap();

        Map<Biome, Double> biomesPercentage1 = new HashMap<>();
        biomesPercentage1.put(Biome.ALPINE, 24.8);
        biomesPercentage1.put(Biome.GLACIER, 75.2);

        Map<Biome, Double> biomesPercentage2 = new HashMap<>();
        biomesPercentage2.put(Biome.ALPINE, 63.4);
        biomesPercentage2.put(Biome.TUNDRA, 37.6);

        Map<Biome, Double> biomesPercentage3 = new HashMap<>();
        biomesPercentage3.put(Biome.TAIGA, 66.3);
        biomesPercentage3.put(Biome.TUNDRA, 33.7);

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
    }

    @Test
    public void calculateDistanceTest() {
        assertEquals(0.0, PathFinder.calculateDistance(new Coordinates(0, 0), new Coordinates(0, 0)), 0);
        assertEquals(5.0, PathFinder.calculateDistance(new Coordinates(0, 3), new Coordinates(4, 0)), 0);
        assertEquals(5.0, PathFinder.calculateDistance(new Coordinates(0, -3), new Coordinates(-4, 0)), 0);
        assertEquals(1.41421356, PathFinder.calculateDistance(new Coordinates(0, 1), new Coordinates(1, 0)), 0.00000001);
        assertEquals(93.14504817756, PathFinder.calculateDistance(new Coordinates(-30, -13), new Coordinates(-6, 77)), 0.00000000001);
    }

    @Test
    public void findNearestCreekTest() {
        Coordinates coordinates = new Coordinates(-2, 44);

        assertEquals("id2", PathFinder.findNearestCreek(map.getCreeks(), coordinates));

        map.addCreek(new Coordinates(-2, 42), "id4");

        assertEquals("id4", PathFinder.findNearestCreek(map.getCreeks(), coordinates));
    }

    @Test
    public void findNearestTileOfBiomeTest() {
        assertEquals(null, PathFinder.findNearestTileOfBiome(map, new Coordinates(0, 0), Biome.SHRUBLAND));
        assertEquals(new Coordinates(15, -34), PathFinder.findNearestTileOfBiome(map, new Coordinates(-20, -26), Biome.TAIGA));
        assertEquals(new Coordinates(15, -34), PathFinder.findNearestTileOfBiome(map, new Coordinates(0, 0), Biome.TUNDRA));
        assertEquals(new Coordinates(13, 39), PathFinder.findNearestTileOfBiome(map, new Coordinates(0, 0), Biome.ALPINE));
        assertEquals(new Coordinates(23, -40), PathFinder.findNearestTileOfBiome(map, new Coordinates(39, -45), Biome.GLACIER));
    }

    @Test
    public void findNearestTileOfResourceTest() {
        assertEquals(null, PathFinder.findNearestTileOfResource(map, new Coordinates(24, -42), RawResource.FRUITS));
        assertEquals(null, PathFinder.findNearestTileOfResource(map, new Coordinates(-17, 2), RawResource.FISH));
        assertEquals(new Coordinates(15, -34), PathFinder.findNearestTileOfResource(map, new Coordinates(6, -14), RawResource.WOOD));
        map.getTile(new Coordinates(15, -34)).setExplored(true);
        assertEquals(new Coordinates(13, 39), PathFinder.findNearestTileOfResource(map, new Coordinates(0, 0), RawResource.FUR));
        assertEquals(new Coordinates(13, 39), PathFinder.findNearestTileOfResource(map, new Coordinates(0, 0), RawResource.FLOWER));
        map.getTile(new Coordinates(13, 39)).setExplored(true);
        assertEquals(new Coordinates(23, -40), PathFinder.findNearestTileOfResource(map, new Coordinates(0, 0), RawResource.ORE));
    }

    @Test
    public void findNearestCreekOfResourceTest() {
        assertEquals("", PathFinder.findNearestCreekOfResource(map, RawResource.FRUITS));
        assertEquals("", PathFinder.findNearestCreekOfResource(map, RawResource.FISH));
        assertEquals("id1", PathFinder.findNearestCreekOfResource(map, RawResource.WOOD));
        assertEquals("id2", PathFinder.findNearestCreekOfResource(map, RawResource.FUR));
        assertEquals("id2", PathFinder.findNearestCreekOfResource(map, RawResource.FLOWER));
        assertEquals("id2", PathFinder.findNearestCreekOfResource(map, RawResource.ORE));
    }
}
