package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.StopAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.EchoAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.FlyAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.HeadingAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import scala.Tuple2;

import java.util.HashMap;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.GTurnAction.Step.*;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.FRONT;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.getOpposite;

public class GTurnAction extends DroneAction
{
    private Step          currentStep;
    private FlyAction     flyAction;
    private EchoAction    echoAction;
    private HeadingAction headingAction;
    private boolean turnNotBis;

    public GTurnAction (Drone drone)
    {
        super(drone);
        currentStep = FlyFront_1;
        flyAction = new FlyAction(drone);
        echoAction = new EchoAction(drone);
        headingAction = new HeadingAction(drone);
        turnNotBis = false;
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
                nextStep = EchoSide;
                break;

            case EchoSide:
                res = echoAction.apply(getDrone().getLastTurn());
                nextStep = FlyFront_2;
                break;

            case FlyFront_2:
                HashMap<DroneEnums.ZQSD, Tuple2<DroneEnums.Obstacle, Integer>> margins = getDrone().getMargins();
                DroneEnums.ZQSD lastTurn = getDrone().getLastTurn();
                Tuple2<DroneEnums.Obstacle, Integer> lastMargin = margins.get(lastTurn);
                if (lastMargin._1 == DroneEnums.Obstacle.GROUND) {
                    nextStep = Turn_2;
                } else {
                    nextStep = Turn_2_bis;
                }
                res = decideToFly();
                break;

            case Turn_2:
                turnNotBis = true;
                res = headingAction.apply(getDrone().getLastTurn());
                nextStep = Turn_3;
                break;

            case Turn_2_bis:
                res = headingAction.apply(getDrone().getLastTurn());
                nextStep = EchoFront;
                break;

            case Turn_3:
                res = headingAction.apply(getDrone().getLastTurn());
                nextStep = Turn_4;
                break;

            case Turn_4:
                res = headingAction.apply(getOpposite(getDrone().getLastTurn()));
                nextStep = EchoFront;
                break;

            case EchoFront:
                res = echoAction.apply(FRONT);
                nextStep = Fly;
                break;

            case Fly:
                int frontDist = getDrone().getMargins().get(FRONT)._2;
                if (frontDist > 0) {
                    res = flyAction.apply();
                    nextStep = EchoFront;
                } else {
                    res = flyAction.apply();
                    if (turnNotBis) {
                        getDrone().setLastTurn(DroneEnums.ZQSD.getOpposite(getDrone().getLastTurn()));
                    }
                    finish();
                }
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
        EchoSide,
        FlyFront_2,
        Turn_2,
        Turn_2_bis,
        Turn_3,
        Turn_4,
        EchoFront,
        Fly,
    }
}