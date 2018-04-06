package fr.unice.polytech.si3.qgl.ise.actions.loop;

import fr.unice.polytech.si3.qgl.ise.actions.drone.GTurnAction;
import fr.unice.polytech.si3.qgl.ise.actions.drone.PassIslandAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.parsing.Echo;
import fr.unice.polytech.si3.qgl.ise.parsing.Scan;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.TestingUtils.setMargins;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Action.ECHO;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW.NORTH;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;
import static org.junit.Assert.assertEquals;

@Ignore
public class ScanIslandLoopActionTest {
    private Drone drone;
    private Drone drone2;
    private ScanIslandLoopAction scanIslandLoopAction;
    private GTurnAction gTurnAction;
    private ScanLineLoopAction scanLineLoopAction;
    private PassIslandAction passIslandAction;

    @Before
    public void setUp() {
        drone = new Drone(new IslandMap(), NORTH);
        setMargins(drone, 50);
        scanIslandLoopAction = new ScanIslandLoopAction(drone);

        drone2 = new Drone(new IslandMap(), NORTH);
        setMargins(drone2, 50);
        gTurnAction = new GTurnAction(drone2);
        scanLineLoopAction = new ScanLineLoopAction(drone2);
        passIslandAction = new PassIslandAction(drone2);
    }

    @Test
    @Ignore
    public void applyFirstShot() {
        for (DroneEnums.NSEW ori : DroneEnums.NSEW.values()) {
            for (DroneEnums.ZQSD dir : new DroneEnums.ZQSD[]{LEFT, RIGHT}) {
                drone.setOrientation(ori);
                drone.setLastEcho(dir);
                drone.setLastTurn(getOpposite(dir));

                drone2.setOrientation(ori);
                drone2.setLastEcho(dir);
                drone2.setLastTurn(getOpposite(dir));

                Context context = new Context();

                while (!scanLineLoopAction.isFinished()) {
                    String applyRes = scanLineLoopAction.apply();
                    if (applyRes.isEmpty()) break;
                    assertEquals(applyRes, scanIslandLoopAction.apply());
                    acknowledgeScanLine(context);
                }

                context.resetWidth();

                while (!passIslandAction.isFinished())
                {
                    String applyRes = passIslandAction.apply();
                    if(applyRes.isEmpty()) break;
                    String apply = scanIslandLoopAction.apply();
                    assertEquals(applyRes, scanIslandLoopAction.apply());
                    acknowledgePassIsland(context);
                }

                setUp();

                break;
            }
            break;
        }
    }

    private void acknowledgeScanLine(Context context) {
        switch (drone.getLastAction()) {
            case SCAN:
                if (context.islandWidth >= 0) {
                    if (!context.creekFound) {
                        scanCreek(drone, drone2);
                        context.creekFound = true;
                    } else scanGround(drone, drone2);
                } else {
                    scanOcean(drone, drone2);
                }
                break;
            case FLY:
                context.islandWidth--;
                break;
            case ECHO:
                if (context.echoCount % 3 != 2 || context.islandHeight <= 0) echoBorder(drone, drone2);
                else echoGround(drone, drone2);
                context.echoCount++;
                break;
            case HEADING:
                context.resetWidth();
                context.islandHeight--;
        }
    }

    private void acknowledgePassIsland(Context context) {
        if(drone.getLastAction() == ECHO)
        {
            if(context.islandWidth >=0)
            {
                echoGround(drone, drone2);
                context.islandWidth--;
            }
            else
            {
                echoBorder(drone, drone2);
            }
        }
    }

    private void echoBorder(Drone... drones) {
        Arrays.stream(drones).forEach(drone -> drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 40, \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }")));
    }

    private void echoGround(Drone... drones) {
        Arrays.stream(drones).forEach(drone -> drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 1, \"found\": \"GROUND\" }, \"status\": \"OK\" }")));
    }

    private void scanOcean(Drone... drones) {
        Arrays.stream(drones).forEach(drone -> drone.acknowledgeScan(new Scan("{\"cost\": 2, \"extras\": { \"biomes\": [ \"OCEAN\" ], \"creeks\": [], \"sites\": []}, \"status\": \"OK\"}")));
    }

    private void scanGround(Drone... drones) {
        Arrays.stream(drones).forEach(drone -> drone.acknowledgeScan(new Scan("{\"cost\": 2, \"extras\": { \"biomes\": [ \"SHRUBLAND\", \"ALPINE\" ], \"creeks\": [], \"sites\": []}, \"status\": \"OK\"}")));
    }

    private void scanCreek(Drone... drones) {
        Arrays.stream(drones).forEach(drone -> drone.acknowledgeScan(new Scan("{\"cost\": 2, \"extras\": { \"biomes\": [ \"SHRUBLAND\", \"ALPINE\" ], \"creeks\": [\"you-can-land-here\"], \"sites\": []}, \"status\": \"OK\"}")));
    }

    private class Context {
        int echoCount = 0;
        int islandWidth;
        int islandHeight;
        boolean creekFound = false;

        Context() {
            this.resetHeight();
            this.resetWidth();
        }

        void resetHeight() {
            islandHeight = new Random().nextInt(20) + 6;
        }

        void resetWidth() {
            islandWidth = new Random().nextInt(5) + 5;
        }

        @Override
        public String toString() {
            return "Context{echoCount=" + echoCount + ", islandWidth=" + islandWidth + ", islandHeight=" + islandHeight + ", creekFound=" + creekFound + '}';
        }
    }
}