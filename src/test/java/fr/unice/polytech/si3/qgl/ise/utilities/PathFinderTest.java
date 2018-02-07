package fr.unice.polytech.si3.qgl.ise.utilities;

import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PathFinderTest {

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
        IslandMap map = new IslandMap();

        map.addCreek(new Coordinates(945, 990), "id0");
        map.addCreek(new Coordinates(648, -976), "id1");
        map.addCreek(new Coordinates(-670, 300), "id2");
        map.addCreek(new Coordinates(376, 833), "id3");
        map.addSite(new Coordinates(-146, -975), "id");

        assertEquals("id1", PathFinder.findNearestCreek(map.getCreeks(), map.getEmergencySite()));

        map.addCreek(new Coordinates(-146, -975), "id4");

        assertEquals("id4", PathFinder.findNearestCreek(map.getCreeks(), map.getEmergencySite()));
    }
}
