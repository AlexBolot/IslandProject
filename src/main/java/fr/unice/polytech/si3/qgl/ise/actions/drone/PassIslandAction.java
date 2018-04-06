package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.simple.EchoAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.FlyAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.utilities.Margin;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.PassIslandAction.Step.ECHO_SIDE;
import static fr.unice.polytech.si3.qgl.ise.actions.drone.PassIslandAction.Step.FLY_OR_TURN;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.GROUND;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.FRONT;

public class PassIslandAction extends DroneAction {
    private final FlyAction flyAction;
    private final EchoAction echoAction;
    private Step currentStep;
    private boolean passing;

    public PassIslandAction(Drone drone) {
        super(drone);
        currentStep = ECHO_SIDE;
        flyAction = new FlyAction(drone);
        echoAction = new EchoAction(drone);
        passing = true;
    }

    @Override
    public String apply() {
        return apply(currentStep);
    }

    private String apply(Step step) {
        String res;
        Step nextStep = null;

        switch (step) {
            case ECHO_SIDE:
                res = echoAction.apply(getDrone().getLastTurn());
                nextStep = FLY_OR_TURN;
                break;

            case FLY_OR_TURN:
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

    private String decideToFly() {
        Margin margins = getDrone().getMargins();
        String res = "";
        if (passing) {
            res = flyAction.apply();
            if (margins.getLocal(getDrone().getLastEcho())._1 == GROUND) {
                passing = false;
            }
        }

        if (margins.getLocal(getDrone().getLastEcho())._1 == GROUND) {
            if (margins.getLocal(FRONT)._2 > 1) res = flyAction.apply();
            else throw new IllegalStateException("Not enough margin left !");
        }

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
    }
}