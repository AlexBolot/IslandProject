package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.parsing.Echo;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW.NORTH;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.BORDER;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChangeLineActionTest {
    private final JsonFactory jsonFact = new JsonFactory();
    private Drone drone;
    private ChangeLineAction changeLineAction;

    @Before
    public void init() {
        drone = new Drone(new IslandMap(), NORTH);

        drone.getMargins().setGlobal(FRONT, BORDER, 50);
        drone.getMargins().setGlobal(BACK, BORDER, 50);
        drone.getMargins().setGlobal(LEFT, BORDER, 50);
        drone.getMargins().setGlobal(RIGHT, BORDER, 50);

        drone.getMargins().setLocal(FRONT, BORDER, 50);
        drone.getMargins().setLocal(BACK, BORDER, 50);
        drone.getMargins().setLocal(LEFT, BORDER, 50);
        drone.getMargins().setLocal(RIGHT, BORDER, 50);

        changeLineAction = new ChangeLineAction(drone);
    }

    private String getOrientation(ZQSD direction) {
        if (direction == RIGHT) return drone.getOrientation().getToTheLeft().getValue();

        if (direction == LEFT) return drone.getOrientation().getToTheRight().getValue();

        throw new IllegalArgumentException("Gave wrong direction");
    }

    @Test
    public void apply_EasyWay() {
        for (NSEW ori : NSEW.values()) {
            for (ZQSD dir : new ZQSD[]{LEFT, RIGHT}) {
                drone.setOrientation(ori);
                drone.setLastTurn(dir);

                String json = jsonFact.createJsonString("echo", "direction", getOrientation(dir));
                String result = changeLineAction.apply();
                assertEquals(json, result);

                drone.acknowledgeEcho(new Echo(
                        "{ \"cost\": 1, \"extras\": { \"range\": 50, \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));
                json = jsonFact.createJsonString("heading", "direction", getOrientation(dir));
                result = changeLineAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("heading", "direction", getOrientation(dir));
                result = changeLineAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getValue());
                result = changeLineAction.apply();
                assertEquals(json, result);

                drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 0, \"found\": \"GROUND\" }, \"status\": \"OK\" }"));

                json = jsonFact.createJsonString("fly");
                result = changeLineAction.apply();
                assertEquals(json, result);

                result = changeLineAction.apply();
                assertTrue(result.isEmpty());

                assertTrue(changeLineAction.isFinished());

                changeLineAction.reset();
            }
        }
    }

    @Test
    public void apply_MultiReachIsland() {
        for (NSEW ori : NSEW.values()) {
            for (ZQSD dir : new ZQSD[]{LEFT, RIGHT}) {
                drone.setOrientation(ori);
                drone.setLastTurn(dir);

                String json = jsonFact.createJsonString("echo", "direction", getOrientation(dir));
                String result = changeLineAction.apply();
                assertEquals(json, result);

                drone.acknowledgeEcho(new Echo(
                        "{ \"cost\": 1, \"extras\": { \"range\": 50, \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));
                json = jsonFact.createJsonString("heading", "direction", getOrientation(dir));
                result = changeLineAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("heading", "direction", getOrientation(dir));
                result = changeLineAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getValue());
                result = changeLineAction.apply();
                assertEquals(json, result);

                int i = new Random().nextInt(2) + 1; //Rand between 1 and 3

                drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": " + i + ", \"found\": \"GROUND\" }, \"status\": \"OK\" }"));

                for (int j = 0; j <= i; j++) {
                    json = jsonFact.createJsonString("fly");
                    result = changeLineAction.apply();
                    assertEquals(json, result);
                }

                result = changeLineAction.apply();
                assertTrue(result.isEmpty());

                assertTrue(changeLineAction.isFinished());

                changeLineAction.reset();
            }
        }
    }

    @Test
    public void apply_MultiPassIsland() {
        for (NSEW ori : NSEW.values()) {
            for (ZQSD dir : new ZQSD[]{LEFT, RIGHT}) {
                drone.setOrientation(ori);
                drone.setLastTurn(dir);

                String json = jsonFact.createJsonString("echo", "direction", getOrientation(dir));
                String result = changeLineAction.apply();
                assertEquals(json, result);

                int i = new Random().nextInt(2) + 1; //Rand between 1 and 3

                for (int j = 0; j < i; j++) {
                    drone.acknowledgeEcho(new Echo(
                            "{ \"cost\": 1, \"extras\": { \"range\": 0, \"found\": \"GROUND\" }, \"status\": \"OK\" }"));

                    json = jsonFact.createJsonString("fly");
                    result = changeLineAction.apply();
                    assertEquals(json, result);

                    json = jsonFact.createJsonString("echo", "direction", getOrientation(dir));
                    result = changeLineAction.apply();
                    assertEquals(json, result);
                }

                drone.acknowledgeEcho(new Echo(
                        "{ \"cost\": 1, \"extras\": { \"range\": 50, \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));
                json = jsonFact.createJsonString("heading", "direction", getOrientation(dir));
                result = changeLineAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("heading", "direction", getOrientation(dir));
                result = changeLineAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getValue());
                result = changeLineAction.apply();
                assertEquals(json, result);

                drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 0, \"found\": \"GROUND\" }, \"status\": \"OK\" }"));

                json = jsonFact.createJsonString("fly");
                result = changeLineAction.apply();
                assertEquals(json, result);

                result = changeLineAction.apply();
                assertTrue(result.isEmpty());

                assertTrue(changeLineAction.isFinished());

                changeLineAction.reset();
            }
        }
    }
}