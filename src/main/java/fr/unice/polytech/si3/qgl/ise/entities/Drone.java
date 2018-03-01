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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.BORDER;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.GROUND;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.SubState.INIT_ECHO_FRONT;
import static org.apache.logging.log4j.LogManager.getLogger;

public class Drone
{
    private static final int movementUnit = 3;
    private NSEW                                     orientation;
    private ZQSD                                     lastTurn;
    private ZQSD                                     lastEcho;
    private DroneEnums.Action                        lastAction;
    private boolean                                  isFlying;
    private boolean                                  hasFoundIsland;
    private SubState                                 subState;
    private IslandMap                                map;
    private Coordinates                              coords;
    private ArrayList<Action>                        steps;
    private Margin margins;

    private static Logger logger = getLogger(Drone.class);

    public Drone (IslandMap map, NSEW orientation)
    {
        this.map = map;
        this.coords = new Coordinates(0, 0);
        this.isFlying = true;
        this.subState = INIT_ECHO_FRONT;
        this.orientation = orientation;
        this.margins = new Margin();

        initSteps();
    }

    private void initSteps ()
    {
        steps = new ArrayList<>();

        steps.add(new DroneInitAction(this));
        steps.add(new SearchIslandAction(this));
        steps.add(new ReachIslandAction(this));
        steps.add(new ScanIslandLoopAction(this));
    }

    public String takeDecision ()
    {
        logger.info("--------> start");

        if (!isFlying) return StopAction.get();

        for (Action step : steps)
        {
            if (step.isFinished()) continue;

            String res = step.apply();

            if (!res.isEmpty()) return res;
        }

        logger.info("--------> end - " + subState);

        isFlying = false;

        return StopAction.get();
    }

    public void acknowledgeEcho (Echo echo)
    {
        Obstacle obstacle = echo.getObstacle();
        Integer range = echo.getRange();

        margins.setLocal(lastEcho, obstacle, range);

        if (obstacle == GROUND) hasFoundIsland = true;
        if (obstacle == BORDER) margins.setGlobal(lastEcho, obstacle, range);
    }

    public void acknowledgeScan (Scan scan)
    {
        Tile tile = new Tile();
        if (!scan.getCreeks().isEmpty()) map.addCreek(coords, scan.getCreeks().get(0));
        if (!scan.getEmergencySites().isEmpty()) map.addSite(coords, scan.getEmergencySites().get(0));
        if (!scan.getBiomes().isEmpty())
        {
            //For each layer
            int numLayer = 0;
            for (List<Tile> layer : map.getTileToUpdateFrom(coords.getX(), coords.getY()))
            {
                //On each tile
                for (Tile tileOfLayer : layer)
                {
                    //With each biome
                    Map<Biome, Double> toAdd = new HashMap<>();
                    for (Biome biome : scan.getBiomes())
                    {
                        toAdd.put(biome, IslandMap.percentageOfLayerForUpdate[numLayer]);
                    }
                    tileOfLayer.addBiomesPercentage(toAdd);
                }
                ++numLayer;
            }
        }
    }

    //region ===== Getters =====

    public boolean isFlying ()
    {
        return isFlying;
    }

    public Margin getMargins()
    {
        return margins;
    }

    public static int getMovementUnit ()
    {
        return movementUnit;
    }

    public NSEW getOrientation ()
    {
        return orientation;
    }

    public DroneEnums.Action getLastAction ()
    {
        return lastAction;
    }

    public void setOrientation (NSEW orientation)
    {
        this.orientation = orientation;
    }

    public void setLastTurn (ZQSD lastTurn)
    {
        this.lastTurn = lastTurn;
    }

    public void setLastEcho (ZQSD lastEcho)
    {
        this.lastEcho = lastEcho;
    }

    public void setLastAction (DroneEnums.Action lastAction)
    {
        this.lastAction = lastAction;
    }

    public Coordinates getCoords ()
    {
        return coords;
    }

    public void setCoords (Coordinates coords)
    {
        this.coords = coords;
    }

    public ZQSD getLastTurn ()
    {
        return lastTurn;
    }

    public ZQSD getLastEcho ()
    {
        return lastEcho;
    }

    public boolean hasFoundIsland ()
    {
        return hasFoundIsland;
    }

    public IslandMap getMap ()
    {
        return map;
    }

    //endregion
}