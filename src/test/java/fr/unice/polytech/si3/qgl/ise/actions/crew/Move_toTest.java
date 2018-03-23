package fr.unice.polytech.si3.qgl.ise.actions.crew;

import fr.unice.polytech.si3.qgl.ise.CraftedContract;
import fr.unice.polytech.si3.qgl.ise.RawContract;
import fr.unice.polytech.si3.qgl.ise.actions.CrewAction;
import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Move_toTest {

    Crew crew;
    CrewAction moveTo;
    IslandMap map;
    List<RawContract> rawContracts;
    List<CraftedContract> craftedContracts;

    @Before
    public void setup() {
        map = new IslandMap();
        map.addCreek(new Coordinates(0, 0), "idDepart");

        rawContracts = new ArrayList<>();
        craftedContracts = new ArrayList<>();

        rawContracts.add(new RawContract(RawResource.WOOD, 500));
        crew = new Crew(map, 10, rawContracts, craftedContracts);
        crew.setIdCreek("idDepart");
        crew.setCoords(new Coordinates(0, 0));
    }

    @Test
    public void testSetup() {
        assertEquals("The map should have one creek", 1, map.getCreeks().size());
        assertEquals("The setup is not ok", new Coordinates(0, 0), crew.getCoords());
    }

    @Test
    public void testDeplacement() {
        moveTo = new Move_to(crew, new Coordinates(10, 10));
        while (!moveTo.isFinished()) {
            moveTo.apply();
        }
        assertEquals("The crew should be in 10,10 ", new Coordinates(10, 10), crew.getCoords());
        moveTo = new Move_to(crew, new Coordinates(-10, -10));
        while (!moveTo.isFinished()) {
            moveTo.apply();
        }
        assertEquals("The crew should be in -10,-10 ", new Coordinates(-10, -10), crew.getCoords());
    }
}
