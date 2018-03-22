package fr.unice.polytech.si3.qgl.ise.actions.crew;

import fr.unice.polytech.si3.qgl.ise.actions.CrewAction;
import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW.*;

public class Move_to extends CrewAction {

    private Coordinates coordinates;
    private Coordinates currentCoords;


    public Move_to(Crew crewToUpdate, Coordinates coordinates) {
        super(crewToUpdate);
        this.coordinates = coordinates;
    }

    private boolean setLastAction() {
        //The action is the last when whe are 1 tile close
        if (Math.abs(coordinates.getX()) - Math.abs(getCrewToUpdate().getCoords().getX()) + Math.abs(coordinates.getY()) - Math.abs(getCrewToUpdate().getCoords().getY()) == 1)
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
                return new JsonFactory().createJsonString("move_to", "direction", NORTH.getValue());
            } else if (crew.getCoords().getY() > coordinates.getY()) { //We go South
                crew.setCoords(new Coordinates(crew.getCoords().getX(), crew.getCoords().getY() - 1));
                return new JsonFactory().createJsonString("move_to", "direction", SOUTH.getValue());
            }
            //If the x is good, we check for the Y
            //We have to go east
            if (crew.getCoords().getX() < coordinates.getX()) {
                crew.setCoords(new Coordinates(crew.getCoords().getX() + 1, crew.getCoords().getY()));
                return new JsonFactory().createJsonString("move_to", "direction", EAST.getValue());
            } else if (crew.getCoords().getX() > coordinates.getX()) {
                crew.setCoords(new Coordinates(crew.getCoords().getX() - 1, crew.getCoords().getY()));
                return new JsonFactory().createJsonString("move_to", "direction", EAST.getValue());
            }
        }

        this.finish();
        return "";
    }

    @Override
    public String acknowledgeResults(Crew crewToUpdate, String result) {
        return "";
    }
}
