package fr.unice.polytech.si3.qgl.ise.utilities;

import fr.unice.polytech.si3.qgl.ise.actions.simple.FlyAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.HeadingAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.parsing.Echo;
import org.junit.Before;
import org.junit.Test;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.BORDER;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.GROUND;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;
import static org.junit.Assert.assertEquals;

public class MarginTest {
    private Drone drone;

    @Before
    public void init() {
        drone = new Drone(new IslandMap(), DroneEnums.NSEW.NORTH);
        drone.setLastEcho(DroneEnums.ZQSD.FRONT);
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
    public void marginUpdateEchoGround() {
        drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 17, \"found\": \"GROUND\" }, \"status\": \"OK\" }"));
        int rangeGlobal = drone.getMargins().getGlobal(DroneEnums.ZQSD.FRONT)._2;
        int rangeLocal = drone.getMargins().getLocal(DroneEnums.ZQSD.FRONT)._2;
        assertEquals(15, rangeGlobal);
        assertEquals(17, rangeLocal);
    }

    @Test
    public void marginUpdateEchoBorder() {
        drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 16, \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }"));
        int rangeGlobal = drone.getMargins().getGlobal(DroneEnums.ZQSD.FRONT)._2;
        int rangeLocal = drone.getMargins().getLocal(DroneEnums.ZQSD.FRONT)._2;
        assertEquals(16, rangeGlobal);
        assertEquals(16, rangeLocal);
    }

    @Test
    public void marginUpdateFly() {
        FlyAction flyAction = new FlyAction(drone);
        flyAction.apply();
        int newGlobalFront = drone.getMargins().getGlobal(FRONT)._2;
        int newGlobalBack = drone.getMargins().getGlobal(BACK)._2;
        int newLocalFront = drone.getMargins().getLocal(FRONT)._2;
        int newLocalBack = drone.getMargins().getLocal(BACK)._2;

        assertEquals(14, newGlobalFront);
        assertEquals(11, newGlobalBack);
        assertEquals(13, newLocalFront);
        assertEquals(12, newLocalBack);
    }

    @Test
    public void marginUpdateHeading() {
        HeadingAction headingAction = new HeadingAction(drone);
        headingAction.apply(LEFT);
        int newGlobalFront = drone.getMargins().getGlobal(FRONT)._2;
        int newGlobalBack = drone.getMargins().getGlobal(BACK)._2;
        int newGlobalRight = drone.getMargins().getGlobal(RIGHT)._2;
        int newGlobalLeft = drone.getMargins().getGlobal(LEFT)._2;

        int newLocalFront = drone.getMargins().getLocal(FRONT)._2;
        int newLocalBack = drone.getMargins().getLocal(BACK)._2;
        int newLocalLeft = drone.getMargins().getLocal(LEFT)._2;
        int newLocalRight = drone.getMargins().getLocal(RIGHT)._2;

        assertEquals(14, newGlobalRight);
        assertEquals(11, newGlobalLeft);
        assertEquals(17, newGlobalFront);
        assertEquals(24, newGlobalBack);


        assertEquals(13, newLocalRight);
        assertEquals(12, newLocalLeft);
        assertEquals(17, newLocalFront);
        assertEquals(5, newLocalBack);
    }

}
