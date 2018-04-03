package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.TestingUtils.setMargins;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW.NORTH;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.BORDER;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.GROUND;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.LEFT;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.RIGHT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GTurnActionTest {
    private final JsonFactory jsonFact = new JsonFactory();
    private Drone drone;
    private GTurnAction gTurnAction;

    @Before
    public void setUp() {
        drone = new Drone(new IslandMap(), NORTH);
        setMargins(drone, 50);
        gTurnAction = new GTurnAction(drone);
    }

    /**
     * Testing when there was still a strip of land undiscovered, on top of the last one that was discovered
     */
    @Test
    public void apply_LineOver() {
        for (NSEW ori : NSEW.values()) {
            for (ZQSD dir : new ZQSD[]{LEFT, RIGHT}) {
                drone.setOrientation(ori);
                drone.setLastTurn(dir);
                drone.setLastEcho(dir);

                String json = jsonFact.createJsonString("fly");
                String result = gTurnAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("heading", "direction", drone.getOrientation().getToThe(dir).getValue());
                result = gTurnAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getToThe(dir).getValue());
                result = gTurnAction.apply();
                assertEquals(json, result);

                int range = new Random().nextInt(5) + 3;

                drone.getMargins().setLocal(drone.getLastEcho(), GROUND, range);

                json = jsonFact.createJsonString("fly");
                result = gTurnAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("heading", "direction", drone.getOrientation().getToThe(dir).getValue());
                result = gTurnAction.apply()
                ;
                assertEquals(json, result);

                json = jsonFact.createJsonString("heading", "direction", drone.getOrientation().getToThe(dir).getValue());
                result = gTurnAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("heading", "direction", drone.getOrientation().getToThe(ZQSD.getOpposite(dir)).getValue());
                result = gTurnAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getValue());
                result = gTurnAction.apply();
                assertEquals(json, result);

                drone.getMargins().setLocal(drone.getLastEcho(), GROUND, range - 3);
                range -= 3;

                flyLoop(range);

                json = jsonFact.createJsonString("fly");
                result = gTurnAction.apply();
                assertEquals(json, result);

                assertTrue(gTurnAction.isFinished());
                setUp();
            }
        }
    }

    /**
     * Testing when there was still a strip of land undiscovered, on top of the last one that was discovered
     */
    @Test
    public void apply_LineUnder() {
        for (NSEW ori : NSEW.values()) {
            for (ZQSD dir : new ZQSD[]{LEFT, RIGHT}) {
                drone.setOrientation(ori);
                drone.setLastTurn(dir);
                drone.setLastEcho(dir);

                String json = jsonFact.createJsonString("fly");
                String result = gTurnAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("heading", "direction", drone.getOrientation().getToThe(dir).getValue());
                result = gTurnAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getToThe(dir).getValue());
                result = gTurnAction.apply();
                assertEquals(json, result);

                drone.getMargins().setLocal(drone.getLastEcho(), BORDER, drone.getMargins().getGlobal(drone.getLastEcho())._2);

                json = jsonFact.createJsonString("fly");
                result = gTurnAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("heading", "direction", drone.getOrientation().getToThe(dir).getValue());
                result = gTurnAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getValue());
                result = gTurnAction.apply();
                assertEquals(json, result);

                int range = new Random().nextInt(5);

                drone.getMargins().setLocal(drone.getLastEcho(), GROUND, range);

                flyLoop(range);

                json = jsonFact.createJsonString("fly");
                result = gTurnAction.apply();
                assertEquals(json, result);

                assertTrue(gTurnAction.isFinished());
                setUp();
            }
        }
    }

    private void flyLoop(int range) {
        String json;
        String result;
        while (drone.getMargins().getLocal(drone.getLastEcho())._2 > 0) {
            json = jsonFact.createJsonString("fly");
            result = gTurnAction.apply();
            assertEquals(json, result);

            json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getValue());
            result = gTurnAction.apply();
            assertEquals(json, result);

            drone.getMargins().setLocal(drone.getLastEcho(), GROUND, range--);
        }
    }
}