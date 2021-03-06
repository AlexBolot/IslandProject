package fr.unice.polytech.si3.qgl.ise.actions.loop;

import fr.unice.polytech.si3.qgl.ise.actions.Action;
import fr.unice.polytech.si3.qgl.ise.actions.drone.ChangeLineAction;
import fr.unice.polytech.si3.qgl.ise.actions.drone.ScanLineStrategy;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;

import java.util.ArrayList;
import java.util.Arrays;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.GROUND;

public class ScanLineLoopAction extends Action {
    private final Drone drone;
    private final ArrayList<Action> actions;

    /**
     * @param drone            Drone to update
     * @param scanLineStrategy strategy to use to scan a line
     */
    ScanLineLoopAction(Drone drone, ScanLineStrategy scanLineStrategy) {
        actions = new ArrayList<>(Arrays.asList(scanLineStrategy, new ChangeLineAction(drone)));
        this.drone = drone;
    }

    @Override
    public String apply() {
        String res;

        for (Action action : actions) {
            if (!action.isFinished()) {
                res = action.apply();

                if (!res.isEmpty()) {
                    return res;
                }
            }
        }

        if (drone.getMargins().getLocal(drone.getLastEcho())._1 == GROUND) {
            actions.forEach(Action::reset);
            return apply();
        } else {
            finish();
            return "";
        }
    }
}
