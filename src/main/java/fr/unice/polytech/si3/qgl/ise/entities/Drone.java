package fr.unice.polytech.si3.qgl.ise.entities;

import fr.unice.polytech.si3.qgl.ise.enums.Biome;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.SubState;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.map.Tile;
import fr.unice.polytech.si3.qgl.ise.parsing.Echo;
import fr.unice.polytech.si3.qgl.ise.parsing.Scan;
import org.json.JSONObject;
import scala.Tuple2;

import java.util.HashMap;
import java.util.List;

import static fr.unice.polytech.si3.qgl.ise.enums.Biome.OCEAN;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW.*;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.BORDER;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.GROUND;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.SubState.*;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;

public class Drone {
    private static final int movementUnit = 3;
    private NSEW orientation;
    private ZQSD lastTurn;
    private ZQSD lastEcho;
    private boolean isFlying;
    private IslandMap map;
    private Coordinates coords;
    private SubState subState;
    private HashMap<ZQSD, Tuple2<Obstacle, Integer>> margins;

    public Drone(IslandMap map, NSEW orientation) {
        this.map = map;
        this.coords = new Coordinates(0, 0);
        this.isFlying = true;
        this.subState = INIT_ECHO_FRONT;
        this.orientation = orientation;
    }

    /**
     * Create a JSON formatted string doing action with parameters
     *
     * @param functionName      action name
     * @param parameterAndValue name of parameter then value
     * @return String matching the action requested
     */
    private String createFunctionWithParams(String functionName, String... parameterAndValue) {
        if (parameterAndValue.length % 2 != 0)
            throw new IllegalArgumentException("The parameters name and value must be in same number");
        JSONObject jsonReturn = new JSONObject();
        jsonReturn.put("action", functionName);
        if (parameterAndValue.length > 0) {
            JSONObject params = new JSONObject();
            for (int i = 0; i < parameterAndValue.length - 1; i += 2) {
                params.put(parameterAndValue[i], parameterAndValue[i + 1]);
            }
            jsonReturn.put("parameters", parameterAndValue);
        }
        return jsonReturn.toString();
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

        return createFunctionWithParams("fly");
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

        return createFunctionWithParams("heading", "direction", direction.getValue());
    }

    private String echo(NSEW direction) {
        return createFunctionWithParams("echo", "direction", direction.getValue());
    }

    private String scan() {
        return createFunctionWithParams("scan");
    }

    private String stop() {
        return createFunctionWithParams("action", "stop");
    }

    public String takeDecision() {
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

                if (margins.get(getOpposite(lastTurn))._1 == GROUND) {
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

                return echo(getOri(FRONT));

            case REACH_ISLAND_MOVE:

                if (margins.get(getOpposite(lastTurn))._1 == GROUND) {
                    Integer frontDist = margins.get(FRONT)._2;

                    if (frontDist > 0) {
                        margins.put(FRONT, new Tuple2<>(BORDER, frontDist - 1));
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

                List<Biome> biomes = map.getTile(coords).getPossibleBiomes();

                if (biomes.stream().anyMatch(biome -> biome != OCEAN)) {
                    subState = SCAN_STEP_1;
                    margins.put(FRONT, new Tuple2<>(BORDER, margins.get(FRONT)._2 - 1));

                    return fly();
                }

                subState = CHANGELINE_TURN1;
                return takeDecision();

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
                break;
        }
        return stop();
    }

    public void acknowledgeEcho(Echo echo) {
        Obstacle obstacle = echo.getObstacle();
        Integer range = echo.getRange();

        margins.put(lastEcho, new Tuple2<>(obstacle, range));
    }

    public void acknowledgeScan(Scan scan) {
        Tile tile = new Tile();

        if (!scan.getCreeks().isEmpty()) map.addCreek(coords, scan.getCreeks().get(0));
        if (!scan.getEmergencySites().isEmpty()) map.addSite(coords, scan.getCreeks().get(0));

        map.addTile(coords, tile);
    }

    //region ===== Getters =====

    public boolean isFlying() {
        return isFlying;
    }

    public HashMap<ZQSD, Tuple2<Obstacle, Integer>> getMargins() {
        return margins;
    }

    public void setLastEcho(ZQSD lastEcho) {
        this.lastEcho = lastEcho;
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
    //endregion
}