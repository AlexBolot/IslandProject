package fr.unice.polytech.si3.qgl.ise.maps;

import fr.unice.polytech.si3.qgl.ise.enums.Biome;
import fr.unice.polytech.si3.qgl.ise.enums.Exploitability;
import fr.unice.polytech.si3.qgl.ise.enums.Abundance;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import org.junit.Before;
import org.junit.Test;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ExplorerMapTest {

    private ExplorerMap explorerMap;
    private List<ExplorerTile> explorerTiles;

    @Before
    public void init() {
        explorerMap = new ExplorerMap();

        Map<Biome, Double> biomesPercentage = new HashMap<>();
        biomesPercentage.put(Biome.ALPINE, 30.8);
        biomesPercentage.put(Biome.GLACIER, 69.2);

        Map<RawResource, Tuple2<Abundance, Exploitability>> resourcesStats = new HashMap<>();

        resourcesStats.put(RawResource.ORE, new Tuple2<>(Abundance.HIGH, Exploitability.HARSH));
        resourcesStats.put(RawResource.WOOD, new Tuple2<>(Abundance.LOW, Exploitability.EASY));

        explorerTiles = new ArrayList<>();
        explorerTiles.add(new ExplorerTile());
        explorerTiles.add(new ExplorerTile(biomesPercentage, new HashMap<>()));
        explorerTiles.add(new ExplorerTile(biomesPercentage, resourcesStats));
    }

    @Test
    public void addTileTest() {
        explorerMap.addTile(new Coordinates(1, 1), explorerTiles.get(0));
        assertEquals(explorerTiles.get(0), explorerMap.getTile(new Coordinates(1, 1)));
        assertNotEquals(explorerTiles.get(1), explorerMap.getTile(new Coordinates(1, 1)));

        explorerMap.addTile(new Coordinates(19865, -4566), explorerTiles.get(1));
        assertEquals(explorerTiles.get(1), explorerMap.getTile(new Coordinates(19865, -4566)));
        assertNotEquals(explorerTiles.get(0), explorerMap.getTile(new Coordinates(19865, -4566)));

        explorerMap.addTile(new Coordinates(19865, -4566), explorerTiles.get(0));
        assertEquals(explorerTiles.get(0), explorerMap.getTile(new Coordinates(19865, -4566)));
        assertNotEquals(explorerTiles.get(1), explorerMap.getTile(new Coordinates(19865, -4566)));

        explorerMap.addTile(new Coordinates(1, 1), new ExplorerTile());
        assertEquals(explorerTiles.get(0), explorerMap.getTile(new Coordinates(1, 1)));
        assertNotEquals(explorerTiles.get(1), explorerMap.getTile(new Coordinates(1, 1)));
    }
}
