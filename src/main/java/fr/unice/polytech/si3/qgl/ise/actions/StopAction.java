package fr.unice.polytech.si3.qgl.ise.actions;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Action.STOP;

public class StopAction extends Action {
    private Drone drone;

    public StopAction(Drone drone) {
        this.drone = drone;
    }

    public StopAction() {

    }

    @Override
    public String apply() {
        if (drone != null) drone.setLastAction(STOP);
        return new JsonFactory().createJsonString("stop");
    }

    protected Drone getDrone() {
        return drone;
    }
}
