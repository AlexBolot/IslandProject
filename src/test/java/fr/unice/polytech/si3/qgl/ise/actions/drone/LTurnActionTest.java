package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import org.junit.Before;
import org.junit.Test;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW.NORTH;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LTurnActionTest {
    private final JsonFactory jsonFact = new JsonFactory();
    private Drone drone;
    private LTurnAction lTurnAction;

    @Before
    public void setUp() {
        drone = new Drone(new IslandMap(), NORTH);
        TestingUtils.setMargins(drone, 50);
        lTurnAction = new LTurnAction(drone);
    }

    private String getOrientation(ZQSD direction) {
        if (direction == RIGHT) return drone.getOrientation().getToTheRight().getValue();

        if (direction == LEFT) return drone.getOrientation().getToTheLeft().getValue();

        throw new IllegalArgumentException("Gave wrong direction");
    }

    @Test
    public void apply() {
        for (NSEW ori : NSEW.values()) {
            for (ZQSD dir : new ZQSD[]{RIGHT, LEFT}) {
                drone.setOrientation(ori);
                drone.setLastEcho(dir);

                String json = jsonFact.createJsonString("heading", "direction", getOrientation(dir));
                String result = lTurnAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("heading", "direction", getOrientation(dir));
                result = lTurnAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("heading", "direction", getOrientation(getOpposite(dir)));
                result = lTurnAction.apply();
                assertEquals(json, result);

                assertTrue(lTurnAction.isFinished());

                setUp();
            }
        }
    }
}