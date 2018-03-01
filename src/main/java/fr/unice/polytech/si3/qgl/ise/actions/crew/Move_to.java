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
        currentCoords = getCrewToUpdate().getCoords();
    }

    private void setLastAction() {
        if (Math.abs(coordinates.getX()) - Math.abs(currentCoords.getX()) + Math.abs(coordinates.getY()) - Math.abs(currentCoords.getY()) <= 1)
            finish();
    }

    @Override
    public String apply() {
        //We update the state of the action
        setLastAction();
        if (!isFinished()) {
            //We have to go north
            if (currentCoords.getY() < coordinates.getY())
                return new JsonFactory().createJsonString("move_to", "direction", DroneEnums.NSEW.NORTH.getValue());
            else if (currentCoords.getY() > coordinates.getY()) //We go South
                return new JsonFactory().createJsonString("move_to", "direction", DroneEnums.NSEW.SOUTH.getValue());
            //If the x is good, we check for the Y
            //We have to go east
            if (currentCoords.getX() < coordinates.getX())
                return new JsonFactory().createJsonString("move_to", "direction", DroneEnums.NSEW.EAST.getValue());
            else if (currentCoords.getX() > coordinates.getX())
                return new JsonFactory().createJsonString("move_to", "direction", DroneEnums.NSEW.EAST.getValue());
        }
        throw new IllegalStateException("You shouldn't try to move if you already reached your goal.");
    }

    @Override
    public String aknowledgeResult(Crew crewToUpdate, String result) {
        return "";
    }
}
