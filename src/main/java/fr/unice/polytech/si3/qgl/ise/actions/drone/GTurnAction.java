package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.StopAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.EchoAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.FlyAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.HeadingAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.GTurnAction.Step.*;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.FRONT;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.getOpposite;

public class GTurnAction extends DroneAction
{
    private Step          currentStep;
    private FlyAction     flyAction;
    private EchoAction    echoAction;
    private HeadingAction headingAction;

    public GTurnAction (Drone drone)
    {
        super(drone);
        currentStep = FlyFront_1;
        flyAction = new FlyAction(drone);
        echoAction = new EchoAction(drone);
        headingAction = new HeadingAction(drone);
    }

    @Override
    public String apply ()
    {
        return apply(currentStep);
    }

    public String apply (Step step)
    {
        String res;
        Step nextStep = null;

        switch (step)
        {
            case FlyFront_1:
                res = decideToFly();
                nextStep = Turn_1;
                break;

            case Turn_1:
                res = headingAction.apply(getDrone().getLastTurn());
                nextStep = FlyFront_2;
                break;

            case FlyFront_2:
                res = decideToFly();
                nextStep = Turn_2;
                break;

            case Turn_2:
                res = headingAction.apply(getDrone().getLastTurn());
                nextStep = Turn_3;
                break;

            case Turn_3:
                res = headingAction.apply(getDrone().getLastTurn());
                nextStep = Turn_4;
                this.finish();
                break;

            case Turn_4:
                res = headingAction.apply(getOpposite(getDrone().getLastTurn()));
                this.finish();
                break;

            default:
                throw new IllegalStateException("Unkown step : " + step);
        }

        currentStep = nextStep;

        return res;
    }

    private String decideToFly ()
    {
        String res;

        int frontDist = getDrone().getMargins().get(FRONT)._2;

        if (frontDist > 1) res = flyAction.apply();
        else res = StopAction.get();

        return res;
    }

    @Override
    public void reset ()
    {
        super.reset();
        currentStep = FlyFront_1;
    }

    public enum Step
    {
        FlyFront_1,
        Turn_1,
        FlyFront_2,
        Turn_2,
        Turn_3,
        Turn_4,
    }
}