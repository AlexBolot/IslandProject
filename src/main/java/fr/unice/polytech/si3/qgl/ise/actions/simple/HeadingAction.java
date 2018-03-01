package fr.unice.polytech.si3.qgl.ise.actions.simple;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import fr.unice.polytech.si3.qgl.ise.utilities.Margin;
import scala.Tuple2;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Action.Heading;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Action.Stop;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;

public class HeadingAction extends SimpleAction {
    public HeadingAction(Drone drone) {
        super(drone);
    }

    @Override
    public String apply() {
        return apply(getDrone().getLastTurn());
    }

    public String apply(ZQSD direction) {
        String res;

        switch (direction) {
            case LEFT:
                res = turnLeft();
                break;

            case RIGHT:
                res = turnRight();
                break;

            default:
                throw new IllegalArgumentException("Wrong direction to head to !");
        }

        updateMargins(direction);
        getDrone().setLastTurn(direction);
        getDrone().setLastAction(Heading);

        return res;
    }

    private String turnRight() {
        //security if the drone wants to head out of the map
        if (getDrone().getMargins().getGlobal(FRONT)._2 < 1 || getDrone().getMargins().getGlobal(RIGHT)._2 < 1) {
            getDrone().setLastAction(Stop);
            return new JsonFactory().createJsonString("stop");
        }

        int unit = Drone.getMovementUnit();
        int oldX = getDrone().getCoords().getX();
        int oldY = getDrone().getCoords().getY();

        Coordinates newCoords;

        switch (getDrone().getOrientation()) {
            case EAST:
                newCoords = new Coordinates(oldX + unit, oldY - unit);
                break;

            case WEST:
                newCoords = new Coordinates(oldX - unit, oldY + unit);
                break;

            case NORTH:
                newCoords = new Coordinates(oldX + unit, oldY + unit);
                break;

            case SOUTH:
                newCoords = new Coordinates(oldX - unit, oldY - unit);
                break;

            default:
                throw new IllegalStateException("Something went wrong while turning right");
        }

        NSEW newOri = getDrone().getOrientation().getToTheRight();

        getDrone().setCoords(newCoords);
        getDrone().setOrientation(newOri);

        return new JsonFactory().createJsonString("heading", "direction", newOri.getValue());
    }

    private String turnLeft() {
        //security if the drone wants to head out of the map
        if (getDrone().getMargins().getGlobal(FRONT)._2 < 1 || getDrone().getMargins().getGlobal(LEFT)._2 < 1) {
            getDrone().setLastAction(Stop);
            return new JsonFactory().createJsonString("stop");
        }

        int unit = Drone.getMovementUnit();
        int oldX = getDrone().getCoords().getX();
        int oldY = getDrone().getCoords().getY();

        Coordinates newCoords;

        switch (getDrone().getOrientation()) {
            case EAST:
                newCoords = new Coordinates(oldX + unit, oldY + unit);
                break;

            case WEST:
                newCoords = new Coordinates(oldX - unit, oldY - unit);
                break;

            case NORTH:
                newCoords = new Coordinates(oldX - unit, oldY + unit);
                break;

            case SOUTH:
                newCoords = new Coordinates(oldX + unit, oldY - unit);
                break;

            default:
                throw new IllegalStateException("Something went wrong while turning right");
        }

        NSEW newOri = getDrone().getOrientation().getToTheLeft();

        getDrone().setCoords(newCoords);
        getDrone().setOrientation(newOri);

        return new JsonFactory().createJsonString("heading", "direction", newOri.getValue());
    }

    private void updateMargins(ZQSD direction) {
        Margin margins = getDrone().getMargins();

        Tuple2<Obstacle, Integer> oldFront = margins.getLocal(FRONT);
        Tuple2<Obstacle, Integer> oldRight = margins.getLocal(RIGHT);
        Tuple2<Obstacle, Integer> oldBack = margins.getLocal(BACK);
        Tuple2<Obstacle, Integer> oldLeft = margins.getLocal(LEFT);

        Tuple2<Obstacle, Integer> oldGlobalFront = margins.getGlobal(FRONT);
        Tuple2<Obstacle, Integer> oldGlobalRight = margins.getGlobal(RIGHT);
        Tuple2<Obstacle, Integer> oldGlobalBack = margins.getGlobal(BACK);
        Tuple2<Obstacle, Integer> oldGlobalLeft = margins.getGlobal(LEFT);

        switch (direction) {
            case LEFT:

                margins.setLocal(FRONT, oldLeft._1, oldLeft._2 - 1);
                margins.setLocal(RIGHT, oldFront._1, oldFront._2 - 1);
                margins.setLocal(BACK, oldRight._1, oldRight._2 + 1);
                margins.setLocal(LEFT, oldBack._1, oldBack._2 + 1);

                margins.setGlobal(FRONT, oldGlobalLeft._1, oldGlobalLeft._2 - 1);
                margins.setGlobal(RIGHT, oldGlobalFront._1, oldGlobalFront._2 - 1);
                margins.setGlobal(BACK, oldGlobalRight._1, oldGlobalRight._2 + 1);
                margins.setGlobal(LEFT, oldGlobalBack._1, oldGlobalBack._2 + 1);

                break;

            case RIGHT:

                margins.setLocal(FRONT, oldRight._1, oldRight._2 - 1);
                margins.setLocal(RIGHT, oldBack._1, oldBack._2 + 1);
                margins.setLocal(BACK, oldLeft._1, oldLeft._2 + 1);
                margins.setLocal(LEFT, oldFront._1, oldFront._2 - 1);

                margins.setGlobal(FRONT, oldGlobalRight._1, oldGlobalRight._2 - 1);
                margins.setGlobal(RIGHT, oldGlobalBack._1, oldGlobalBack._2 + 1);
                margins.setGlobal(BACK, oldGlobalLeft._1, oldGlobalLeft._2 + 1);
                margins.setGlobal(LEFT, oldGlobalFront._1, oldGlobalFront._2 - 1);

                break;

            default:
                throw new IllegalArgumentException("Wrong direction to head to !");
        }
    }
}
