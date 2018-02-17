package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.simple.FlyAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.ScanAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.Biome;
import fr.unice.polytech.si3.qgl.ise.map.Tile;

import java.util.List;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.ScanLineAction.Step.FlyOrTurn;
import static fr.unice.polytech.si3.qgl.ise.actions.drone.ScanLineAction.Step.Scan;
import static fr.unice.polytech.si3.qgl.ise.enums.Biome.OCEAN;

public class ScanLineAction extends DroneAction
{
    private Step       currentStep;
    private FlyAction  flyAction;
    private ScanAction scanAction;

    public ScanLineAction (Drone drone)
    {
        super(drone);
        currentStep = Scan;
        flyAction = new FlyAction(drone);
        scanAction = new ScanAction(drone);
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
            case Scan:
                res = scanAction.apply();
                nextStep = FlyOrTurn;
                break;

            case FlyOrTurn:
                res = checkResult();

                if (!res.isEmpty()) nextStep = Scan;
                else this.finish();
                break;

            default:
                throw new IllegalStateException("Unkown step : " + step);
        }

        currentStep = nextStep;

        return res;
    }

    private String checkResult ()
    {
        Tile tile = getDrone().getMap().getTile(getDrone().getCoords());
        List<Biome> biomes = tile.getPossibleBiomes();

        String res = "";

        if (biomes.stream().anyMatch(biome -> biome != OCEAN))
        {
            /*if (getDrone().getMargins().get(FRONT)._2 > 1) res = flyAction.apply();
            else res = StopAction.get();*/

            res = flyAction.apply();
        }

        return res;
    }

    @Override
    public void reset ()
    {
        super.reset();
        currentStep = Scan;
    }

    public enum Step
    {
        Scan,
        FlyOrTurn
    }
}