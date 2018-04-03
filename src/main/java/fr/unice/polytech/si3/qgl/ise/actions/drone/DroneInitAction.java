package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.StopAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.EchoAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.DroneInitAction.Step.*;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;

public class DroneInitAction extends DroneAction {
    private final EchoAction echoAction;
    private Step currentStep;

    public DroneInitAction(Drone drone) {
        super(drone);
        currentStep = ECHO_FRONT;
        echoAction = new EchoAction(drone);
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
                nextStep = ECHO_RIGHT;
                break;

            case ECHO_RIGHT:
                res = echoAction.apply(RIGHT);
                nextStep = ECHO_LEFT;
                break;

            case ECHO_LEFT:
                res = echoAction.apply(LEFT);
                nextStep = OZ_CHECK;
                break;

            case OZ_CHECK:
                res = "";
                if (getDrone().getMargins().getLocal(FRONT)._2 < 1 && getDrone().getMargins().getLocal(LEFT)._2 < 1 && getDrone().getMargins().getLocal(RIGHT)._2 < 1)
                    return new StopAction(getDrone()).apply();
                this.finish();
                break;

            default:
                throw new IllegalStateException("Unknown step : " + step);
        }

        currentStep = nextStep;

        return res;
    }

    @Override
    public void reset() {
        super.reset();
        currentStep = ECHO_FRONT;
    }

    public enum Step {
        ECHO_FRONT,
        ECHO_RIGHT,
        ECHO_LEFT,
        OZ_CHECK
    }
}