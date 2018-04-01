package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.simple.EchoAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.FlyAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.ScanAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.Biome;
import fr.unice.polytech.si3.qgl.ise.map.Tile;
import scala.Tuple2;

import java.util.List;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.ScanLineAction.Step.*;
import static fr.unice.polytech.si3.qgl.ise.enums.Biome.OCEAN;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.BORDER;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.GROUND;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.FRONT;

public class ScanLineAction extends DroneAction {
    private Step currentStep;
    private final FlyAction flyAction;
    private final ScanAction scanAction;
    private final EchoAction echoAction;

    public ScanLineAction(Drone drone) {
        super(drone);
        currentStep = Scan;
        flyAction = new FlyAction(drone);
        scanAction = new ScanAction(drone);
        echoAction = new EchoAction(drone);
    }

    @Override
    public String apply() {
        return apply(currentStep);
    }

    private String apply(Step step) {
        String res;
        Step nextStep;

        switch (step) {
            case Scan:
                res = scanAction.apply();
                nextStep = FlyOrTurn;
                break;

            case FlyOrTurn:
                res = checkResult();

                if (getDrone().getMargins().getGlobal(FRONT)._2 < 2) {
                    res = echoAction.apply(FRONT);
                    this.finish();
                }

                if (!res.isEmpty()) nextStep = Scan;
                else {
                    res = echoAction.apply(FRONT);
                    nextStep = EchoFront;
                }
                break;

            case EchoFront:
                res = flyAction.apply();
                Tuple2<Obstacle, Integer> margin = getDrone().getMargins().getLocal(FRONT);
                if (margin._1 == BORDER) {
                    this.finish();
                }
                nextStep = Reach;
                break;

            case Reach:
                Tuple2<Obstacle, Integer> lastMargin = getDrone().getMargins().getLocal(FRONT);
                if (lastMargin._1 == GROUND && lastMargin._2 > 0) {
                    res = flyAction.apply();
                    nextStep = Reach;
                } else {
                    res = scanAction.apply();
                    nextStep = FlyOrTurn;
                }
                break;

            default:
                throw new IllegalStateException("Unkown step : " + step);
        }

        currentStep = nextStep;

        return res;
    }

    private String checkResult() {
        Tile tile = getDrone().getMap().getTile(getDrone().getCoords());
        List<Biome> biomes = tile.getPossibleBiomes();

        String res = "";

        if (biomes.stream().anyMatch(biome -> biome != OCEAN)) res = flyAction.apply();

        return res;
    }

    @Override
    public void reset() {
        super.reset();
        currentStep = Scan;
    }

    public enum Step {
        Scan,
        FlyOrTurn,
        EchoFront,
        Reach,
    }
}