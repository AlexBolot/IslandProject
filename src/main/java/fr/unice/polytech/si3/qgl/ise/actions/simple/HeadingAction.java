package fr.unice.polytech.si3.qgl.ise.actions.simple;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import scala.Tuple2;

import java.util.HashMap;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Action.Heading;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;

public class HeadingAction extends SimpleAction
{
    public HeadingAction (Drone drone)
    {
        super(drone);
    }

    @Override
    public String apply ()
    {
        return apply(getDrone().getLastTurn());
    }

    public String apply (ZQSD direction)
    {
        String res;

        switch (direction)
        {
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

    private String turnRight ()
    {
        int unit = Drone.getMovementUnit();
        int oldX = getDrone().getCoords().getX();
        int oldY = getDrone().getCoords().getY();

        Coordinates newCoords;

        switch (getDrone().getOrientation())
        {
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

    private String turnLeft ()
    {
        int unit = Drone.getMovementUnit();
        int oldX = getDrone().getCoords().getX();
        int oldY = getDrone().getCoords().getY();

        Coordinates newCoords;

        switch (getDrone().getOrientation())
        {
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

    private void updateMargins (ZQSD direction)
    {
        HashMap<ZQSD, Tuple2<Obstacle, Integer>> margins = getDrone().getMargins();

        Tuple2<Obstacle, Integer> oldFront = margins.get(FRONT);
        Tuple2<Obstacle, Integer> oldRight = margins.get(RIGHT);
        Tuple2<Obstacle, Integer> oldBack = margins.get(BACK);
        Tuple2<Obstacle, Integer> oldLeft = margins.get(LEFT);

        switch (direction)
        {
            case LEFT:

                margins.put(FRONT, new Tuple2<>(oldLeft._1, oldLeft._2 - 1));
                margins.put(RIGHT, new Tuple2<>(oldFront._1, oldFront._2 - 1));
                margins.put(BACK, new Tuple2<>(oldRight._1, oldRight._2 + 1));
                margins.put(LEFT, new Tuple2<>(oldBack._1, oldBack._2 + 1));

                break;

            case RIGHT:

                margins.put(FRONT, new Tuple2<>(oldRight._1, oldRight._2 - 1));
                margins.put(RIGHT, new Tuple2<>(oldBack._1, oldBack._2 + 1));
                margins.put(BACK, new Tuple2<>(oldLeft._1, oldLeft._2 + 1));
                margins.put(LEFT, new Tuple2<>(oldFront._1, oldFront._2 - 1));

                break;

            default:
                throw new IllegalArgumentException("Wrong direction to head to !");
        }
    }
}
