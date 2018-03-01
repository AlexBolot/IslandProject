package fr.unice.polytech.si3.qgl.ise.actions.crew;

import fr.unice.polytech.si3.qgl.ise.actions.CrewAction;
import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;

public class Move_to extends CrewAction {

    private Coordinates coordinates;

    public Move_to(Crew crewToUpdate, Coordinates coordinates) {
        super(crewToUpdate);
        this.coordinates = coordinates;
    }

    @Override
    public String apply() {
        //TODO
        return "";
    }

    @Override
    public String aknowledgeResult(Crew crewToUpdate, String result) {
        return "";
    }
}
