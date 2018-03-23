package fr.unice.polytech.si3.qgl.ise.parsing;

import fr.unice.polytech.si3.qgl.ise.enums.Abundance;
import fr.unice.polytech.si3.qgl.ise.enums.Exploitability;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static fr.unice.polytech.si3.qgl.ise.enums.RawResource.*;
import static fr.unice.polytech.si3.qgl.ise.enums.Abundance.*;
import static fr.unice.polytech.si3.qgl.ise.enums.Exploitability.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExploreParsingTest {

    private ExploreParsing exploreParsing;
    private RawResource rawResource;
    private Abundance abundance;
    private Exploitability exploitability;


    @Test
    public void parsingTest() {
        for (int i = 0; i < 100; i++) {
            rawResource = generateResource();
            abundance = generateAbundance();
            exploitability = generateExploitability();
            exploreParsing = new ExploreParsing(generateData(rawResource, abundance, exploitability));
            assertTrue(exploreParsing.getResources().containsKey(rawResource));
            assertEquals(abundance, exploreParsing.getResources().get(rawResource));
        }
    }

    private RawResource generateResource() {
        return RawResource.values()[new Random().nextInt(RawResource.values().length - 1)];
    }

    private Abundance generateAbundance() {
        return Abundance.values()[new Random().nextInt(Abundance.values().length - 1)];
    }

    private Exploitability generateExploitability() {
        return Exploitability.values()[new Random().nextInt(Exploitability.values().length - 1)];
    }

    private String generateData(RawResource rawResource, Abundance abundance, Exploitability exploitability) {
        return "{\"cost\": 5,\"extras\": {\"resources\": [{ \"amount\": \"" + abundance + "\", \"resource\": \"" + rawResource + "\", \"cond\": \"" + exploitability + "\" }],\"pois\": [{\"kind\": \"Creek\", \"id\": \"43e3eb42-50f0-47c5-afa3-16cd3d50faff\"}]},\"status\": \"OK\"}";
    }

}
