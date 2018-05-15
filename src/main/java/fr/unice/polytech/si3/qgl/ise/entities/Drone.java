package fr.unice.polytech.si3.qgl.ise.entities;

import fr.unice.polytech.si3.qgl.ise.actions.Action;
import fr.unice.polytech.si3.qgl.ise.actions.StopAction;
import fr.unice.polytech.si3.qgl.ise.actions.drone.DroneInitAction;
import fr.unice.polytech.si3.qgl.ise.actions.drone.ReachIslandAction;
import fr.unice.polytech.si3.qgl.ise.actions.drone.ScanLineActionStraight;
import fr.unice.polytech.si3.qgl.ise.actions.drone.SearchIslandAction;
import fr.unice.polytech.si3.qgl.ise.actions.loop.ScanIslandLoopAction;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.map.Tile;
import fr.unice.polytech.si3.qgl.ise.parsing.Echo;
import fr.unice.polytech.si3.qgl.ise.parsing.Scan;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.Biome;
import fr.unice.polytech.si3.qgl.ise.utilities.Margin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.BORDER;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.GROUND;

/**
 * Drone entitity, coordinates actions in {@link fr.unice.polytech.si3.qgl.ise.actions.drone}
 * and in {@link fr.unice.polytech.si3.qgl.ise.actions.loop}
 */
public class Drone {
    private static final int MOVEMENT_UNIT = 3;
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
        this.orientation = orientation;
        this.margins = new Margin();

        initSteps();
    }

    /**
     * @return the number of case the drone flies before scan
     */
    public static int getMovementUnit() {
        return MOVEMENT_UNIT;
    }

    /**
     * Init the drone, init the steps that the drone will have to perform to do the job
     */
    private void initSteps() {
        steps = new ArrayList<>();

        steps.add(new DroneInitAction(this));
        steps.add(new SearchIslandAction(this));
        steps.add(new ReachIslandAction(this));
        steps.add(new ScanIslandLoopAction(this, new ScanLineActionStraight(this, 1)));
    }

    /**
     * Main method, execute the current action
     *
     * @return a String JSON formatted doing the right action
     */
    public String takeDecision() {

        if (!isFlying) return new StopAction(this).apply();

        for (Action step : steps) {
            if (step.isFinished()) continue;

            String res = step.apply();

            if (!res.isEmpty()) return res;
        }

        isFlying = false;
        return "";
    }

    /**
     * @param echo Echo action that has been done earlier
     * @see Echo
     */
    public void acknowledgeEcho(Echo echo) {
        Obstacle obstacle = echo.getObstacle();
        Integer range = echo.getRange();

        margins.setLocal(lastEcho, obstacle, range);

        if (obstacle == GROUND) hasFoundIsland = true;
        if (obstacle == BORDER) margins.setGlobal(lastEcho, obstacle, range);
    }

    /**
     * @param scan Scan action that has been done earlier
     * @see Scan
     */
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
                    Map<Biome, Double> toAdd = new HashMap<>();
                    for (Biome biome : scan.getBiomes()) {
                        toAdd.put(biome, IslandMap.getPercentageOfLayerForUpdate()[numLayer]);
                    }
                    tileOfLayer.addBiomesPercentage(toAdd);
                }
                ++numLayer;
            }
        }
    }

    //region ========= Getters & Setters ========

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