package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.StopAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.EchoAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.FlyAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import scala.Tuple2;

import java.util.HashMap;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.PassIslandAction.Step.EchoSide;
import static fr.unice.polytech.si3.qgl.ise.actions.drone.PassIslandAction.Step.FlyOrTurn;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.GROUND;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.FRONT;

public class PassIslandAction extends DroneAction
{
    private Step          currentStep;
    private FlyAction     flyAction;
    private EchoAction    echoAction;

    public PassIslandAction (Drone drone)
    {
        super(drone);
        currentStep = EchoSide;
        flyAction = new FlyAction(drone);
        echoAction = new EchoAction(drone);
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
            case EchoSide:
                res = echoAction.apply(getDrone().getLastTurn());
                nextStep = FlyOrTurn;
                break;

            case FlyOrTurn:
                res = decideToFly();

                if(!res.isEmpty()) nextStep = EchoSide;
                else finish();
                break;

            default:
                throw new IllegalStateException("Unkown step : " + step);
        }

        currentStep = nextStep;

        return res;
    }

    private String decideToFly ()
    {
        HashMap<DroneEnums.ZQSD, Tuple2<DroneEnums.Obstacle, Integer>> margins = getDrone().getMargins();
        String res = "";

        if (margins.get(getDrone().getLastEcho())._1 == GROUND)
        {
            if (margins.get(FRONT)._2 > 1) res = flyAction.apply();
            else res = StopAction.get();
        }

        return res;
    }

    @Override
    public void reset ()
    {
        super.reset();
        currentStep = EchoSide;
    }

    public enum Step
    {
        EchoSide,
        FlyOrTurn,
    }
}