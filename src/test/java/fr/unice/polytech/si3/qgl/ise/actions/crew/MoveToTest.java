package fr.unice.polytech.si3.qgl.ise.actions.crew;

import fr.unice.polytech.si3.qgl.ise.actions.CrewAction;
import fr.unice.polytech.si3.qgl.ise.contracts.CraftedContract;
import fr.unice.polytech.si3.qgl.ise.contracts.RawContract;
import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static fr.unice.polytech.si3.qgl.ise.parsing.externalresources.ExtResSelector.bundle;
import static org.junit.Assert.assertEquals;

public class MoveToTest {

    private Crew crew;
    private IslandMap map;

    @Before
    public void setup() {
        map = new IslandMap();
        map.addCreek(new Coordinates(0, 0), "idDepart");

        List<RawContract> rawContracts = new ArrayList<>();
        List<CraftedContract> craftedContracts = new ArrayList<>();

        rawContracts.add(new RawContract(bundle().getRawRes("WOOD"), 500));
        crew = new Crew(map, rawContracts, craftedContracts);
        crew.setIdCreek("idDepart");
        crew.setCoordinates(new Coordinates(0, 0));
    }

    @Test
    public void testSetup() {
        assertEquals("The map should have one creek", 1, map.getCreeks().size());
        assertEquals("The setup is not ok", new Coordinates(0, 0), crew.getCoordinates());
    }

    @Test
    public void moveTest() {
        CrewAction moveTo = new MoveTo(crew, new Coordinates(10, 10));
        while (!moveTo.isFinished()) {
            moveTo.apply();
        }
        assertEquals("The crew should be in 10,10 ", new Coordinates(10, 10), crew.getCoordinates());
        moveTo = new MoveTo(crew, new Coordinates(-10, -10));
        while (!moveTo.isFinished()) {
            moveTo.apply();
        }
        assertEquals("The crew should be in -10,-10 ", new Coordinates(-10, -10), crew.getCoordinates());
    }
}
