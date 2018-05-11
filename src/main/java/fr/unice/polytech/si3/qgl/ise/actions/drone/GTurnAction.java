package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.simple.EchoAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.FlyAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.HeadingAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD;
import fr.unice.polytech.si3.qgl.ise.utilities.Margin;
import scala.Tuple2;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.GTurnAction.Step.*;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.GROUND;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.FRONT;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.getOpposite;

public class GTurnAction extends DroneAction {
    private final FlyAction flyAction;
    private final EchoAction echoAction;
    private final HeadingAction headingAction;
    private Step currentStep;
    private boolean turnNotBis;

    public GTurnAction(Drone drone) {
        super(drone);
        currentStep = FLY_FRONT_1;
        flyAction = new FlyAction(drone);
        echoAction = new EchoAction(drone);
        headingAction = new HeadingAction(drone);
        turnNotBis = false;
    }

    @Override
    public void reset() {
        super.reset();
        currentStep = FLY_FRONT_1;
    }

    @Override
    public String apply() {
        return apply(currentStep);
    }

    private String apply(Step step) {
        String res;
        Step nextStep = null;

        switch (step) {
            case FLY_FRONT_1:
                res = decideToFly();
                nextStep = TURN_1;
                break;

            case TURN_1:
                res = headingAction.apply(getDrone().getLastTurn());
                nextStep = ECHO_SIDE;
                break;

            case ECHO_SIDE:
                res = echoAction.apply(getDrone().getLastTurn());
                nextStep = FLY_FRONT_2;
                break;

            case FLY_FRONT_2:
                Margin margins = getDrone().getMargins();
                ZQSD lastTurn = getDrone().getLastTurn();
                Tuple2<Obstacle, Integer> lastMargin = margins.getLocal(lastTurn);
                if (lastMargin._1 == GROUND) {
                    nextStep = TURN_2;
                } else {
                    nextStep = TURN_2_BIS;
                }
                res = decideToFly();
                break;

            case TURN_2:
                turnNotBis = true;
                res = headingAction.apply(getDrone().getLastTurn());
                nextStep = TURN_3;
                break;

            case TURN_2_BIS:
                res = headingAction.apply(getDrone().getLastTurn());
                nextStep = ECHO_FRONT;
                break;

            case TURN_3:
                res = headingAction.apply(getDrone().getLastTurn());
                nextStep = TURN_4;
                break;

            case TURN_4:
                res = headingAction.apply(getOpposite(getDrone().getLastTurn()));
                nextStep = ECHO_FRONT;
                break;

            case ECHO_FRONT:
                res = echoAction.apply(FRONT);
                nextStep = FLY;
                break;

            case FLY:
                int frontDist = getDrone().getMargins().getLocal(FRONT)._2;
                if (frontDist > 0) {
                    res = flyAction.apply();
                    nextStep = ECHO_FRONT;
                } else {
                    res = flyAction.apply();
                    if (turnNotBis) {
                        getDrone().setLastTurn(ZQSD.getOpposite(getDrone().getLastTurn()));
                    }
                    finish();
                }
                break;

            default:
                throw new IllegalStateException("Unknown step : " + step);
        }

        currentStep = nextStep;

        return res;
    }

    private String decideToFly() {
        String res;

        int frontDist = getDrone().getMargins().getLocal(FRONT)._2;

        if (frontDist > 1) res = flyAction.apply();
        else throw new IllegalStateException("Not enough margin left !");

        return res;
    }

    public enum Step {
        FLY_FRONT_1,
        TURN_1,
        ECHO_SIDE,
        FLY_FRONT_2,
        TURN_2,
        TURN_2_BIS,
        TURN_3,
        TURN_4,
        ECHO_FRONT,
        FLY,
    }
}