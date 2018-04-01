package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.simple.HeadingAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.LTurnAction.Step.*;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD;

public class LTurnAction extends DroneAction {
    private Step currentStep;
    private final HeadingAction headingAction;

    LTurnAction(Drone drone) {
        super(drone);
        currentStep = Turn1;
        headingAction = new HeadingAction(drone);
    }

    @Override
    public String apply() {
        return apply(getDrone().getLastEcho());
    }

    public String apply(ZQSD direction) {
        String res;
        Step nextStep = null;

        switch (currentStep) {
            case Turn1:
                res = headingAction.apply(direction);
                nextStep = Turn2;
                break;

            case Turn2:
                res = headingAction.apply(direction);
                nextStep = Turn3;
                break;

            case Turn3:
                res = headingAction.apply(ZQSD.getOpposite(direction));
                this.finish();
                break;

            default:
                throw new IllegalStateException("Unkown step : " + currentStep);
        }

        currentStep = nextStep;

        return res;
    }

    @Override
    public void reset() {
        super.reset();
        currentStep = Turn1;
    }

    public enum Step {
        Turn1,
        Turn2,
        Turn3
    }
}