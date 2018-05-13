package fr.unice.polytech.si3.qgl.ise.parsing;

import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.Biome;
import org.junit.Before;
import org.junit.Test;

import static fr.unice.polytech.si3.qgl.ise.parsing.externalresources.ExtResSelector.bundle;
import static org.junit.Assert.assertEquals;

/**
 * @author Lucas OMS
 */
public class ScanTest {

    private Scan scan;

    //region --- utils for readability ---
    private static Biome biome(String name) {
        return bundle().getBiome(name);
    }

    @Before
    public void setup() {
        String dataIn = "{\"cost\": 2, \"extras\": { \"biomes\": " +
                "[\"OCEAN\", \"LAKE\", \"BEACH\", \"GRASSLAND\", " +
                "\"MANGROVE\", \"TROPICAL_RAIN_FOREST\", \"TROPICAL_SEASONAL_FOREST\", " +
                "\"TEMPERATE_DECIDUOUS_FOREST\", \"TEMPERATE_RAIN_FOREST\", \"TEMPERATE_DESERT\", " +
                "\"TAIGA\", \"SNOW\", \"TUNDRA\", \"ALPINE\", \"GLACIER\", " +
                "\"SHRUBLAND\", \"SUB_TROPICAL_DESERT\"], \"creeks\": [\"idC1\", \"idC2\"], \"sites\": [\"idS\"]}, \"status\": \"OK\"}";
        scan = new Scan(dataIn);
    }

    @Test
    public void parsingTestForBiomes() {
        assertEquals("Test OCEAN", biome("OCEAN"), scan.getBiomes().get(0));
        assertEquals("Test LAKE", biome("LAKE"), scan.getBiomes().get(1));
        assertEquals("Test BEACH", biome("BEACH"), scan.getBiomes().get(2));
        assertEquals("Test GRASSLAND", biome("GRASSLAND"), scan.getBiomes().get(3));

        assertEquals("Test MANGROVE", biome("MANGROVE"), scan.getBiomes().get(4));
        assertEquals("Test TROPICAL_RAIN_FOREST", biome("TROPICAL_RAIN_FOREST"), scan.getBiomes().get(5));
        assertEquals("Test TROPICAL_SEASONAL_FOREST", biome("TROPICAL_SEASONAL_FOREST"), scan.getBiomes().get(6));

        assertEquals("Test TEMPERATE_DECIDUOUS_FOREST", biome("TEMPERATE_DECIDUOUS_FOREST"), scan.getBiomes().get(7));
        assertEquals("Test TEMPERATE_RAIN_FOREST", biome("TEMPERATE_RAIN_FOREST"), scan.getBiomes().get(8));
        assertEquals("Test TEMPERATE_DESERT", biome("TEMPERATE_DESERT"), scan.getBiomes().get(9));

        assertEquals("Test TAIGA", biome("TAIGA"), scan.getBiomes().get(10));
        assertEquals("Test SNOW", biome("SNOW"), scan.getBiomes().get(11));
        assertEquals("Test TUNDRA", biome("TUNDRA"), scan.getBiomes().get(12));
        assertEquals("Test ALPINE", biome("ALPINE"), scan.getBiomes().get(13));
        assertEquals("Test GLACIER", biome("GLACIER"), scan.getBiomes().get(14));

        assertEquals("Test SHRUBLAND", biome("SHRUBLAND"), scan.getBiomes().get(15));
        assertEquals("Test SUB_TROPICAL_DESERT", biome("SUB_TROPICAL_DESERT"), scan.getBiomes().get(16));
    }

    @Test
    public void parsingTestForCreeks() {
        assertEquals("The scan must fin creek with id \"idC1\"", "idC1", scan.getCreeks().get(0));
        assertEquals("The scan must fin creek with id \"idC2\"", "idC2", scan.getCreeks().get(1));
    }
    //endregion
}
