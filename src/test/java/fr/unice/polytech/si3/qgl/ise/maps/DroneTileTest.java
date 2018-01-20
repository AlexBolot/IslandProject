package fr.unice.polytech.si3.qgl.ise.maps;

import fr.unice.polytech.si3.qgl.ise.enums.Biome;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DroneTileTest {
    private List<DroneTile> droneTiles;
    private List<Biome> possibleBiomes;

    @Before
    public void init() {
        possibleBiomes = new ArrayList<>();
        possibleBiomes.add(Biome.ALPINE);
        possibleBiomes.add(Biome.GLACIER);

        droneTiles = new ArrayList<>();
        droneTiles.add(new DroneTile());
        droneTiles.add(new DroneTile("creek", "site",  new ArrayList<>()));
        droneTiles.add(new DroneTile("creek", "site", possibleBiomes));
    }

    @Test
    public void equalsTest() {
        assertEquals(droneTiles.get(0), new DroneTile());
        assertEquals(droneTiles.get(1), new DroneTile("creek", "site", new ArrayList<>()));
        assertEquals(droneTiles.get(2), new DroneTile("creek", "site", possibleBiomes));

        assertNotEquals(droneTiles.get(0), new DroneTile("creek", "site", new ArrayList<>()));
        assertNotEquals(droneTiles.get(1), new DroneTile("creek", "site", possibleBiomes));
        assertNotEquals(droneTiles.get(2), new DroneTile());

        assertNotEquals(new DroneTile(), new ExplorerTile());
    }

    @Test
    public void hashCodeTest() {
        assertEquals(droneTiles.get(0).hashCode(), new DroneTile().hashCode());
        assertEquals(droneTiles.get(1).hashCode(), new DroneTile("creek", "site", new ArrayList<>()).hashCode());
        assertEquals(droneTiles.get(2).hashCode(), new DroneTile("creek", "site", possibleBiomes).hashCode());

        assertNotEquals(droneTiles.get(0).hashCode(), new DroneTile("creek", "site", new ArrayList<>()).hashCode());
        assertNotEquals(droneTiles.get(1).hashCode(), new DroneTile("creek", "site", possibleBiomes).hashCode());
        assertNotEquals(droneTiles.get(2).hashCode(), new DroneTile().hashCode());

        assertNotEquals(new DroneTile().hashCode(), new ExplorerTile().hashCode());
    }

    @Test
    public void copyTest() {
        assertEquals(droneTiles.get(0), droneTiles.get(0).makeCopy());
        assertEquals(droneTiles.get(1), droneTiles.get(1).makeCopy());
        assertEquals(droneTiles.get(2), droneTiles.get(2).makeCopy());

        assertNotEquals(droneTiles.get(0), droneTiles.get(1).makeCopy());
        assertNotEquals(droneTiles.get(1), droneTiles.get(2).makeCopy());
        assertNotEquals(droneTiles.get(2), droneTiles.get(0).makeCopy());

        assertNotEquals(new DroneTile(), new ExplorerTile().makeCopy());
    }
}
