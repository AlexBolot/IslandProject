package fr.unice.polytech.si3.qgl.ise.actions.crew;

import fr.unice.polytech.si3.qgl.ise.actions.CrewAction;
import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MoveTo {

    Crew crew;
    CrewAction moveTo;
    IslandMap map;

    @Before
    public void setup() {
        map = new IslandMap();
        map.addCreek(new Coordinates(0, 0), "idDepart");

        crew = new Crew(map, null, null);
        crew.setCrewSize(10);
        crew.setIdCreek("idDepart");
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
    }
}
