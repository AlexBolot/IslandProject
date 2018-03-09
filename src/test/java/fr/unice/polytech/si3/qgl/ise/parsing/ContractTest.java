package fr.unice.polytech.si3.qgl.ise.parsing;

import fr.unice.polytech.si3.qgl.ise.Contract;
import fr.unice.polytech.si3.qgl.ise.enums.CraftedResource;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ContractTest {

    private Contract contract;

    @Before
    public void setup() {
        contract = new Contract("{ \n" +
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
        assertEquals("The heading isn't parse correctly", contract.getHeading(), DroneEnums.NSEW.WEST.getValue());
    }

    @Test
    public void testMen() {
        assertEquals("The number of crew isn't parse correctly", contract.getMen(), 12);
    }

    @Test
    public void testBudget() {
        assertEquals("The budget isn't parse correctly", contract.getBudget(), 10000);
    }

    @Test
    public void testWood() {
        assertEquals("The number of wood isn't parse correctly", contract.getAmountOf(RawResource.WOOD), 600);
    }

    @Test
    public void testFur() {
        assertEquals("The number of fur isn't parse correctly, it should be calculated parsing Leather",
                contract.getAmountOf(RawResource.FUR), 600);
    }

    @Test
    public void testAddWithRawAndCrafted() {
        assertEquals(new Integer(10), contract.getCraftedRessource().get(CraftedResource.RUM));
        assertEquals("The number of suger cane isn't parse correctly, it should be calculated parsing rum",
                contract.getAmountOf(RawResource.SUGAR_CANE), 20 + 10 * 10);
        assertEquals("The number of fruit isn't parse correctly, it should be calculated parsing rum",
                contract.getAmountOf(RawResource.FRUITS), 10);
    }
}
