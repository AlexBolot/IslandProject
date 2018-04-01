package fr.unice.polytech.si3.qgl.ise.actions.simple;

import fr.unice.polytech.si3.qgl.ise.actions.Action;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;

abstract class SimpleAction extends Action {
    private final Drone drone;

    SimpleAction(Drone drone) {
        this.drone = drone;
    }

    Drone getDrone() {
        return drone;
    }
}
