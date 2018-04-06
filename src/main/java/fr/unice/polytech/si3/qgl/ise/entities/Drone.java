package fr.unice.polytech.si3.qgl.ise.entities;

import fr.unice.polytech.si3.qgl.ise.actions.Action;
import fr.unice.polytech.si3.qgl.ise.actions.StopAction;
import fr.unice.polytech.si3.qgl.ise.actions.drone.DroneInitAction;
import fr.unice.polytech.si3.qgl.ise.actions.drone.ReachIslandAction;
import fr.unice.polytech.si3.qgl.ise.actions.drone.SearchIslandAction;
import fr.unice.polytech.si3.qgl.ise.actions.loop.ScanIslandLoopAction;
import fr.unice.polytech.si3.qgl.ise.enums.Biome;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.SubState;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.map.Tile;
import fr.unice.polytech.si3.qgl.ise.parsing.Echo;
import fr.unice.polytech.si3.qgl.ise.parsing.Scan;
import fr.unice.polytech.si3.qgl.ise.utilities.Margin;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.BORDER;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.GROUND;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.SubState.INIT_ECHO_FRONT;
import static org.apache.logging.log4j.LogManager.getLogger;

public class Drone {
    private static final int MOVEMENT_UNIT = 3;
    private static final Logger logger = getLogger(Drone.class);
    private final SubState subState;
    private final IslandMap map;
    private final Margin margins;
    private NSEW orientation;
    private ZQSD lastTurn;
    private ZQSD lastEcho;
    private DroneEnums.Action lastAction;
    private boolean isFlying;
    private boolean hasFoundIsland;
    private Coordinates coordinates;
    private ArrayList<Action> steps;

    public Drone(IslandMap map, NSEW orientation) {
        this.map = map;
        this.coordinates = new Coordinates(0, 0);
        this.isFlying = true;
        this.subState = INIT_ECHO_FRONT;
        this.orientation = orientation;
        this.margins = new Margin();

        initSteps();
    }

    public static int getMovementUnit() {
        return MOVEMENT_UNIT;
    }

    private void initSteps() {
        steps = new ArrayList<>();

        steps.add(new DroneInitAction(this));
        steps.add(new SearchIslandAction(this));
        steps.add(new ReachIslandAction(this));
        steps.add(new ScanIslandLoopAction(this));
    }

    public String takeDecision() {
        logger.info("--------> start");

        if (!isFlying) return new StopAction(this).apply();

        for (Action step : steps) {
            if (step.isFinished()) continue;

            String res = step.apply();

            if (!res.isEmpty()) return res;
        }

        logger.info("--------> end - " + subState);

        isFlying = false;
        return "";
    }

    public void acknowledgeEcho(Echo echo) {
        Obstacle obstacle = echo.getObstacle();
        Integer range = echo.getRange();

        margins.setLocal(lastEcho, obstacle, range);

        if (obstacle == GROUND) hasFoundIsland = true;
        if (obstacle == BORDER) margins.setGlobal(lastEcho, obstacle, range);
    }

    //region ===== Getters =====

    public void acknowledgeScan(Scan scan) {
        if (!scan.getCreeks().isEmpty()) map.addCreeks(coordinates, scan.getCreeks());
        if (!scan.getEmergencySites().isEmpty()) map.addSite(coordinates, scan.getEmergencySites().get(0));
        if (!scan.getBiomes().isEmpty()) {
            //For each layer
            int numLayer = 0;
            //noinspection LoopStatementThatDoesntLoop
            for (List<Tile> layer : map.getTileToUpdateFrom(coordinates.getX(), coordinates.getY())) {
                //On each tile
                for (Tile tileOfLayer : layer) {
                    //With each biome
                    Map<Biome, Double> toAdd = new EnumMap<>(Biome.class);
                    for (Biome biome : scan.getBiomes()) {
                        toAdd.put(biome, IslandMap.percentageOfLayerForUpdate[numLayer]);
                    }
                    tileOfLayer.addBiomesPercentage(toAdd);
                }
                ++numLayer;
                break;
            }

            Tile currentTile = map.getTile(coordinates);

            currentTile.getBiomesPercentage().clear();

            scan.getBiomes().forEach(biome -> currentTile.getBiomesPercentage().put(biome, 100d));
        }
    }

    public boolean isFlying() {
        return isFlying;
    }

    public void stopFlying() {
        this.isFlying = false;
    }

    public Margin getMargins() {
        return margins;
    }

    public NSEW getOrientation() {
        return orientation;
    }

    public void setOrientation(NSEW orientation) {
        this.orientation = orientation;
    }

    public DroneEnums.Action getLastAction() {
        return lastAction;
    }

    public void setLastAction(DroneEnums.Action lastAction) {
        this.lastAction = lastAction;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public ZQSD getLastTurn() {
        return lastTurn;
    }

    public void setLastTurn(ZQSD lastTurn) {
        this.lastTurn = lastTurn;
    }

    public ZQSD getLastEcho() {
        return lastEcho;
    }

    public void setLastEcho(ZQSD lastEcho) {
        this.lastEcho = lastEcho;
    }

    public boolean hasFoundIsland() {
        return hasFoundIsland;
    }

    public IslandMap getMap() {
        return map;
    }

    //endregion
}