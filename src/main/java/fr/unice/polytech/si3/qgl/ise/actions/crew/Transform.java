package fr.unice.polytech.si3.qgl.ise.actions.crew;

import fr.unice.polytech.si3.qgl.ise.actions.CrewAction;
import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;

import java.util.HashMap;

public class Transform extends CrewAction {
    private HashMap<RawResource, Integer> resourceWithQuantity;

    public Transform(Crew crewToUpdate, HashMap<RawResource, Integer> resourceWithQuantity) {
        super(crewToUpdate);
        this.resourceWithQuantity = resourceWithQuantity;
    }

    @Override
    public String apply() {
        return null;
    }

    @Override
    public String acknowledgeResults(Crew crewToUpdate, String result) {
        return "";
    }
}
