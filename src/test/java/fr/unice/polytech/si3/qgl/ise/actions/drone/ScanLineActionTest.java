package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.parsing.Echo;
import fr.unice.polytech.si3.qgl.ise.parsing.Scan;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW.NORTH;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.BORDER;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ScanLineActionTest {
    private Drone drone;
    private ScanLineAction scanLineAction;
    private final JsonFactory jsonFact = new JsonFactory();

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

        scanLineAction = new ScanLineAction(drone);
    }

    @Test
    public void apply_SimpleCol() {
        for (NSEW ori : NSEW.values()) {
            drone.setOrientation(ori);

            String json = jsonFact.createJsonString("scan");
            String result = scanLineAction.apply();
            assertEquals(json, result);

            int i = new Random().nextInt(2) + 1; //Rand between 1 and 3

            for (int j = 0; j < i; j++) {
                drone.acknowledgeScan(new Scan(
                        "{\"cost\": 2, \"extras\": { \"biomes\": [\"BEACH\", \"ALPINE\"], \"creeks\": [], \"sites\": []}, \"status\": \"OK\"}"));

                json = jsonFact.createJsonString("fly");
                result = scanLineAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("scan");
                result = scanLineAction.apply();
                assertEquals(json, result);
            }

            drone.acknowledgeScan(new Scan(
                    "{\"cost\": 2, \"extras\": { \"biomes\": [\"OCEAN\"], \"creeks\": [], \"sites\": []}, \"status\": \"OK\"}"));

            json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getValue());
            result = scanLineAction.apply();
            assertEquals(json, result);

            drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 50, \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));

            json = jsonFact.createJsonString("fly");
            result = scanLineAction.apply();
            assertEquals(json, result);

            assertTrue(scanLineAction.isFinished());

            scanLineAction.reset();
        }
    }

    @Test
    public void apply_DoubleCol() {
        for (NSEW ori : NSEW.values()) {
            drone.setOrientation(ori);

            String json = jsonFact.createJsonString("scan");
            String result = scanLineAction.apply();
            assertEquals(json, result);

            drone.acknowledgeScan(new Scan(
                    "{\"cost\": 2, \"extras\": { \"biomes\": [\"BEACH\", \"ALPINE\"], \"creeks\": [], \"sites\": []}, \"status\": \"OK\"}"));

            json = jsonFact.createJsonString("fly");
            result = scanLineAction.apply();
            assertEquals(json, result);

            json = jsonFact.createJsonString("scan");
            result = scanLineAction.apply();
            assertEquals(json, result);

            drone.acknowledgeScan(new Scan(
                    "{\"cost\": 2, \"extras\": { \"biomes\": [\"OCEAN\"], \"creeks\": [], \"sites\": []}, \"status\": \"OK\"}"));

            json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getValue());
            result = scanLineAction.apply();
            assertEquals(json, result);

            int i = new Random().nextInt(2) + 1; //Rand between 1 and 3

            for (int j = i; j > 0; j--) {
                drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": " + j + ", \"found\": \"GROUND\" }, \"status\": \"OK\" }"));

                json = jsonFact.createJsonString("fly");
                result = scanLineAction.apply();
                assertEquals(json, result);
            }

            json = jsonFact.createJsonString("scan");
            result = scanLineAction.apply();
            assertEquals(json, result);

            drone.acknowledgeScan(new Scan(
                    "{\"cost\": 2, \"extras\": { \"biomes\": [\"OCEAN\"], \"creeks\": [], \"sites\": []}, \"status\": \"OK\"}"));

            json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getValue());
            result = scanLineAction.apply();
            assertEquals(json, result);

            drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 50, \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));

            json = jsonFact.createJsonString("fly");
            result = scanLineAction.apply();
            assertEquals(json, result);

            assertTrue(scanLineAction.isFinished());

            scanLineAction.reset();
        }
    }
}