package fr.unice.polytech.si3.qgl.ise.actions;

import fr.unice.polytech.si3.qgl.ise.entities.Crew;

public abstract class CrewAction extends Action {

    private final Crew crewToUpdate;

    protected CrewAction(Crew crewToUpdate) {
        this.crewToUpdate = crewToUpdate;
    }

    @Override
    public abstract String apply();

    /**
     * Main function, to perform everything needed with the action result
     *
     * @return JSON formatted String to do the action
     */
    public abstract String acknowledgeResults(String result);

    /**
     * @return the crew that performs the action and need to be updated
     */
    protected Crew getCrewToUpdate() {
        return crewToUpdate;
    }

}
