package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.simple.EchoAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.FlyAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.HeadingAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.utilities.Margin;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.SearchIslandAction.Step.*;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.GROUND;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;

public class SearchIslandAction extends DroneAction {
    private final FlyAction flyAction;
    private final EchoAction echoAction;
    private final HeadingAction headingAction;
    private Step currentStep;

    public SearchIslandAction(Drone drone) {
        super(drone);
        currentStep = CHOOSE_DIRECTION;
        flyAction = new FlyAction(drone);
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
                nextStep = ECHO_SIDE;
                break;

            case ECHO_SIDE:
                res = echoAction.apply(ZQSD.getOpposite(getDrone().getLastTurn()));
                nextStep = FLY_FRONT;
                break;

            case FLY_FRONT:
                res = decideToFly();

                if (!res.isEmpty()) nextStep = ECHO_SIDE;
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

        boolean facingGround = margins.getLocal(FRONT)._1 == GROUND;
        int marginRight = margins.getLocal(RIGHT)._2;
        int marginLeft = margins.getLocal(LEFT)._2;

        if (facingGround) dir = (marginRight < marginLeft) ? RIGHT : LEFT;
        else dir = (marginRight > marginLeft) ? RIGHT : LEFT;

        return headingAction.apply(dir);
    }

    private String decideToFly() {
        Margin margins = getDrone().getMargins();
        String res = "";

        if (margins.getLocal(getDrone().getLastEcho())._1 == GROUND || !getDrone().hasFoundIsland()) {
            int frontDist = margins.getLocal(FRONT)._2;

            if (frontDist > 1) res = flyAction.apply();
            else throw new IllegalStateException("Not enough margin left !");
        }

        return res;
    }

    public enum Step {
        CHOOSE_DIRECTION,
        ECHO_SIDE,
        FLY_FRONT
    }
}