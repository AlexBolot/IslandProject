package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.actions.simple.EchoAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.FlyAction;
import fr.unice.polytech.si3.qgl.ise.actions.simple.ScanAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.map.Tile;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.Biome;

import java.util.List;

public abstract class ScanLineStrategy extends DroneAction {
    final FlyAction flyAction;
    final EchoAction echoAction;
    final ScanAction scanAction;

    ScanLineStrategy(Drone drone) {
        super(drone);
        flyAction = new FlyAction(drone);
        scanAction = new ScanAction(drone);
        echoAction = new EchoAction(drone);
    }

    String checkResult() {
        Tile tile = getDrone().getMap().getTile(getDrone().getCoordinates());
        List<Biome> biomes = tile.getPossibleBiomes();

        String res = "";

        if (biomes.stream().anyMatch(biome -> !biome.sameName("OCEAN") && tile.getBiomePercentage(biome) == 100))
            res = flyAction.apply();

        return res;
    }
}
