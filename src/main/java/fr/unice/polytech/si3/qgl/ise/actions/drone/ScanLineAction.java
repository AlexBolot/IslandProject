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
    private final FlyAction flyAction;
    private final ScanAction scanAction;
    private final EchoAction echoAction;
    private Step currentStep;

    public ScanLineAction(Drone drone) {
        super(drone);
        currentStep = SCAN;
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
            case SCAN:
                res = scanAction.apply();
                nextStep = FLY_OR_TURN;
                break;

            case FLY_OR_TURN:
                res = checkResult();

                if (getDrone().getMargins().getGlobal(FRONT)._2 < 2) {
                    res = echoAction.apply(FRONT);
                    this.finish();
                }

                if (!res.isEmpty()) nextStep = SCAN;
                else {
                    res = echoAction.apply(FRONT);
                    nextStep = ECHO_FRONT;
                }
                break;

            case ECHO_FRONT:
                res = flyAction.apply();
                Tuple2<Obstacle, Integer> margin = getDrone().getMargins().getLocal(FRONT);
                if (margin._1 == BORDER) {
                    this.finish();
                }
                nextStep = REACH;
                break;

            case REACH:
                Tuple2<Obstacle, Integer> lastMargin = getDrone().getMargins().getLocal(FRONT);
                if (lastMargin._1 == GROUND && lastMargin._2 > 0) {
                    res = flyAction.apply();
                    nextStep = REACH;
                } else {
                    res = scanAction.apply();
                    nextStep = FLY_OR_TURN;
                }
                break;

            default:
                throw new IllegalStateException("Unknown step : " + step);
        }

        currentStep = nextStep;

        return res;
    }

    private String checkResult() {
        Tile tile = getDrone().getMap().getTile(getDrone().getCoordinates());
        List<Biome> biomes = tile.getPossibleBiomes();

        String res = "";

        if (biomes.stream().anyMatch(biome -> biome != OCEAN && tile.getBiomePercentage(biome) == 100))
            res = flyAction.apply();

        return res;
    }

    @Override
    public void reset() {
        super.reset();
        currentStep = SCAN;
    }

    public enum Step {
        SCAN,
        FLY_OR_TURN,
        ECHO_FRONT,
        REACH,
    }
}