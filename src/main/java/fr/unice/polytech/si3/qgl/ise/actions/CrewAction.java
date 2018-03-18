package fr.unice.polytech.si3.qgl.ise.actions;

import fr.unice.polytech.si3.qgl.ise.entities.Crew;

public abstract class CrewAction extends Action {

    private Crew crewToUpdate;

    protected CrewAction(Crew crewToUpdate) {
        this.crewToUpdate = crewToUpdate;
    }

    public Crew getCrewToUpdate() {
        return crewToUpdate;
    }

    public abstract String apply();

    public abstract String acknowledgeResults(Crew crewToUpdate, String result);

}
