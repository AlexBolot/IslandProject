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
    private Step currentStep;
    private FlyAction flyAction;
    private EchoAction echoAction;
    private HeadingAction headingAction;

    private ZQSD generalDirection;

    public ChangeLineAction(Drone drone) {
        super(drone);
        currentStep = EchoSide;
        flyAction = new FlyAction(drone);
        echoAction = new EchoAction(drone);
        headingAction = new HeadingAction(drone);
        generalDirection = null;
    }

    @Override
    public String apply() {
        return apply(ZQSD.getOpposite(getDrone().getLastTurn()));
    }

    public String apply(ZQSD direction) {
        String res;
        Step nextStep = null;

        switch (currentStep) {
            case EchoSide:
                res = echoAction.apply(direction);
                nextStep = FlyOrTurn;
                break;

            case FlyOrTurn:
                generalDirection = direction;
                res = flyOrTurn();

                if (!res.isEmpty() && getDrone().getMargins().getGlobal(FRONT)._2 > 1) {
                    nextStep = EchoSide;
                    break;
                } else {
                    res = headingAction.apply(generalDirection);
                    nextStep = Turn2;
                    break;
                }

            case Turn1:
                generalDirection = direction;

                res = headingAction.apply(generalDirection);
                nextStep = Turn2;
                break;

            case Turn2:
                res = headingAction.apply(generalDirection);
                nextStep = EchoFront;
                break;

            case EchoFront:
                res = echoAction.apply(FRONT);
                nextStep = ReachIsland;
                break;

            case ReachIsland:
                res = reachIsland();

                if (!res.isEmpty()) nextStep = ReachIsland;
                else finish();
                break;

            default:
                throw new IllegalStateException("Unkown step : " + currentStep);
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
        currentStep = EchoSide;
    }

    public enum Step {
        EchoSide,
        FlyOrTurn,
        Turn1,
        Turn2,
        EchoFront,
        ReachIsland,
    }
}