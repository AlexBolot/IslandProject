package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import org.junit.Before;
import org.junit.Test;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW.NORTH;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.BORDER;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;
import static org.junit.Assert.assertEquals;

public class DroneInitActionTest {
    private final JsonFactory jsonFact = new JsonFactory();
    private Drone drone;
    private DroneInitAction droneInitAction;

    @Before
    public void setUp() {
        drone = new Drone(new IslandMap(), NORTH);

        drone.getMargins().setGlobal(FRONT, BORDER, 50);
        drone.getMargins().setGlobal(BACK, BORDER, 50);
        drone.getMargins().setGlobal(LEFT, BORDER, 50);
        drone.getMargins().setGlobal(RIGHT, BORDER, 50);

        drone.getMargins().setLocal(FRONT, BORDER, 50);
        drone.getMargins().setLocal(BACK, BORDER, 50);
        drone.getMargins().setLocal(LEFT, BORDER, 50);
        drone.getMargins().setLocal(RIGHT, BORDER, 50);

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


            droneInitAction.reset();
        }
    }
}