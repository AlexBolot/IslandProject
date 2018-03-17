package fr.unice.polytech.si3.qgl.ise.parsing;

import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ContractTest {

    private ContractParser contractParser;

    @Before
    public void setup() {
        contractParser = new ContractParser("{ \n" +
                "  \"men\": 12,\n" +
                "  \"budget\": 10000,\n" +
                "  \"contracts\": [\n" +
                "    { \"amount\": 600, \"resource\": \"WOOD\" },\n" +
                "    { \"amount\": 20, \"resource\": \"SUGAR_CANE\" },\n" +
                "    { \"amount\": 10, \"resource\": \"RUM\" },\n" +
                "    { \"amount\": 200, \"resource\": \"LEATHER\" }\n" +
                "  ],\n" +
                "  \"heading\": \"W\"\n" +
                "}");
    }

    @Test
    public void testHeading() {
        assertEquals("The heading isn't parse correctly", contractParser.getHeading(), DroneEnums.NSEW.WEST.getValue());
    }

    @Test
    public void testMen() {
        assertEquals("The number of crew isn't parse correctly", contractParser.getMen(), 12);
    }

    @Test
    public void testBudget() {
        assertEquals("The budget isn't parse correctly", contractParser.getBudget(), 10000);
    }

    @Test
    public void testWood() {
        assertEquals("The number of wood isn't parse correctly", contractParser.getRawContracts().get(0).getQuantity(), 600);
    }

    @Test
    public void testCraftedContract() {
        assertEquals("The contractParser in raw resource isn't calculated correctly", new Double(100),
                contractParser.getCraftedContracts().get(0).getRawQuantities().get(RawResource.SUGAR_CANE));
    }
}
