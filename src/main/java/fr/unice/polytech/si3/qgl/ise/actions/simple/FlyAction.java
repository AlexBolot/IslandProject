package fr.unice.polytech.si3.qgl.ise.actions.simple;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import fr.unice.polytech.si3.qgl.ise.utilities.Margin;
import scala.Tuple2;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Action.Fly;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.BACK;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.FRONT;

public class FlyAction extends SimpleAction {
    public FlyAction(Drone drone) {
        super(drone);
    }

    @Override
    public String apply() {
        getDrone().setLastAction(Fly);
        return fly();
    }

    private String fly() {
        int unit = Drone.getMovementUnit();
        int oldX = getDrone().getCoords().getX();
        int oldY = getDrone().getCoords().getY();

        Coordinates newCoords;

        switch (getDrone().getOrientation()) {
            case EAST:
                newCoords = new Coordinates(oldX + unit, oldY);
                break;

            case WEST:
                newCoords = new Coordinates(oldX - unit, oldY);
                break;

            case NORTH:
                newCoords = new Coordinates(oldX, oldY + unit);
                break;

            case SOUTH:
                newCoords = new Coordinates(oldX, oldY - unit);
                break;

            default:
                throw new IllegalStateException("Something went wrong while turning right");
        }

        getDrone().setCoords(newCoords);
        updateMargins();

        return new JsonFactory().createJsonString("fly");
    }

    private void updateMargins() {
        Margin margins = getDrone().getMargins();
        //update local margins
        Tuple2<Obstacle, Integer> oldFront = margins.getLocal(FRONT);
        Tuple2<Obstacle, Integer> oldBack = margins.getLocal(BACK);

        margins.setLocal(FRONT, oldFront._1, oldFront._2 - 1);
        margins.setLocal(BACK, oldBack._1, oldBack._2 + 1);

        //update global margins
        Tuple2<Obstacle, Integer> oldGlobalFront = margins.getGlobal(FRONT);
        Tuple2<Obstacle, Integer> oldGlobalBack = margins.getGlobal(BACK);

        margins.setGlobal(FRONT, oldGlobalFront._1, oldGlobalFront._2 - 1);
        margins.setGlobal(BACK, oldGlobalBack._1, oldGlobalBack._2 + 1);
    }
}
