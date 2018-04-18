package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import scala.Tuple2;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.ScanLineActionStraight.Step.*;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.BORDER;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.GROUND;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.FRONT;

public class ScanLineActionStraight extends ScanLineStrategy {
    private Step currentStep;
    private int stepCount = 0;
    private final int pace;

    public ScanLineActionStraight(Drone drone, int pace) {
        super(drone);
        this.pace = pace;
        currentStep = SCAN;
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
                stepCount = 0;
                break;

            case FLY_OR_TURN:
                res = stepCount == 0 ? checkResult() : flyAction.apply();
                stepCount++;

                if (getDrone().getMargins().getGlobal(FRONT)._2 < 2) {
                    res = echoAction.apply(FRONT);
                    this.finish();
                }

                if (!res.isEmpty()) {
                    nextStep = (stepCount == pace) ? SCAN : FLY_OR_TURN;
                } else {
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
                    currentStep = SCAN;
                    return apply();
                }
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
        currentStep = SCAN;
    }

    public enum Step {
        SCAN,
        FLY_OR_TURN,
        ECHO_FRONT,
        REACH,
    }

    public enum Strategy {
        FULL(1),
        HALF(2),
        THIRD(3);

        private int pace;

        Strategy(int pace) {
            this.pace = pace;
        }

        public int getPace() {
            return pace;
        }
    }
}