package fr.unice.polytech.si3.qgl.ise.actions.simple;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScanActionTest {
    private Drone drone;
    private ScanAction scanAction;

    @Test
    public void correct() {
        drone = new Drone(new IslandMap(), DroneEnums.NSEW.NORTH);
        scanAction = new ScanAction(drone);
        String result = scanAction.apply();
        assertEquals("{\"action\":\"scan\"}", result);
    }
}
