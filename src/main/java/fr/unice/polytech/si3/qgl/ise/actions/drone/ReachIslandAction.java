package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.simple.EchoAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.FlyAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.utilities.Margin;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.ReachIslandAction.Step.FlyToIsland;
import static fr.unice.polytech.si3.qgl.ise.actions.drone.ReachIslandAction.Step.LTurn;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.GROUND;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.FRONT;

public class ReachIslandAction extends DroneAction {
    private Step currentStep;
    private final FlyAction flyAction;
    private final EchoAction echoAction;
    private final LTurnAction lTurnAction;

    public ReachIslandAction(Drone drone) {
        super(drone);
        currentStep = LTurn;
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
            case LTurn:
                if (!lTurnAction.isFinished()) {
                    res = lTurnAction.apply();
                    nextStep = LTurn;
                    break;
                }

            case EchoFront:
                res = echoAction.apply(FRONT);
                nextStep = FlyToIsland;
                break;

            case FlyToIsland:
                res = decideToFly();
                if (res.isEmpty()) this.finish();
                else nextStep = FlyToIsland;
                break;

            default:
                throw new IllegalStateException("Unkown step : " + step);
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
        currentStep = LTurn;
        lTurnAction.reset();
    }

    public enum Step {
        LTurn,
        EchoFront,
        FlyToIsland
    }
}