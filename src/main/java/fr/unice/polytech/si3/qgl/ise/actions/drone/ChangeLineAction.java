package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.simple.EchoAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.FlyAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.HeadingAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.utilities.Margin;
import scala.Tuple2;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.ChangeLineAction.Step.*;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.GROUND;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.FRONT;

public class ChangeLineAction extends DroneAction {
    private final FlyAction flyAction;
    private final EchoAction echoAction;
    private final HeadingAction headingAction;
    private Step currentStep;
    private ZQSD generalDirection;

    public ChangeLineAction(Drone drone) {
        super(drone);
        currentStep = ECHO_SIDE;
        flyAction = new FlyAction(drone);
        echoAction = new EchoAction(drone);
        headingAction = new HeadingAction(drone);
        generalDirection = null;
    }

    @Override
    public String apply() {
        return apply(ZQSD.getOpposite(getDrone().getLastTurn()));
    }

    private String apply(ZQSD direction) {
        String res;
        Step nextStep = null;

        switch (currentStep) {
            case ECHO_SIDE:
                res = echoAction.apply(direction);
                nextStep = FLY_OR_TURN;
                break;

            case FLY_OR_TURN:
                generalDirection = direction;
                res = flyOrTurn();

                if (!res.isEmpty() && getDrone().getMargins().getGlobal(FRONT)._2 > 1) {
                    nextStep = ECHO_SIDE;
                    break;
                } else {
                    res = headingAction.apply(generalDirection);
                    nextStep = TURN_2;
                    break;
                }

            case TURN_1:
                generalDirection = direction;

                res = headingAction.apply(generalDirection);
                nextStep = TURN_2;
                break;

            case TURN_2:
                res = headingAction.apply(generalDirection);
                nextStep = ECHO_FRONT;
                break;

            case ECHO_FRONT:
                res = echoAction.apply(FRONT);
                nextStep = REACH_ISLAND;
                break;

            case REACH_ISLAND:
                res = reachIsland();

                if (!res.isEmpty()) nextStep = REACH_ISLAND;
                else finish();
                break;

            default:
                throw new IllegalStateException("Unknown step : " + currentStep);
        }

        currentStep = nextStep;

        return res;
    }

    private String flyOrTurn() {
        Margin margins = getDrone().getMargins();
        ZQSD lastEcho = getDrone().getLastEcho();
        Tuple2<Obstacle, Integer> lastMargin = margins.getLocal(lastEcho);

        String res = "";

        if (lastMargin._1 == GROUND && lastMargin._2 <= 1) res = flyAction.apply();

        return res;
    }

    private String reachIsland() {
        Margin margins = getDrone().getMargins();
        Tuple2<Obstacle, Integer> frontMargin = margins.getLocal(FRONT);
        String res = "";

        if (frontMargin._1 == GROUND && frontMargin._2 >= 0) res = flyAction.apply();

        return res;
    }

    @Override
    public void reset() {
        super.reset();
        currentStep = ECHO_SIDE;
    }

    public enum Step {
        ECHO_SIDE,
        FLY_OR_TURN,
        TURN_1,
        TURN_2,
        ECHO_FRONT,
        REACH_ISLAND,
    }
}