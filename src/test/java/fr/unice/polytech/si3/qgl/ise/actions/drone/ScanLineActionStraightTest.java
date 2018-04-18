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

import static fr.unice.polytech.si3.qgl.ise.actions.drone.ScanLineActionStraight.Strategy.HALF;
import static fr.unice.polytech.si3.qgl.ise.actions.drone.TestingUtils.setMargins;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW.NORTH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ScanLineActionStraightTest {
    private final JsonFactory jsonFact = new JsonFactory();
    private Drone drone;
    private ScanLineActionStraight scanLineActionStraight;
    private int pace;

    @Before
    public void setUp() {
        drone = new Drone(new IslandMap(), NORTH);
        setMargins(drone, 50);
        scanLineActionStraight = new ScanLineActionStraight(drone, HALF.getPace());
    }

    private void setUpScanLineAction(int pace) {
        scanLineActionStraight = new ScanLineActionStraight(drone, pace);
    }

    @Test
    public void apply_SimpleCol() {
        Random random = new Random();

        for (int i = 0; i < 500; i++) {

            for (NSEW ori : NSEW.values()) {

                pace = 3; //random.nextInt(10) + 3;
                setUpScanLineAction(pace);

                drone.setOrientation(ori);

                String json = jsonFact.createJsonString("scan");
                String result = scanLineActionStraight.apply();
                assertEquals(json, result);

                int j = random.nextInt(2) + 1; //Rand between 1 and 3

                for (int k = 0; k < j; k++) {
                    drone.acknowledgeScan(new Scan("{\"cost\": 2, \"extras\": { \"biomes\": [\"BEACH\", \"ALPINE\"], \"creeks\": [], \"sites\": []}, \"status\": \"OK\"}"));

                    for (int l = 0; l < pace; l++) {
                        json = jsonFact.createJsonString("fly");
                        result = scanLineActionStraight.apply();
                        assertEquals(String.valueOf(l), json, result);
                    }

                    json = jsonFact.createJsonString("scan");
                    result = scanLineActionStraight.apply();
                    assertEquals(json, result);
                }

                drone.acknowledgeScan(new Scan("{\"cost\": 2, \"extras\": { \"biomes\": [\"OCEAN\"], \"creeks\": [], \"sites\": []}, \"status\": \"OK\"}"));

                json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getValue());
                result = scanLineActionStraight.apply();
                assertEquals(json, result);

                drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 50, \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));

                json = jsonFact.createJsonString("fly");
                result = scanLineActionStraight.apply();
                assertEquals(json, result);

                assertTrue(scanLineActionStraight.isFinished());

                setUp();
            }
        }
    }

    @Test
    public void apply_DoubleCol() {
        Random random = new Random();

        for (int i = 0; i < 500; i++) {

            for (NSEW ori : NSEW.values()) {

                pace = random.nextInt(10) + 3;
                setUpScanLineAction(pace);

                drone.setOrientation(ori);

                String json = jsonFact.createJsonString("scan");
                String result = scanLineActionStraight.apply();
                assertEquals(json, result);

                drone.acknowledgeScan(new Scan("{\"cost\": 2, \"extras\": { \"biomes\": [\"BEACH\", \"ALPINE\"], \"creeks\": [], \"sites\": []}, \"status\": \"OK\"}"));

                for (int j = 0; j < pace; j++) {
                    json = jsonFact.createJsonString("fly");
                    result = scanLineActionStraight.apply();
                    assertEquals(json, result);
                }

                json = jsonFact.createJsonString("scan");
                result = scanLineActionStraight.apply();
                assertEquals(json, result);

                drone.acknowledgeScan(new Scan("{\"cost\": 2, \"extras\": { \"biomes\": [\"OCEAN\"], \"creeks\": [], \"sites\": []}, \"status\": \"OK\"}"));

                json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getValue());
                result = scanLineActionStraight.apply();
                assertEquals(json, result);

                int j = random.nextInt(2) + 1; //Rand between 1 and 3

                for (int k = j; k > 0; k--) {
                    drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": " + k + ", \"found\": \"GROUND\" }, \"status\": \"OK\" }"));

                    json = jsonFact.createJsonString("fly");
                    result = scanLineActionStraight.apply();
                    assertEquals(json, result);
                }

                json = jsonFact.createJsonString("scan");
                result = scanLineActionStraight.apply();
                assertEquals(json, result);

                drone.acknowledgeScan(new Scan("{\"cost\": 2, \"extras\": { \"biomes\": [\"OCEAN\"], \"creeks\": [], \"sites\": []}, \"status\": \"OK\"}"));

                json = jsonFact.createJsonString("echo", "direction", drone.getOrientation().getValue());
                result = scanLineActionStraight.apply();
                assertEquals(json, result);

                drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 50, \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));

                json = jsonFact.createJsonString("fly");
                result = scanLineActionStraight.apply();
                assertEquals(json, result);

                assertTrue(scanLineActionStraight.isFinished());

                setUp();
            }
        }
    }
}