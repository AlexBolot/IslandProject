package fr.unice.polytech.si3.qgl.ise.entities;

import fr.unice.polytech.si3.qgl.ise.contracts.CraftedContract;
import fr.unice.polytech.si3.qgl.ise.contracts.RawContract;
import fr.unice.polytech.si3.qgl.ise.enums.CraftedResource;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class CrewTest {
    private Crew crew;

    @Before
    public void setup() {
        List<RawContract> rawContracts = new ArrayList<>();
        List<CraftedContract> craftedContracts = new ArrayList<>();
        rawContracts.add(new RawContract(RawResource.WOOD, 500));
        craftedContracts.add(new CraftedContract(CraftedResource.PLANK, 60));

        crew = new Crew(new IslandMap(), rawContracts, craftedContracts);
    }

    @Test
    public void finishRawContractTest() {
        crew.addToStock(RawResource.WOOD, 500);
        assertTrue(crew.getCompletedRawContracts().isEmpty());
        assertFalse(crew.getRawContracts().isEmpty());
        crew.tryToFinishContracts();
        assertFalse("There should be a completed raw contract", crew.getCompletedRawContracts().isEmpty());
        assertTrue("All raw contracts should be completed", crew.getRawContracts().isEmpty());
    }

    @Test
    public void finishCraftedContractTest() {
        crew.addToCraftedStock(CraftedResource.PLANK, 60);
        assertTrue(crew.getCompletedCraftedContracts().isEmpty());
        assertFalse(crew.getCraftedContracts().isEmpty());
        crew.tryToFinishContracts();
        assertFalse("There should be a completed crafted contract", crew.getCompletedCraftedContracts().isEmpty());
        assertTrue("All crafted contracts should be completed", crew.getCraftedContracts().isEmpty());
    }

    @Test
    public void canCraftResource() {
        crew.addToStock(RawResource.WOOD, 15);
        Map<RawResource, Double> res = crew.tryCrafting();
        assertEquals(15.0, res.get(RawResource.WOOD), 0);
    }

    @Test
    public void notEnoughToCraftResource() {
        crew.addToStock(RawResource.WOOD, 10);
        Map<RawResource, Double> res = crew.tryCrafting();
        assertNull("No resource should be crafted here", res);
    }

    @Test
    public void nothingToCraft() {
        crew.addToCraftedStock(CraftedResource.PLANK, 60);
        crew.tryToFinishContracts();
        Map<RawResource, Double> res = crew.tryCrafting();
        assertNull("No resource should be crafted here", res);
    }

    @Test
    public void newWantedResource() {
        List<RawResource> res = crew.getWantedResources();
        assertEquals(RawResource.WOOD, res.get(0));
        crew.getRawContracts().add(new RawContract(RawResource.ORE, 10));
        crew.tryToFinishContracts();
        res = crew.getWantedResources();
        assertEquals(RawResource.WOOD, res.get(0));
        assertEquals(RawResource.ORE, res.get(1));
    }

    @Test
    public void removeFromWantedResource() {
        crew.getRawContracts().add(new RawContract(RawResource.ORE, 10));
        crew.addToStock(RawResource.WOOD, 500);
        crew.addToCraftedStock(CraftedResource.PLANK, 60);
        crew.tryToFinishContracts();
        List<RawResource> res = crew.getWantedResources();
        assertEquals(RawResource.ORE, res.get(0));
        assertEquals("There should be only 1 wanted resource left", 1, res.size());
    }

    @Test
    public void changeFocusRaw() {
        crew.getRawContracts().add(new RawContract(RawResource.ORE, 10));
        crew.tryToFinishContracts();
        assertEquals(RawResource.WOOD, crew.getCurrentResource());
        crew.addToStock(RawResource.WOOD, 500);
        crew.tryToFinishContracts();
        assertEquals(RawResource.ORE, crew.getCurrentResource());
    }

    @Test
    public void changeFocusCrafted() {
        crew.getRawContracts().add(new RawContract(RawResource.ORE, 10));
        crew.tryToFinishContracts();
        assertEquals(RawResource.WOOD, crew.getCurrentResource());
        crew.addToStock(RawResource.WOOD, 500);
        crew.addToStock(RawResource.ORE, 10);
        crew.tryToFinishContracts();
        assertEquals(RawResource.WOOD, crew.getCurrentResource());
    }

    @Test
    public void firstActionLand() {
        String res = crew.takeDecision();
        JSONObject json = new JSONObject(res);
        assertEquals("The first action is not land", "land", json.getString("action"));
    }

}
