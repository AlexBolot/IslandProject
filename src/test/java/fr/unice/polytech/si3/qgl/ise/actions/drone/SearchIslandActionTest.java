package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.parsing.Echo;
import org.junit.Before;
import org.junit.Test;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.TestingUtils.setMargins;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW.NORTH;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SearchIslandActionTest {
    private final JsonFactory jsonFact = new JsonFactory();
    private Drone drone;
    private SearchIslandAction searchIslandAction;
    private DroneInitAction droneInitAction;

    @Before
    public void setUp() {
        drone = new Drone(new IslandMap(), NORTH);
        setMargins(drone, 50);
        searchIslandAction = new SearchIslandAction(drone);
        droneInitAction = new DroneInitAction(drone);
    }

    @Test
    public void apply_FacingGround() {
        for (NSEW ori : NSEW.values()) {
            for (ZQSD dir : new ZQSD[]{LEFT, RIGHT}) {
                drone.setOrientation(ori);

                //Front
                droneInitAction.apply();
                drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 15, \"found\": \"GROUND\" }, \"status\": \"OK\" }"));

                //Right
                droneInitAction.apply();
                drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": " + (dir == LEFT ? 20 : 10) + ", \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));

                //Left
                droneInitAction.apply();
                drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": " + (dir == LEFT ? 10 : 20) + ", \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));

                String json = jsonFact.createJsonString("heading", "direction", drone.getOrientation().getToThe(getOpposite(dir)).getValue());
                String result = searchIslandAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getToThe(FRONT).getValue());
                result = searchIslandAction.apply();
                assertEquals(json, result);

                drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 15, \"found\": \"GROUND\" }, \"status\": \"OK\" }"));

                assertTrue(searchIslandAction.apply().isEmpty());
                assertTrue(searchIslandAction.isFinished());

                setUp();
            }
        }
    }

    @Test
    public void apply_FacingBorder() {
        for (NSEW ori : NSEW.values()) {
            for (ZQSD dir : new ZQSD[]{LEFT, RIGHT}) {
                drone.setOrientation(ori);

                //Front
                droneInitAction.apply();
                drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 15, \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));

                //Right
                droneInitAction.apply();
                drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": " + (dir == LEFT ? 10 : 20) + ", \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));

                //Left
                droneInitAction.apply();
                drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": " + (dir == LEFT ? 20 : 10) + ", \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));

                String json = jsonFact.createJsonString("heading", "direction", drone.getOrientation().getToThe(dir).getValue());
                String result = searchIslandAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getToThe(FRONT).getValue());
                result = searchIslandAction.apply();
                assertEquals(json, result);

                drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 15, \"found\": \"GROUND\" }, \"status\": \"OK\" }"));

                assertTrue(searchIslandAction.apply().isEmpty());
                assertTrue(searchIslandAction.isFinished());

                setUp();
            }
        }
    }
}