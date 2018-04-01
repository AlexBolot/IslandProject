package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.Action;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;

public abstract class DroneAction extends Action {
    private final Drone drone;

    DroneAction(Drone drone) {
        this.drone = drone;
    }

    Drone getDrone() {
        return drone;
    }
}
