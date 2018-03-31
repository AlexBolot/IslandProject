package fr.unice.polytech.si3.qgl.ise.actions.loop;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScanLineLoopActionTest {

    @Test
    public void mainTest() {
        IslandMap map = new IslandMap();
        Drone drone = new Drone(map, DroneEnums.NSEW.EAST);
        ScanLineLoopAction action = new ScanLineLoopAction(drone);
        assertEquals("{\"action\":\"scan\"}", action.apply());
        assertEquals(false, action.isFinished());
        assertEquals("{\"action\":\"echo\",\"parameters\":{\"direction\":\"E\"}}", action.apply());
        assertEquals(false, action.isFinished());
        drone.setOrientation(DroneEnums.NSEW.EAST);
        drone.setLastTurn(DroneEnums.ZQSD.LEFT);
        assertEquals("{\"action\":\"echo\",\"parameters\":{\"direction\":\"S\"}}", action.apply());
        assertEquals(false, action.isFinished());
    }
}
