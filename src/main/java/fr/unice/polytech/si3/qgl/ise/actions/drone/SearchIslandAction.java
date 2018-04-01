package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.StopAction;
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
    private Step currentStep;
    private final FlyAction flyAction;
    private final EchoAction echoAction;
    private final HeadingAction headingAction;

    public SearchIslandAction(Drone drone) {
        super(drone);
        currentStep = ChooseDirection;
        flyAction = new FlyAction(drone);
        echoAction = new EchoAction(drone);
        headingAction = new HeadingAction(drone);
    }

    @Override
    public String apply() {
        return apply(currentStep);
    }

    private String apply(Step step) {
        String res;
        Step nextStep = null;

        switch (step) {
            case ChooseDirection:
                res = chooseDirection();
                nextStep = EchoSide;
                break;

            case EchoSide:
                res = echoAction.apply(ZQSD.getOpposite(getDrone().getLastTurn()));
                nextStep = FlyFront;
                break;

            case FlyFront:
                res = decideToFly();

                if (!res.isEmpty()) nextStep = EchoSide;
                else finish();
                break;

            default:
                throw new IllegalStateException("Unkown step : " + step);
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
            else res = new StopAction(getDrone()).apply();
        }

        return res;
    }

    @Override
    public void reset() {
        super.reset();
        currentStep = ChooseDirection;
    }

    public enum Step {
        ChooseDirection,
        EchoSide,
        FlyFront
    }
}