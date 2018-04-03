package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.simple.EchoAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.FlyAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.utilities.Margin;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.ReachIslandAction.Step.FLY_TO_ISLAND;
import static fr.unice.polytech.si3.qgl.ise.actions.drone.ReachIslandAction.Step.L_TURN;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.GROUND;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.FRONT;

public class ReachIslandAction extends DroneAction {
    private final FlyAction flyAction;
    private final EchoAction echoAction;
    private final LTurnAction lTurnAction;
    private Step currentStep;

    public ReachIslandAction(Drone drone) {
        super(drone);
        currentStep = L_TURN;
        flyAction = new FlyAction(drone);
        echoAction = new EchoAction(drone);
        lTurnAction = new LTurnAction(drone);
    }

    @Override
    public String apply() {
        return apply(currentStep);
    }

    private String apply(Step step) {
        String res;
        Step nextStep = null;

        switch (step) {
            case L_TURN:
                if (!lTurnAction.isFinished()) {
                    res = lTurnAction.apply();
                    nextStep = L_TURN;
                    break;
                }

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

    @Override
    public void reset() {
        super.reset();
        currentStep = L_TURN;
        lTurnAction.reset();
    }

    public enum Step {
        L_TURN,
        ECHO_FRONT,
        FLY_TO_ISLAND
    }
}