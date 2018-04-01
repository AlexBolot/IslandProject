package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.StopAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.EchoAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.DroneInitAction.Step.*;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;

public class DroneInitAction extends DroneAction {
    private Step currentStep;
    private final EchoAction echoAction;

    public DroneInitAction(Drone drone) {
        super(drone);
        currentStep = EchoFront;
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
            case EchoFront:
                res = echoAction.apply(FRONT);
                nextStep = EchoRight;
                break;

            case EchoRight:
                res = echoAction.apply(RIGHT);
                nextStep = EchoLeft;
                break;

            case EchoLeft:
                res = echoAction.apply(LEFT);
                this.finish();
                break;

            case OzCheck:
                if (getDrone().getMargins().getLocal(FRONT)._2 < 1 && getDrone().getMargins().getLocal(LEFT)._2 < 1 && getDrone().getMargins().getLocal(RIGHT)._2 < 1)
                    return new StopAction(getDrone()).apply();

            default:
                throw new IllegalStateException("Unkown step : " + step);
        }

        currentStep = nextStep;

        return res;
    }

    @Override
    public void reset() {
        super.reset();
        currentStep = EchoFront;
    }

    public enum Step {
        EchoFront,
        EchoRight,
        EchoLeft,
        OzCheck
    }
}