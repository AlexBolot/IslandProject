package fr.unice.polytech.si3.qgl.ise.actions.loop;

import fr.unice.polytech.si3.qgl.ise.actions.Action;
import fr.unice.polytech.si3.qgl.ise.actions.StopAction;
import fr.unice.polytech.si3.qgl.ise.actions.drone.GTurnAction;
import fr.unice.polytech.si3.qgl.ise.actions.drone.PassIslandAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;

public class ScanIslandLoopAction extends Action {
    private Drone drone;
    private GTurnAction gTurnAction;
    private ScanLineLoopAction scanLineLoopAction;
    private PassIslandAction passIslandAction;

    public ScanIslandLoopAction(Drone drone) {
        this.drone = drone;
        this.scanLineLoopAction = new ScanLineLoopAction(drone);
        this.gTurnAction = new GTurnAction(drone);
        this.passIslandAction = new PassIslandAction(drone);
    }

    @Override
    public String apply() {
        IslandMap map = drone.getMap();
        String res = "";

        if (!scanLineLoopAction.isFinished()) res = scanLineLoopAction.apply();
        if (!res.isEmpty()) return res;

        if (!map.getCreeks().isEmpty() && map.getEmergencySite() != null) {
            finish();
            return StopAction.get();
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
