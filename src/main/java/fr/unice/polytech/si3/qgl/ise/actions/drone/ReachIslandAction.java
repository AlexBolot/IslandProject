package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.simple.EchoAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.FlyAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.utilities.Margin;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.ReachIslandAction.Step.*;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.GROUND;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.FRONT;

public class ReachIslandAction extends DroneAction {
    private final FlyAction flyAction;
    private final EchoAction echoAction;
    private Step currentStep;

    public ReachIslandAction(Drone drone) {
        super(drone);
        currentStep = ECHO_FRONT;
        flyAction = new FlyAction(drone);
        echoAction = new EchoAction(drone);
    }

    @Override
    public void reset() {
        super.reset();
        currentStep = ECHO_FRONT;
        echoAction.reset();
    }

    @Override
    public String apply() {
        return apply(currentStep);
    }

    private String apply(Step step) {
        String res;
        Step nextStep = null;

        switch (step) {
            case ECHO_FRONT:
                res = echoAction.apply(FRONT);
                nextStep = FLY_TO_ISLAND;
                break;

            case FLY_TO_ISLAND:
                res = decideToFly();
                if (res.isEmpty()) this.finish();
                else nextStep = FLY_TO_ISLAND;
                break;

            default:
                throw new IllegalStateException("Unknown step : " + step);
        }

        currentStep = nextStep;

        return res;
    }

    private String decideToFly() {
        Margin margins = getDrone().getMargins();
        String res = "";

        if (margins.getLocal(FRONT)._1 == GROUND) {
            int frontDist = margins.getLocal(FRONT)._2;

            if (frontDist >= 0) return flyAction.apply();
        }
        return res;
    }

    public enum Step {
        ECHO_FRONT,
        FLY_TO_ISLAND
    }
}