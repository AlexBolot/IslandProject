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
        assertEquals("The heading isn't parse correctly", DroneEnums.NSEW.WEST.getValue(), contractParser.getHeading());
    }

    @Test
    public void testMen() {
        assertEquals("The number of crew isn't parse correctly", 12, contractParser.getMen());
    }

    @Test
    public void testBudget() {
        assertEquals("The budget isn't parse correctly", 10000, contractParser.getBudget());
    }

    @Test
    public void testWood() {
        assertEquals("The number of wood isn't parse correctly", 600, contractParser.getRawContracts().get(0).getQuantity());
    }

    @Test
    public void testCraftedContract() {
        assertEquals("The contractParser in raw resource isn't calculated correctly", new Double(100),
                contractParser.getCraftedContracts().get(0).getRawQuantities().get(RawResource.SUGAR_CANE));
    }
}
