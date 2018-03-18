package fr.unice.polytech.si3.qgl.ise.actions.crew;

import fr.unice.polytech.si3.qgl.ise.actions.CrewAction;
import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;

public class Move_to extends CrewAction {

    private Coordinates coordinates;
    private Coordinates currentCoords;


    public Move_to(Crew crewToUpdate, Coordinates coordinates) {
        super(crewToUpdate);
        this.coordinates = coordinates;
    }

    private boolean setLastAction() {
        //The action is the last when whe are 1 case close
        if (Math.abs(coordinates.getX()) - Math.abs(getCrewToUpdate().getCoords().getX()) + Math.abs(coordinates.getY()) - Math.abs(getCrewToUpdate().getCoords().getY()) == 1) {
            finish();
            return true;
        }
        return false;
    }

    @Override
    public String apply() {
        //We update the state of the action
        if (setLastAction() || !isFinished()) {
            //We have to go north
            if (getCrewToUpdate().getCoords().getY() < coordinates.getY()) {
                getCrewToUpdate().setCoords(new Coordinates(getCrewToUpdate().getCoords().getX(), getCrewToUpdate().getCoords().getY() + 1));
                return new JsonFactory().createJsonString("move_to", "direction",
                        DroneEnums.NSEW.NORTH.getValue());
            } else if (getCrewToUpdate().getCoords().getY() > coordinates.getY()) { //We go South
                getCrewToUpdate().setCoords(new Coordinates(getCrewToUpdate().getCoords().getX(), getCrewToUpdate().getCoords().getY() - 1));
                return new JsonFactory().createJsonString("move_to", "direction",
                        DroneEnums.NSEW.SOUTH.getValue());
            }
            //If the x is good, we check for the Y
            //We have to go east
            if (getCrewToUpdate().getCoords().getX() < coordinates.getX()) {
                getCrewToUpdate().setCoords(new Coordinates(getCrewToUpdate().getCoords().getX() + 1, getCrewToUpdate().getCoords().getY()));
                return new JsonFactory().createJsonString("move_to", "direction",
                        DroneEnums.NSEW.EAST.getValue());
            } else if (getCrewToUpdate().getCoords().getX() > coordinates.getX()) {
                getCrewToUpdate().setCoords(new Coordinates(getCrewToUpdate().getCoords().getX() - 1, getCrewToUpdate().getCoords().getY()));
                return new JsonFactory().createJsonString("move_to", "direction",
                        DroneEnums.NSEW.EAST.getValue());
            }
        }
        throw new IllegalStateException("You shouldn't try to move if you've already reached your goal, coords : " + getCrewToUpdate().getCoords());
    }

    @Override
    public String acknowledgeResults(Crew crewToUpdate, String result) {
        return "";
    }
}
