package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import org.junit.Before;
import org.junit.Test;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.TestingUtils.setMargins;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW.NORTH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DroneInitActionTest {
    private final JsonFactory jsonFact = new JsonFactory();
    private Drone drone;
    private DroneInitAction droneInitAction;

    @Before
    public void setUp() {
        drone = new Drone(new IslandMap(), NORTH);
        setMargins(drone, 50);
        droneInitAction = new DroneInitAction(drone);
    }

    @Test
    public void apply() {
        for (NSEW ori : NSEW.values()) {
            drone.setOrientation(ori);

            String json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getValue());
            String result = droneInitAction.apply();
            assertEquals(json, result);

            json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getToTheRight().getValue());
            result = droneInitAction.apply();
            assertEquals(json, result);

            json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getToTheLeft().getValue());
            result = droneInitAction.apply();
            assertEquals(json, result);

            assertTrue(droneInitAction.apply().isEmpty());
            assertTrue(droneInitAction.isFinished());

            setUp();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void apply_OZ() {
        setMargins(drone, 0);

        String json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getValue());
        String result = droneInitAction.apply();
        assertEquals(json, result);

        json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getToTheRight().getValue());
        result = droneInitAction.apply();
        assertEquals(json, result);

        json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getToTheLeft().getValue());
        result = droneInitAction.apply();
        assertEquals(json, result);

        assertTrue(droneInitAction.apply().isEmpty());

        setUp();
    }
}