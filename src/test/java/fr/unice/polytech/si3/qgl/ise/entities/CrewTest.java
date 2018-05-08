package fr.unice.polytech.si3.qgl.ise.entities;

import fr.unice.polytech.si3.qgl.ise.CraftedContract;
import fr.unice.polytech.si3.qgl.ise.RawContract;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.RawResource;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fr.unice.polytech.si3.qgl.ise.parsing.externalresources.ExtResSelector.bundle;
import static org.junit.Assert.*;

public class CrewTest {
    private Crew crew;

    @Before
    public void setup() {
        List<RawContract> rawContracts = new ArrayList<>();
        List<CraftedContract> craftedContracts = new ArrayList<>();
        rawContracts.add(new RawContract(bundle().getRawRes("WOOD"), 500));
        craftedContracts.add(new CraftedContract(bundle().getCraftedRes("PLANK"), 60));

        crew = new Crew(new IslandMap(), rawContracts, craftedContracts);
    }

    @Test
    public void finishRawContractTest() {
        crew.addToStock(bundle().getRawRes("WOOD"), 500);
        assertTrue(crew.getCompletedRawContracts().isEmpty());
        assertFalse(crew.getRawContracts().isEmpty());
        crew.tryToFinishContracts();
        assertFalse(crew.getCompletedRawContracts().isEmpty());
        assertTrue(crew.getRawContracts().isEmpty());
    }

    @Test
    public void finishCraftedContractTest() {
        crew.addToCraftedStock(bundle().getCraftedRes("PLANK"), 60);
        assertTrue(crew.getCompletedCraftedContracts().isEmpty());
        assertFalse(crew.getCraftedContracts().isEmpty());
        crew.tryToFinishContracts();
        assertFalse(crew.getCompletedCraftedContracts().isEmpty());
        assertTrue(crew.getCraftedContracts().isEmpty());
    }

    @Test
    public void canCraftResource() {
        crew.addToStock(bundle().getRawRes("WOOD"), 15);
        Map<RawResource, Double> res = crew.tryCrafting();
        assertEquals(15.0, res.get(bundle().getRawRes("WOOD")), 0);
    }

    @Test
    public void notEnoughToCraftResource() {
        crew.addToStock(bundle().getRawRes("WOOD"), 10);
        Map<RawResource, Double> res = crew.tryCrafting();
        assertNull(res);
    }

    @Test
    public void nothingToCraft() {
        crew.addToCraftedStock(bundle().getCraftedRes("PLANK"), 60);
        crew.tryToFinishContracts();
        Map<RawResource, Double> res = crew.tryCrafting();
        assertNull(res);
    }

    @Test
    public void newWantedResource() {
        List<RawResource> res = crew.getWantedResources();
        assertEquals(bundle().getRawRes("WOOD"), res.get(0));
        crew.getRawContracts().add(new RawContract(bundle().getRawRes("ORE"), 10));
        crew.tryToFinishContracts();
        res = crew.getWantedResources();
        assertEquals(bundle().getRawRes("WOOD"), res.get(0));
        assertEquals(bundle().getRawRes("ORE"), res.get(1));
    }

    @Test
    public void removeFromWantedResource() {
        crew.getRawContracts().add(new RawContract(bundle().getRawRes("ORE"), 10));
        crew.addToStock(bundle().getRawRes("WOOD"), 500);
        crew.addToCraftedStock(bundle().getCraftedRes("PLANK"), 60);
        crew.tryToFinishContracts();
        List<RawResource> res = crew.getWantedResources();
        assertEquals(bundle().getRawRes("ORE"), res.get(0));
        assertEquals(1, res.size());
    }

    @Test
    public void changeFocusRaw() {
        crew.getRawContracts().add(new RawContract(bundle().getRawRes("ORE"), 10));
        crew.tryToFinishContracts();
        assertEquals(bundle().getRawRes("WOOD"), crew.getCurrentResource());
        crew.addToStock(bundle().getRawRes("WOOD"), 500);
        crew.tryToFinishContracts();
        assertEquals(bundle().getRawRes("ORE"), crew.getCurrentResource());
    }

    @Test
    public void changeFocusCrafted() {
        crew.getRawContracts().add(new RawContract(bundle().getRawRes("ORE"), 10));
        crew.tryToFinishContracts();
        assertEquals(bundle().getRawRes("WOOD"), crew.getCurrentResource());
        crew.addToStock(bundle().getRawRes("WOOD"), 500);
        crew.addToStock(bundle().getRawRes("ORE"), 10);
        crew.tryToFinishContracts();
        assertEquals(bundle().getRawRes("WOOD"), crew.getCurrentResource());
    }

}
