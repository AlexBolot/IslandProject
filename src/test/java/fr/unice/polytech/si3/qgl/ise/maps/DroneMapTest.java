package fr.unice.polytech.si3.qgl.ise.maps;

import fr.unice.polytech.si3.qgl.ise.enums.Biome;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DroneMapTest {

    private DroneMap droneMap;
    private List<DroneTile> droneTiles;

    @Before
    public void init() {
        droneMap = new DroneMap();

        List<Biome> possibleBiomes = new ArrayList<>();
        possibleBiomes.add(Biome.ALPINE);
        possibleBiomes.add(Biome.GLACIER);

        droneTiles = new ArrayList<>();
        droneTiles.add(new DroneTile());
        droneTiles.add(new DroneTile("creek", "site", possibleBiomes));
    }

    @Test
    public void addTileTest() {
        droneMap.addTile(new Coordinates(0, 0), droneTiles.get(0));
        assertEquals(droneTiles.get(0), droneMap.getTile(new Coordinates(0, 0)));
        assertNotEquals(droneTiles.get(1), droneMap.getTile(new Coordinates(0, 0)));

        droneMap.addTile(new Coordinates(-4668, 489861), droneTiles.get(1));
        assertEquals(droneTiles.get(1), droneMap.getTile(new Coordinates(-4668, 489861)));
        assertNotEquals(droneTiles.get(0), droneMap.getTile(new Coordinates(-4668, 489861)));

        droneMap.addTile(new Coordinates(-4668, 489861), droneTiles.get(0));
        assertEquals(droneTiles.get(0), droneMap.getTile(new Coordinates(-4668, 489861)));
        assertNotEquals(droneTiles.get(1), droneMap.getTile(new Coordinates(-4668, 489861)));

        droneMap.addTile(new Coordinates(0, 0), new ExplorerTile());
        assertEquals(droneTiles.get(0), droneMap.getTile(new Coordinates(0, 0)));
        assertNotEquals(droneTiles.get(1), droneMap.getTile(new Coordinates(0, 0)));
    }
}
