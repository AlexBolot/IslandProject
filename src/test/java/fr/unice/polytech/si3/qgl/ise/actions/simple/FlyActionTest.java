package fr.unice.polytech.si3.qgl.ise.actions.simple;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import org.junit.Before;
import org.junit.Test;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.BORDER;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.GROUND;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;
import static org.junit.Assert.assertEquals;

public class FlyActionTest {
    private Drone drone;
    private FlyAction flyAction;

    @Before
    public void init() {
        drone = new Drone(new IslandMap(), DroneEnums.NSEW.NORTH);
        flyAction = new FlyAction(drone);
        drone.getMargins().setGlobal(FRONT, BORDER, 15);
        drone.getMargins().setGlobal(BACK, BORDER, 10);
        drone.getMargins().setGlobal(LEFT, BORDER, 18);
        drone.getMargins().setGlobal(RIGHT, BORDER, 23);

        drone.getMargins().setLocal(FRONT, GROUND, 14);
        drone.getMargins().setLocal(BACK, GROUND, 11);
        drone.getMargins().setLocal(LEFT, BORDER, 18);
        drone.getMargins().setLocal(RIGHT, GROUND, 4);
    }

    @Test
    public void correctResult() {
        String result = flyAction.apply();
        assertEquals("{\"action\":\"fly\"}", result);
    }

    @Test
    public void security() {
        drone.getMargins().setGlobal(FRONT, BORDER, 1);
        String result = flyAction.apply();
        assertEquals("{\"action\":\"fly\"}", result);
        drone.getMargins().setLocal(FRONT, GROUND, 1);
        result = flyAction.apply();
        assertEquals("{\"action\":\"stop\"}", result);
    }
}
