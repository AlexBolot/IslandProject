package fr.unice.polytech.si3.qgl.ise.actions.simple;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScanActionTest {

    @Test
    public void correct() {
        Drone drone = new Drone(new IslandMap(), DroneEnums.NSEW.NORTH);
        ScanAction scanAction = new ScanAction(drone);
        String result = scanAction.apply();
        assertEquals("{\"action\":\"scan\"}", result);
    }
}
