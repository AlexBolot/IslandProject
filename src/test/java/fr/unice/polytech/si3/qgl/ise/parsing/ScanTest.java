package fr.unice.polytech.si3.qgl.ise.parsing;

import fr.unice.polytech.si3.qgl.ise.enums.Biome;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScanTest {

    @Test
    public void parsingTest() {
        String dataIn = "{\"cost\": 2, \"extras\": { \"biomes\": [\"BEACH\"], \"creeks\": [], \"sites\": [\"id\"]}, \"status\": \"OK\"}";
        Scan parsedData = new Scan(dataIn);
        assertEquals(0, parsedData.getCreeks().size());
        assertEquals("id", parsedData.getEmergencySites().get(0));
        assertEquals(Biome.BEACH, parsedData.getBiomes().get(0));
    }
}
