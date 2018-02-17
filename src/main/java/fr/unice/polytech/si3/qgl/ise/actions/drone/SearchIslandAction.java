package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.StopAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.EchoAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.FlyAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.HeadingAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import scala.Tuple2;

import java.util.HashMap;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.SearchIslandAction.Step.*;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.GROUND;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;

public class SearchIslandAction extends DroneAction
{
    private Step          currentStep;
    private FlyAction     flyAction;
    private EchoAction    echoAction;
    private HeadingAction headingAction;

    public SearchIslandAction (Drone drone)
    {
        super(drone);
        currentStep = ChoseDirection;
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
            case ChoseDirection:
                res = choseDirection();
                nextStep = EchoSide;
                break;

            case EchoSide:
                res = echoAction.apply(ZQSD.getOpposite(getDrone().getLastTurn()));
                nextStep = FlyFront;
                break;

            case FlyFront:
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

    private String choseDirection ()
    {
        HashMap<ZQSD, Tuple2<Obstacle, Integer>> margins = getDrone().getMargins();
        ZQSD dir;

        boolean facingGround = margins.get(FRONT)._1 == GROUND;
        int marginRight = margins.get(RIGHT)._2;
        int marginLeft = margins.get(LEFT)._2;

        if (facingGround) dir = (marginRight < marginLeft) ? RIGHT : LEFT;
        else dir = (marginRight > marginLeft) ? RIGHT : LEFT;

        return headingAction.apply(dir);
    }

    private String decideToFly ()
    {
        HashMap<ZQSD, Tuple2<Obstacle, Integer>> margins = getDrone().getMargins();
        String res = "";

        if (margins.get(getDrone().getLastEcho())._1 == GROUND || !getDrone().hasFoundIsland())
        {
            int frontDist = margins.get(FRONT)._2;

            if (frontDist > 1) res = flyAction.apply();
            else  res= StopAction.get();
        }

        return res;
    }

    @Override
    public void reset ()
    {
        super.reset();
        currentStep = ChoseDirection;
    }

    public enum Step
    {
        ChoseDirection,
        EchoSide,
        FlyFront
    }
}