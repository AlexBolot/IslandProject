package fr.unice.polytech.si3.qgl.ise.actions.simple;

import fr.unice.polytech.si3.qgl.ise.actions.Action;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;

public abstract class SimpleAction extends Action {
    private Drone drone;

    SimpleAction(Drone drone) {
        this.drone = drone;
    }

    protected Drone getDrone() {
        return drone;
    }
}
