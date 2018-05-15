package fr.unice.polytech.si3.qgl.ise.actions.loop;

import fr.unice.polytech.si3.qgl.ise.actions.Action;
import fr.unice.polytech.si3.qgl.ise.actions.drone.GTurnAction;
import fr.unice.polytech.si3.qgl.ise.actions.drone.PassIslandAction;
import fr.unice.polytech.si3.qgl.ise.actions.drone.ScanLineStrategy;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;

public class ScanIslandLoopAction extends Action {
    private final Drone drone;
    private final GTurnAction gTurnAction;
    private final ScanLineLoopAction scanLineLoopAction;
    private final PassIslandAction passIslandAction;

    /**
     * Used to scan the Island with how to do it in parameter
     *
     * @param drone            Drone to update
     * @param scanLineStrategy strat to use to scan a line
     */
    public ScanIslandLoopAction(Drone drone, ScanLineStrategy scanLineStrategy) {
        this.drone = drone;
        this.scanLineLoopAction = new ScanLineLoopAction(drone, scanLineStrategy);
        this.gTurnAction = new GTurnAction(drone);
        this.passIslandAction = new PassIslandAction(drone);
    }

    @Override
    public String apply() {
        IslandMap map = drone.getMap();
        String res = "";

        if (!scanLineLoopAction.isFinished()) res = scanLineLoopAction.apply();
        if (!res.isEmpty()) return res;

        if (!map.getCreeks().isEmpty()) {
            finish();
            return "";
        }
        if (!passIslandAction.isFinished()) res = passIslandAction.apply();
        if (!res.isEmpty()) return res;

        if (!gTurnAction.isFinished()) res = gTurnAction.apply();
        else {
            scanLineLoopAction.reset();
            res = apply();
        }

        return res;
    }
}
