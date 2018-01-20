package fr.unice.polytech.si3.qgl.ise.maps;

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

public class ExplorerTileTest {
    private List<ExplorerTile> explorerTiles;
    private Map<RawResource, Tuple2<Abundance, Exploitability>> resourcesStats;
    private Map<Biome, Double> biomesPercentage;

    @Before
    public void init() {
        biomesPercentage = new HashMap<>();
        biomesPercentage.put(Biome.ALPINE, 30.8);
        biomesPercentage.put(Biome.GLACIER, 69.2);

        resourcesStats = new HashMap<>();

        resourcesStats.put(RawResource.ORE, new Tuple2<>(Abundance.HIGH, Exploitability.HARSH));
        resourcesStats.put(RawResource.WOOD, new Tuple2<>(Abundance.LOW, Exploitability.EASY));

        explorerTiles = new ArrayList<>();
        explorerTiles.add(new ExplorerTile());
        explorerTiles.add(new ExplorerTile(biomesPercentage, new HashMap<>()));
        explorerTiles.add(new ExplorerTile(biomesPercentage, resourcesStats));
    }

    @Test
    public void equalsTest() {
        assertEquals(explorerTiles.get(0), new ExplorerTile());
        assertEquals(explorerTiles.get(1), new ExplorerTile(biomesPercentage, new HashMap<>()));
        assertEquals(explorerTiles.get(2), new ExplorerTile(biomesPercentage, resourcesStats));

        assertNotEquals(explorerTiles.get(0), new ExplorerTile(biomesPercentage, new HashMap<>()));
        assertNotEquals(explorerTiles.get(1), new ExplorerTile(biomesPercentage, resourcesStats));
        assertNotEquals(explorerTiles.get(2), new ExplorerTile());

        assertNotEquals(new ExplorerTile(), new DroneTile());
    }

    @Test
    public void hashCodeTest() {
        assertEquals(explorerTiles.get(0).hashCode(), new ExplorerTile().hashCode());
        assertEquals(explorerTiles.get(1).hashCode(), new ExplorerTile(biomesPercentage, new HashMap<>()).hashCode());
        assertEquals(explorerTiles.get(2).hashCode(), new ExplorerTile(biomesPercentage, resourcesStats).hashCode());

        assertNotEquals(explorerTiles.get(0).hashCode(), new ExplorerTile(biomesPercentage, new HashMap<>()).hashCode());
        assertNotEquals(explorerTiles.get(1).hashCode(), new ExplorerTile(biomesPercentage, resourcesStats).hashCode());
        assertNotEquals(explorerTiles.get(2).hashCode(), new ExplorerTile().hashCode());

        assertNotEquals(new ExplorerTile().hashCode(), new DroneTile().hashCode());
    }

    @Test
    public void copyTest() {
        assertEquals(explorerTiles.get(0), explorerTiles.get(0).makeCopy());
        assertEquals(explorerTiles.get(1), explorerTiles.get(1).makeCopy());
        assertEquals(explorerTiles.get(2), explorerTiles.get(2).makeCopy());

        assertNotEquals(explorerTiles.get(0), explorerTiles.get(1).makeCopy());
        assertNotEquals(explorerTiles.get(1), explorerTiles.get(2).makeCopy());
        assertNotEquals(explorerTiles.get(2), explorerTiles.get(0).makeCopy());

        assertNotEquals(new ExplorerTile(), new DroneTile().makeCopy());
    }
}
