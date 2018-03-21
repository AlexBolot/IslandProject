package fr.unice.polytech.si3.qgl.ise.actions.crew;

import fr.unice.polytech.si3.qgl.ise.actions.CrewAction;
import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;

public class Glimpse extends CrewAction {

    private DroneEnums.NSEW direction;
    private int range;

    public Glimpse(Crew crewToUpdate, DroneEnums.NSEW direction, int range) {
        super(crewToUpdate);
        this.direction = direction;
        this.range = range;
    }

    @Override
    public String apply() {
        return new JsonFactory().createJsonString("glimpse",
                "direction", direction.getValue(),
                "range", Integer.toString(range));
    }

    @Override
    public String acknowledgeResults(Crew crewToUpdate, String result) {
        return "";
    }
}
