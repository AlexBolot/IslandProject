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

    private int caseToReachFrom(Coordinates coord) {
        return Math.abs(Math.abs(coord.getX()) + Math.abs(coordinates.getX()))
                + Math.abs(Math.abs(coord.getY()) + Math.abs(coordinates.getY()));
    }

    private boolean setLastAction() {
        //The action is the last when whe are 1 tile close
        Coordinates crewCoords = getCrewToUpdate().getCoords();
//        if ((crewCoords.getX() == coordinates.getX() && Ydiff1) || (crewCoords.getY() == coordinates.getY() && Xdiff1))
        if (caseToReachFrom(crewCoords) == 1)
            finish();

        return this.isFinished();
    }

    @Override
    public String apply() {
        Crew crew = getCrewToUpdate();

        //We update the state of the action
        if (setLastAction() || !isFinished()) {
            //We have to go north
            if (crew.getCoords().getY() < coordinates.getY()) {
                crew.setCoords(new Coordinates(crew.getCoords().getX(), crew.getCoords().getY() + 1));
                return new JsonFactory().createJsonString(MOVE_TO, DIRECTION, NORTH.getValue());
            } else if (crew.getCoords().getY() > coordinates.getY()) { //We go South
                crew.setCoords(new Coordinates(crew.getCoords().getX(), crew.getCoords().getY() - 1));
                return new JsonFactory().createJsonString(MOVE_TO, DIRECTION, SOUTH.getValue());
            }
            //If the x is good, we check for the X
            //We have to go east
            if (crew.getCoords().getX() < coordinates.getX()) {
                crew.setCoords(new Coordinates(crew.getCoords().getX() + 1, crew.getCoords().getY()));
                return new JsonFactory().createJsonString(MOVE_TO, DIRECTION, EAST.getValue());
            } else if (crew.getCoords().getX() > coordinates.getX()) {
                crew.setCoords(new Coordinates(crew.getCoords().getX() - 1, crew.getCoords().getY()));
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
