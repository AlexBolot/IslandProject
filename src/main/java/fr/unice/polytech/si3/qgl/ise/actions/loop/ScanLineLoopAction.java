package fr.unice.polytech.si3.qgl.ise.actions.loop;

import fr.unice.polytech.si3.qgl.ise.actions.Action;
import fr.unice.polytech.si3.qgl.ise.actions.drone.ChangeLineAction;
import fr.unice.polytech.si3.qgl.ise.actions.drone.ScanLineAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;

import java.util.ArrayList;
import java.util.Arrays;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.GROUND;

public class ScanLineLoopAction extends Action {
    private Drone drone;
    private ArrayList<Action> actions;

    public ScanLineLoopAction(Drone drone) {
        actions = new ArrayList<>(Arrays.asList(new ScanLineAction(drone), new ChangeLineAction(drone)));
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
