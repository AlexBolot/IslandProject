package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.simple.EchoAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.HeadingAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.utilities.Margin;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.SearchIslandAction.Step.*;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.BORDER;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;

public class SearchIslandAction extends DroneAction {
    private final EchoAction echoAction;
    private final HeadingAction headingAction;
    private Step currentStep;

    public SearchIslandAction(Drone drone) {
        super(drone);
        currentStep = CHOOSE_DIRECTION;
        echoAction = new EchoAction(drone);
        headingAction = new HeadingAction(drone);
    }

    @Override
    public void reset() {
        super.reset();
        currentStep = CHOOSE_DIRECTION;
    }

    @Override
    public String apply() {
        return apply(currentStep);
    }

    private String apply(Step step) {
        String res;
        Step nextStep = null;

        switch (step) {
            case CHOOSE_DIRECTION:
                res = chooseDirection();
                nextStep = ECHO_FRONT;
                break;

            case ECHO_FRONT:
                res = echoAction.apply(FRONT);
                nextStep = HEADING;
                break;

            case HEADING:
                res = decideToTurn();

                if (!res.isEmpty()) nextStep = ECHO_FRONT;
                else finish();
                break;

            default:
                throw new IllegalStateException("Unknown step : " + step);
        }

        currentStep = nextStep;

        return res;
    }

    private String chooseDirection() {
        Margin margins = getDrone().getMargins();
        ZQSD dir;

        int marginRight = margins.getLocal(RIGHT)._2;
        int marginLeft = margins.getLocal(LEFT)._2;

        dir = (marginRight > marginLeft) ? RIGHT : LEFT;

        return headingAction.apply(dir);
    }

    private String decideToTurn() {
        Margin margins = getDrone().getMargins();
        String res = "";

        if (margins.getLocal(getDrone().getLastEcho())._1 == BORDER || !getDrone().hasFoundIsland()) {
            int frontDist = margins.getLocal(FRONT)._2;

            if (frontDist > 1) res = headingAction.apply(ZQSD.getOpposite(getDrone().getLastTurn()));
            else throw new IllegalStateException("Not enough margin left !");
        }

        return res;
    }

    public enum Step {
        CHOOSE_DIRECTION,
        ECHO_FRONT,
        HEADING
    }
}