package fr.unice.polytech.si3.qgl.ise.actions.loop;

import fr.unice.polytech.si3.qgl.ise.actions.drone.ScanLineActionStraight;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.parsing.Echo;
import fr.unice.polytech.si3.qgl.ise.parsing.Scan;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.FRONT;
import static org.junit.Assert.assertEquals;

public class ScanIslandLoopActionTest {
    private ScanIslandLoopAction scanIslandLoopAction;
    private Drone drone;

    @Before
    public void init() {
        drone = new Drone(new IslandMap(), DroneEnums.NSEW.NORTH);
        scanIslandLoopAction = new ScanIslandLoopAction(drone, new ScanLineActionStraight(drone, 1));
        drone.setLastTurn(DroneEnums.ZQSD.RIGHT);
        drone.setLastEcho(DroneEnums.ZQSD.RIGHT);
        drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 10, \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));
        drone.setLastEcho(DroneEnums.ZQSD.LEFT);
        drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 10, \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));
        drone.setLastEcho(DroneEnums.ZQSD.BACK);
        drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 10, \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));
        drone.setLastEcho(DroneEnums.ZQSD.FRONT);
        drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 10, \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));
    }

    @Test
    public void startByScanLineLoopAction() {
        String res = scanIslandLoopAction.apply();
        JSONObject json = new JSONObject(res);
        assertEquals("scan", json.getString("action"));
        res = scanIslandLoopAction.apply();
        json = new JSONObject(res);
        assertEquals("echo", json.getString("action"));
        res = scanIslandLoopAction.apply();
        json = new JSONObject(res);
        assertEquals("fly", json.getString("action"));
    }

    @Test
    public void afterScanLineStopsIfHasCreeks() {
        simulateScanLineLoopAction();
        drone.acknowledgeScan(new Scan("{\"cost\": 2, \"extras\": { \"biomes\": [\"BEACH\"], \"creeks\": [\"id\"], \"sites\": []}, \"status\": \"OK\"}"));
        String res = scanIslandLoopAction.apply();
        assertEquals("", res);
    }

    @Test
    public void afterScanLineAboutTurnIfNoCreeks() {
        simulateScanLineLoopAction();
        String res = scanIslandLoopAction.apply();
        JSONObject json = new JSONObject(res);
        assertEquals("echo", json.getString("action"));
        drone.setLastEcho(DroneEnums.ZQSD.FRONT);
        drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 10, \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));
        drone.setLastEcho(DroneEnums.ZQSD.LEFT);
        drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 10, \"found\": \"GROUND\" }, \"status\": \"OK\" }"));
        res = scanIslandLoopAction.apply();
        json = new JSONObject(res);
        assertEquals("fly", json.getString("action"));
        res = scanIslandLoopAction.apply();
        json = new JSONObject(res);
        assertEquals("echo", json.getString("action"));
        drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 10, \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));
        res = scanIslandLoopAction.apply();
        json = new JSONObject(res);
        assertEquals("fly", json.getString("action"));
        res = scanIslandLoopAction.apply();
        json = new JSONObject(res);
        assertEquals("heading", json.getString("action"));
    }

    @Test
    public void afterAboutTurnReset() {
        simulateScanLineLoopAction();
        simulateAboutTurn();

        String res = scanIslandLoopAction.apply();
        JSONObject json = new JSONObject(res);
        assertEquals("echo", json.getString("action"));
    }

    private void simulateAboutTurn() {
        scanIslandLoopAction.apply();
        drone.setLastEcho(DroneEnums.ZQSD.FRONT);
        drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 10, \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));
        drone.setLastEcho(DroneEnums.ZQSD.LEFT);
        drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 10, \"found\": \"GROUND\" }, \"status\": \"OK\" }"));
        scanIslandLoopAction.apply();
        scanIslandLoopAction.apply();
        drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 10, \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));
        scanIslandLoopAction.apply();
        scanIslandLoopAction.apply();
        scanIslandLoopAction.apply();
        scanIslandLoopAction.apply();
        scanIslandLoopAction.apply();
        scanIslandLoopAction.apply();
        scanIslandLoopAction.apply();
        drone.getMargins().setLocal(DroneEnums.ZQSD.FRONT, DroneEnums.Obstacle.GROUND, 0);
    }

    private void simulateScanLineLoopAction() {
        //the following lines are here to simulate the scanLineLoopAction
        scanIslandLoopAction.apply();
        scanIslandLoopAction.apply();
        scanIslandLoopAction.apply();
        scanIslandLoopAction.apply();
        scanIslandLoopAction.apply();
        scanIslandLoopAction.apply();
        scanIslandLoopAction.apply();
        drone.setOrientation(DroneEnums.NSEW.SOUTH);
        drone.setLastEcho(DroneEnums.ZQSD.FRONT);
        drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 0, \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));
    }
}