package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.Action;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;

public abstract class DroneAction extends Action {
    private Drone drone;

    DroneAction(Drone drone) {
        this.drone = drone;
    }

    protected Drone getDrone() {
        return drone;
    }
}
