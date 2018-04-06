package fr.unice.polytech.si3.qgl.ise.actions;

import fr.unice.polytech.si3.qgl.ise.actions.crew.Land;
import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;

public class EmergencyAction extends Action {

    private Drone drone;
    private Land land;

    public EmergencyAction(Drone drone, Crew crew) {
        this.drone = drone;
        this.land = new Land(crew, crew.getIdCreek());
    }

    @Override
    public String apply() {
        if (drone.isFlying() && !drone.getMap().getCreeks().isEmpty()) {
            drone.stopFlying();
            return land.apply();
        }

        return new StopAction().apply();
    }
}
