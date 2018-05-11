package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.simple.HeadingAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.LTurnAction.Step.*;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD;

public class LTurnAction extends DroneAction {
    private final HeadingAction headingAction;
    private Step currentStep;

    LTurnAction(Drone drone) {
        super(drone);
        currentStep = TURN_1;
        headingAction = new HeadingAction(drone);
    }

    @Override
    public void reset() {
        super.reset();
        currentStep = TURN_1;
    }

    @Override
    public String apply() {
        return apply(getDrone().getLastEcho());
    }

    private String apply(ZQSD direction) {
        String res;
        Step nextStep = null;

        switch (currentStep) {
            case TURN_1:
                res = headingAction.apply(direction);
                nextStep = TURN_2;
                break;

            case TURN_2:
                res = headingAction.apply(direction);
                nextStep = TURN_3;
                break;

            case TURN_3:
                res = headingAction.apply(ZQSD.getOpposite(direction));
                this.finish();
                break;

            default:
                throw new IllegalStateException("Unknown step : " + currentStep);
        }

        currentStep = nextStep;

        return res;
    }

    public enum Step {
        TURN_1,
        TURN_2,
        TURN_3
    }
}