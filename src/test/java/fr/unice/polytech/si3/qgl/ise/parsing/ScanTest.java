package fr.unice.polytech.si3.qgl.ise.parsing;

import fr.unice.polytech.si3.qgl.ise.enums.Biome;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Lucas OMS
 */
public class ScanTest {

    private String dataIn;

    @Before
    public void setup() {
        dataIn = "{\"cost\": 2, \"extras\": { \"biomes\": " +
                "[\"OCEAN\", \"LAKE\", \"BEACH\", \"GRASSLAND\", " +
                "\"MANGROVE\", \"TROPICAL_RAIN_FOREST\", \"TROPICAL_SEASONAL_FOREST\", " +
                "\"TEMPERATE_DECIDUOUS_FOREST\", \"TEMPERATE_RAIN_FOREST\", \"TEMPERATE_DESERT\", " +
                "\"TAIGA\", \"SNOW\", \"TUNDRA\", \"ALPINE\", \"GLACIER\", " +
                "\"SHRUBLAND\", \"SUB_TROPICAL_DESERT\"], \"creeks\": [\"idC\"], \"sites\": [\"idS\"]}, \"status\": \"OK\"}";
    }

    @Test
    public void parsingTestForBiomes() {
        Scan scan = new Scan(dataIn);
        assertEquals("Test OCEAN", Biome.OCEAN, scan.getBiomes().get(0));
        assertEquals("Test LAKE", Biome.LAKE, scan.getBiomes().get(1));
        assertEquals("Test BEACH", Biome.BEACH, scan.getBiomes().get(2));
        assertEquals("Test GRASSLAND", Biome.GRASSLAND, scan.getBiomes().get(3));

        assertEquals("Test MANGROVE", Biome.MANGROVE, scan.getBiomes().get(4));
        assertEquals("Test TROPICAL_RAIN_FOREST", Biome.TROPICAL_RAIN_FOREST, scan.getBiomes().get(5));
        assertEquals("Test TROPICAL_SEASONAL_FOREST", Biome.TROPICAL_SEASONAL_FOREST, scan.getBiomes().get(6));

        assertEquals("Test TEMPERATE_DECIDUOUS_FOREST", Biome.TEMPERATE_DECIDUOUS_FOREST, scan.getBiomes().get(7));
        assertEquals("Test TEMPERATE_RAIN_FOREST", Biome.TEMPERATE_RAIN_FOREST, scan.getBiomes().get(8));
        assertEquals("Test TEMPERATE_DESERT", Biome.TEMPERATE_DESERT, scan.getBiomes().get(9));

        assertEquals("Test TAIGA", Biome.TAIGA, scan.getBiomes().get(10));
        assertEquals("Test SNOW", Biome.SNOW, scan.getBiomes().get(11));
        assertEquals("Test TUNDRA", Biome.TUNDRA, scan.getBiomes().get(12));
        assertEquals("Test ALPINE", Biome.ALPINE, scan.getBiomes().get(13));
        assertEquals("Test GLACIER", Biome.GLACIER, scan.getBiomes().get(14));

        assertEquals("Test SHRUBLAND", Biome.SHRUBLAND, scan.getBiomes().get(15));
        assertEquals("Test SUB_TROPICAL_DESERT", Biome.SUB_TROPICAL_DESERT, scan.getBiomes().get(16));
    }
}
