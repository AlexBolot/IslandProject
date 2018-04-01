package fr.unice.polytech.si3.qgl.ise.actions.simple;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Action.SCAN;

public class ScanAction extends SimpleAction {
    public ScanAction(Drone drone) {
        super(drone);
    }

    @Override
    public String apply() {
        getDrone().setLastAction(SCAN);
        return new JsonFactory().createJsonString("scan");
    }
}
