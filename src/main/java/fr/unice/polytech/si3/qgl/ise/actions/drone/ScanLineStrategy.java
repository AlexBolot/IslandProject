package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.simple.EchoAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.FlyAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.ScanAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.Biome;
import fr.unice.polytech.si3.qgl.ise.map.Tile;

import java.util.List;

import static fr.unice.polytech.si3.qgl.ise.enums.Biome.OCEAN;

public abstract class ScanLineStrategy extends DroneAction {
    protected FlyAction flyAction;
    protected EchoAction echoAction;
    protected ScanAction scanAction;

    ScanLineStrategy(Drone drone) {
        super(drone);
        flyAction = new FlyAction(drone);
        scanAction = new ScanAction(drone);
        echoAction = new EchoAction(drone);
    }

    protected String checkResult() {
        Tile tile = getDrone().getMap().getTile(getDrone().getCoordinates());
        List<Biome> biomes = tile.getPossibleBiomes();

        String res = "";

        if (biomes.stream().anyMatch(biome -> biome != OCEAN && tile.getBiomePercentage(biome) == 100))
            res = flyAction.apply();

        return res;
    }
}
