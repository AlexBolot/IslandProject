package fr.unice.polytech.si3.qgl.ise.actions.simple;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EchoActionTest {
    private Drone drone;
    private EchoAction echoAction;

    @Before
    public void init() {
        drone = new Drone(new IslandMap(), DroneEnums.NSEW.NORTH);
        echoAction = new EchoAction(drone);
    }

    @Test
    public void correctEcho() {
        String result = echoAction.apply(DroneEnums.ZQSD.FRONT);
        assertEquals("{\"action\":\"echo\",\"parameters\":{\"direction\":\"N\"}}", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidEcho() {
        String result = echoAction.apply(DroneEnums.ZQSD.BACK);
    }
}
