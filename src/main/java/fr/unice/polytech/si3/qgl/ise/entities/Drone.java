package fr.unice.polytech.si3.qgl.ise.entities;

import fr.unice.polytech.si3.qgl.ise.enums.Biome;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.SubState;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.map.Tile;
import fr.unice.polytech.si3.qgl.ise.parsing.Echo;
import fr.unice.polytech.si3.qgl.ise.parsing.Scan;
import org.apache.logging.log4j.Logger;
import scala.Tuple2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.unice.polytech.si3.qgl.ise.enums.Biome.OCEAN;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Action;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW.*;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.BORDER;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.GROUND;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.SubState.*;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;
import static org.apache.logging.log4j.LogManager.getLogger;

public class Drone {
    private static final int movementUnit = 3;
    private NSEW orientation;
    private ZQSD lastTurn;
    private ZQSD lastEcho;
    private Action lastAction;
    private boolean isFlying;
    private boolean hasFoundIsland;
    private SubState subState;
    private IslandMap map;
    private Coordinates coords;
    private HashMap<ZQSD, Tuple2<Obstacle, Integer>> margins;
    private JsonFactory jsonFactory;

    private static Logger logger = getLogger(Drone.class);

    public Drone(IslandMap map, NSEW orientation) {
        this.map = map;
        this.coords = new Coordinates(0, 0);
        this.isFlying = true;
        this.subState = INIT_ECHO_FRONT;
        this.orientation = orientation;
        this.margins = new HashMap<>();
        this.jsonFactory = new JsonFactory();
    }

    private String fly() {

        //region --> switch (orientation) // to change coordinates
        switch (orientation) {
            case EAST:
                coords = new Coordinates(coords.getX() + movementUnit, coords.getY());
                break;

            case WEST:
                coords = new Coordinates(coords.getX() - movementUnit, coords.getY());
                break;

            case NORTH:
                coords = new Coordinates(coords.getX(), coords.getY() + movementUnit);
                break;

            case SOUTH:
                coords = new Coordinates(coords.getX(), coords.getY() - movementUnit);
                break;
        }
        //endregion

        lastAction = Action.Fly;
        return jsonFactory.createJsonString("fly");
    }

    @SuppressWarnings("Duplicates")
    private String heading(NSEW direction) {

        //region --> switch (orientation) // to change coordinates
        int newX;
        int newY;

        switch (orientation) {
            case EAST:
                newX = coords.getX() + movementUnit;

                switch (direction) {
                    case NORTH:
                        coords = new Coordinates(newX, coords.getY() + movementUnit);
                        break;

                    case SOUTH:
                        coords = new Coordinates(newX, coords.getY() - movementUnit);
                        break;
                }

                break;

            case WEST:
                newX = coords.getX() - movementUnit;

                switch (direction) {
                    case NORTH:
                        coords = new Coordinates(newX, coords.getY() + movementUnit);
                        break;

                    case SOUTH:
                        coords = new Coordinates(newX, coords.getY() - movementUnit);
                        break;
                }
                break;

            case NORTH:
                newY = coords.getY() + movementUnit;

                switch (direction) {
                    case EAST:
                        coords = new Coordinates(coords.getX() + movementUnit, newY);
                        break;

                    case WEST:
                        coords = new Coordinates(coords.getX() - movementUnit, newY);
                        break;
                }
                break;

            case SOUTH:
                newY = coords.getY() - movementUnit;

                switch (direction) {
                    case EAST:
                        coords = new Coordinates(coords.getX() + movementUnit, newY);
                        break;

                    case WEST:
                        coords = new Coordinates(coords.getX() - movementUnit, newY);
                        break;
                }
                break;
        }
        //endregion

        orientation = direction;
        lastAction = Action.Heading;
        return jsonFactory.createJsonString("heading", "direction", direction.getValue());
    }

    private String echo(NSEW direction) {
        lastAction = Action.Echo;
        return jsonFactory.createJsonString("echo", "direction", direction.getValue());
    }

    private String scan() {
        lastAction = Action.Scan;
        return jsonFactory.createJsonString("scan");
    }

    private String stop() {

        lastAction = Action.Stop;
        return jsonFactory.createJsonString("stop");
    }

    public String takeDecision() {

        logger.info("--------> start");

        if (!isFlying) return stop();

        switch (subState) {

            //region -> INITIAL
            case INIT_ECHO_FRONT:
                subState = INIT_ECHO_RIGHT;

                lastEcho = FRONT;
                return echo(getOri(lastEcho));

            case INIT_ECHO_RIGHT:
                subState = INIT_ECHO_LEFT;

                lastEcho = RIGHT;
                return echo(getOri(lastEcho));

            case INIT_ECHO_LEFT:
                subState = SEARCH_CHOSE_DIR;

                lastEcho = LEFT;
                return echo(getOri(lastEcho));
            //endregion

            //region -> SEARCH
            case SEARCH_CHOSE_DIR:
                subState = SEARCH_ECHO_SIDE;

                ZQSD dir;

                if (margins.get(FRONT)._1 == GROUND)
                    dir = (margins.get(RIGHT)._2 < margins.get(LEFT)._2) ? RIGHT : LEFT;
                else dir = (margins.get(RIGHT)._2 > margins.get(LEFT)._2) ? RIGHT : LEFT;

                margins.put(FRONT, new Tuple2<>(BORDER, margins.get(dir)._2 - 1));

                lastTurn = dir;
                return heading(getOri(lastTurn));

            case SEARCH_ECHO_SIDE:
                subState = SEARCH_FLY;

                lastEcho = getOpposite(lastTurn);
                return echo(getOri(lastEcho));

            case SEARCH_FLY:

                if (margins.get(lastEcho)._1 == GROUND || !hasFoundIsland) {

                    Integer frontDist = margins.get(FRONT)._2;

                    if (frontDist > 1) {

                        margins.put(FRONT, new Tuple2<>(BORDER, frontDist - 1));
                        subState = SEARCH_ECHO_SIDE;

                        return fly();
                    }
                }

                subState = REACH_ISLAND_TURN1;
                return takeDecision();
            //endregion

            //region -> REACH ISLAND
            case REACH_ISLAND_TURN1:
                subState = REACH_ISLAND_TURN2;

                lastTurn = getOpposite(lastTurn);
                return heading(getOri(lastTurn));

            case REACH_ISLAND_TURN2:
                subState = REACH_ISLAND_TURN3;

                return heading(getOri(lastTurn));

            case REACH_ISLAND_TURN3:
                subState = REACH_ISLAND_ECHO_FRONT;

                lastTurn = getOpposite(lastTurn);
                return heading(getOri(lastTurn));

            case REACH_ISLAND_ECHO_FRONT:
                subState = REACH_ISLAND_MOVE;

                lastEcho = FRONT;
                return echo(getOri(FRONT));

            case REACH_ISLAND_MOVE:

                if (margins.get(FRONT)._1 == GROUND) {

                    Integer frontDist = margins.get(FRONT)._2;

                    if (frontDist >= 0) {

                        margins.put(FRONT, new Tuple2<>(GROUND, frontDist - 1));
                        subState = REACH_ISLAND_MOVE;

                        return fly();
                    }

                    subState = SCAN_STEP_1;
                    return takeDecision();
                }
                break;
            //endregion

            //region -> SCAN
            case SCAN_STEP_1:
                subState = SCAN_STEP_2;

                return scan();

            case SCAN_STEP_2:

                Tile tile = map.getTile(coords);

                List<Biome> biomes = tile.getPossibleBiomes();

                if (biomes.stream().anyMatch(biome -> biome != OCEAN)) {

                    subState = SCAN_STEP_1;
                    margins.put(FRONT, new Tuple2<>(BORDER, margins.get(FRONT)._2 - 1));

                    return fly();
                }

                subState = CHANGELINE_TURN1;
                return takeDecision();
            //endregion

            //region CHANGE LINE
            case CHANGELINE_TURN1:
                subState = CHANGELINE_TURN2;

                lastTurn = getOpposite(lastTurn);
                return heading(getOri(lastTurn));

            case CHANGELINE_TURN2:
                subState = CHANGELINE_ECHO_FRONT;

                return heading(getOri(lastTurn));

            case CHANGELINE_ECHO_FRONT:
                subState = CHANGELINE_DONE;

                lastEcho = FRONT;
                return echo(getOri(lastEcho));

            case CHANGELINE_DONE:

                if (margins.get(FRONT)._1 == GROUND) {

                    subState = REACH_ISLAND_MOVE;
                    return takeDecision();
                }
                //If no creek or site is found -> beginning of the about turn
                else if (map.getCreeks().isEmpty() || map.getEmergencySite() == null) {
                    subState = PASS_ISLAND_STEP_2;
                    return takeDecision();
                }
                break;
            //endregion

            //region -> ABOUT-TURN
            case PASS_ISLAND_STEP_1:

                if (margins.get(lastTurn)._1 == BORDER) {

                    subState = (lastTurn == LEFT) ? ABOUT_TURN_L_1 : ABOUT_TURN_R_1;
                    return takeDecision();
                } else {
                    Integer frontDist = margins.get(FRONT)._2;
                    margins.put(FRONT, new Tuple2<>(BORDER, frontDist - 1));
                    subState = PASS_ISLAND_STEP_2;

                    return fly();
                }

            case PASS_ISLAND_STEP_2:
                subState = PASS_ISLAND_STEP_1;

                lastEcho = lastTurn;
                return echo(getOri(lastEcho));

            case ABOUT_TURN_L_1:
                subState = ABOUT_TURN_L_2;

                lastTurn = RIGHT;
                return heading(getOri(lastTurn));

            case ABOUT_TURN_L_2:
                subState = ABOUT_TURN_L_3;
                Integer frontDist = margins.get(FRONT)._2;
                Obstacle obst = margins.get(FRONT)._1;
                margins.put(FRONT, new Tuple2<>(obst, frontDist - 1));

                return fly();

            case ABOUT_TURN_L_3:
                subState = ABOUT_TURN_L_4;

                lastTurn = LEFT;
                return heading(getOri(lastTurn));

            case ABOUT_TURN_L_4:
                subState = ABOUT_TURN_L_5;

                lastTurn = LEFT;
                return heading(getOri(lastTurn));

            case ABOUT_TURN_L_5:
                subState = ABOUT_TURN_L_6;

                lastTurn = LEFT;
                return heading(getOri(lastTurn));

            case ABOUT_TURN_L_6:
                subState = ABOUT_TURN_L_7;

                lastTurn = RIGHT;
                return heading(getOri(lastTurn));

            case ABOUT_TURN_L_7:
                subState = REACH_ISLAND_ECHO_FRONT;

                lastTurn = LEFT;
                return heading(getOri(lastTurn));

            case ABOUT_TURN_R_1:
                subState = ABOUT_TURN_R_2;

                lastTurn = RIGHT;
                return heading(getOri(lastTurn));

            case ABOUT_TURN_R_2:
                subState = ABOUT_TURN_R_3;

                lastTurn = LEFT;
                return heading(getOri(lastTurn));

            case ABOUT_TURN_R_3:
                subState = ABOUT_TURN_R_4;

                lastTurn = LEFT;
                return heading(getOri(lastTurn));

            case ABOUT_TURN_R_4:
                subState = ABOUT_TURN_R_5;
                Integer frontDist2 = margins.get(FRONT)._2;
                Obstacle obst2 = margins.get(FRONT)._1;
                margins.put(FRONT, new Tuple2<>(obst2, frontDist2 - 1));

                return fly();

            case ABOUT_TURN_R_5:
                subState = ABOUT_TURN_R_6;

                lastTurn = LEFT;
                return heading(getOri(lastTurn));

            case ABOUT_TURN_R_6:
                subState = ABOUT_TURN_R_7;

                lastTurn = LEFT;
                return heading(getOri(lastTurn));

            case ABOUT_TURN_R_7:
                subState = REACH_ISLAND_ECHO_FRONT;

                lastTurn = RIGHT;
                return heading(getOri(lastTurn));
            //endregion
        }

        logger.info("--------> end - " + subState);

        isFlying = false;

        return stop();
    }

    public void acknowledgeEcho(Echo echo) {

        Obstacle obstacle = echo.getObstacle();
        Integer range = echo.getRange();

        if (obstacle == GROUND) hasFoundIsland = true;

        margins.put(lastEcho, new Tuple2<>(obstacle, range));
    }

    public void acknowledgeScan(Scan scan) {
        Tile tile = new Tile();
        if (!scan.getCreeks().isEmpty()) map.addCreek(coords, scan.getCreeks().get(0));
        if (!scan.getEmergencySites().isEmpty()) map.addSite(coords, scan.getEmergencySites().get(0));
        if (!scan.getBiomes().isEmpty()) {
            //For each layer
            int numLayer = 0;
            for (List<Tile> layer : map.getTileToUpdateFrom(coords.getX(), coords.getY())) {
                //On each tile
                for (Tile tileOfLayer : layer) {
                    //With each biome
                    Map<Biome, Double> toAdd = new HashMap<>();
                    for (Biome biome : scan.getBiomes()) {
                        toAdd.put(biome, IslandMap.percentageOfLayerForUpdate[numLayer]);
                    }
                    tileOfLayer.addBiomesPercentage(toAdd);
                }
                ++numLayer;
            }
        }
    }

    //region ===== Getters =====

    public boolean isFlying() {
        return isFlying;
    }

    public HashMap<ZQSD, Tuple2<Obstacle, Integer>> getMargins() {
        return margins;
    }

    @SuppressWarnings("Duplicates")
    private NSEW getOri(ZQSD direction) {
        switch (orientation) {
            case EAST:
                switch (direction) {
                    case LEFT:
                        return NORTH;
                    case RIGHT:
                        return SOUTH;
                    case FRONT:
                        return EAST;
                }
                break;
            case WEST:
                switch (direction) {
                    case LEFT:
                        return SOUTH;
                    case RIGHT:
                        return NORTH;
                    case FRONT:
                        return WEST;
                }
                break;
            case NORTH:
                switch (direction) {
                    case LEFT:
                        return WEST;
                    case RIGHT:
                        return EAST;
                    case FRONT:
                        return NORTH;
                }
                break;
            case SOUTH:
                switch (direction) {
                    case LEFT:
                        return EAST;
                    case RIGHT:
                        return WEST;
                    case FRONT:
                        return SOUTH;
                }
        }

        throw new IllegalArgumentException("Wrong direction given !");
    }

    private ZQSD getOpposite(ZQSD dir) {
        switch (dir) {
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            case FRONT:
                return BACK;
            case BACK:
                return FRONT;
        }

        throw new IllegalArgumentException("Wrong params");
    }

    public Action getLastAction() {
        return lastAction;
    }
    //endregion
}