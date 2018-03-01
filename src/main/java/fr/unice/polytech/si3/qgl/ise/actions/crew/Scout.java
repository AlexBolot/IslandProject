package fr.unice.polytech.si3.qgl.ise.actions.crew;

import fr.unice.polytech.si3.qgl.ise.actions.CrewAction;
import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;

public class Scout extends CrewAction {
    private DroneEnums.NSEW direction;

    public Scout(Crew crewToUpdate, DroneEnums.NSEW direction) {
        super(crewToUpdate);
        this.direction = direction;
    }

    @Override
    public String apply() {
        return new JsonFactory().createJsonString("scout", "direction", direction.getValue());
    }

    @Override
    public String aknowledgeResult(Crew crewToUpdate, String result) {
        return null;
    }
}
