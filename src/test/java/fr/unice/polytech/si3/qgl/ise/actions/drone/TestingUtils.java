package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.BORDER;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;

public class TestingUtils {
    public static void setMargins(Drone drone, int range) {
        drone.getMargins().setGlobal(FRONT, BORDER, range);
        drone.getMargins().setGlobal(BACK, BORDER, range);
        drone.getMargins().setGlobal(LEFT, BORDER, range);
        drone.getMargins().setGlobal(RIGHT, BORDER, range);

        drone.getMargins().setLocal(FRONT, BORDER, range);
        drone.getMargins().setLocal(BACK, BORDER, range);
        drone.getMargins().setLocal(LEFT, BORDER, range);
        drone.getMargins().setLocal(RIGHT, BORDER, range);
    }
}
