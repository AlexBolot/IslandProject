package fr.unice.polytech.si3.qgl.ise.actions.crew;

import fr.unice.polytech.si3.qgl.ise.actions.CrewAction;
import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW.*;

public class MoveTo extends CrewAction {

    private static final String MOVE_TO = "move_to";
    private static final String DIRECTION = "direction";
    private final Coordinates coordinates;

    public MoveTo(Crew crewToUpdate, Coordinates coordinates) {
        super(crewToUpdate);
        this.coordinates = coordinates;
    }

    private int caseToReachFrom(Coordinates coordinates) {
        return Math.abs(Math.abs(coordinates.getX()) + Math.abs(this.coordinates.getX()))
                + Math.abs(Math.abs(coordinates.getY()) + Math.abs(this.coordinates.getY()));
    }

    private boolean setLastAction() {
        //The action is the last when whe are 1 tile close
        Coordinates crewCoordinates = getCrewToUpdate().getCoordinates();
//        if ((crewCoordinates.getX() == coordinates.getX() && Ydiff1) || (crewCoordinates.getY() == coordinates.getY() && Xdiff1))
        if (caseToReachFrom(crewCoordinates) == 1)
            finish();

        return this.isFinished();
    }

    @Override
    public String apply() {
        Crew crew = getCrewToUpdate();

        //We update the state of the action
        if (setLastAction() || !isFinished()) {
            //We have to go north
            if (crew.getCoordinates().getY() < coordinates.getY()) {
                crew.setCoordinates(new Coordinates(crew.getCoordinates().getX(), crew.getCoordinates().getY() + 1));
                return new JsonFactory().createJsonString(MOVE_TO, DIRECTION, NORTH.getValue());
            } else if (crew.getCoordinates().getY() > coordinates.getY()) { //We go South
                crew.setCoordinates(new Coordinates(crew.getCoordinates().getX(), crew.getCoordinates().getY() - 1));
                return new JsonFactory().createJsonString(MOVE_TO, DIRECTION, SOUTH.getValue());
            }
            //If the x is good, we check for the X
            //We have to go east
            if (crew.getCoordinates().getX() < coordinates.getX()) {
                crew.setCoordinates(new Coordinates(crew.getCoordinates().getX() + 1, crew.getCoordinates().getY()));
                return new JsonFactory().createJsonString(MOVE_TO, DIRECTION, EAST.getValue());
            } else if (crew.getCoordinates().getX() > coordinates.getX()) {
                crew.setCoordinates(new Coordinates(crew.getCoordinates().getX() - 1, crew.getCoordinates().getY()));
                return new JsonFactory().createJsonString(MOVE_TO, DIRECTION, WEST.getValue());
            }
        }

        this.finish();
        return "";
    }

    @Override
    public String acknowledgeResults(String result) {
        return "";
    }
}
