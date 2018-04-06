package fr.unice.polytech.si3.qgl.ise.actions.loop;

import fr.unice.polytech.si3.qgl.ise.actions.drone.ChangeLineAction;
import fr.unice.polytech.si3.qgl.ise.actions.drone.ScanLineAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.parsing.Echo;
import fr.unice.polytech.si3.qgl.ise.parsing.Scan;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static fr.unice.polytech.si3.qgl.ise.actions.drone.TestingUtils.setMargins;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW.NORTH;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ScanLineLoopActionTest {
    private Drone drone;
    private Drone drone2;
    private ScanLineLoopAction scanLineLoopAction;
    private ScanLineAction scanLineAction;
    private ChangeLineAction changeLineAction;

    private int islandWidth;

    @Before
    public void setUp() {
        drone = new Drone(new IslandMap(), NORTH);
        setMargins(drone, 50);
        scanLineLoopAction = new ScanLineLoopAction(drone);

        drone2 = new Drone(new IslandMap(), NORTH);
        setMargins(drone2, 50);
        scanLineAction = new ScanLineAction(drone2);
        changeLineAction = new ChangeLineAction(drone2);
    }

    @Test
    public void apply() {
        for (DroneEnums.NSEW ori : DroneEnums.NSEW.values()) {
            for (DroneEnums.ZQSD dir : new DroneEnums.ZQSD[]{LEFT, RIGHT}) {
                drone.setOrientation(ori);
                drone.setLastEcho(dir);
                drone.setLastTurn(getOpposite(dir));

                drone2.setOrientation(ori);
                drone2.setLastEcho(dir);
                drone2.setLastTurn(getOpposite(dir));

                while (!scanLineLoopAction.isFinished()) {
                    islandWidth = new Random().nextInt(5) + 5;

                    while (!scanLineAction.isFinished()) {
                        assertEquals(scanLineAction.apply(), scanLineLoopAction.apply());
                        acknowledgeAsNeeded();
                    }

                    assertTrue(scanLineAction.isFinished());

                    while (!changeLineAction.isFinished()) {
                        assertEquals(changeLineAction.apply(), scanLineLoopAction.apply());
                    }

                    scanLineAction.reset();
                    changeLineAction.reset();
                }

                assertTrue(scanLineLoopAction.isFinished());

                setUp();
            }
        }
    }

    private void acknowledgeAsNeeded() {
        switch (drone.getLastAction()) {
            case SCAN:
                if (islandWidth >= 0) {
                    scanGround(drone, drone2);
                } else {
                    scanOcean(drone, drone2);
                }
                break;
            case FLY:
                islandWidth--;
                break;
            case ECHO:
                echoBorder(drone, drone2);
                break;
        }
    }

    private void echoBorder(Drone... drones) {
        Arrays.stream(drones).forEach(drone -> drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": 40, \"found\": \"OUT_OF_RANGE\" }, \"status\": \"OK\" }")));
    }

    private void scanOcean(Drone... drones) {
        Arrays.stream(drones).forEach(drone -> drone.acknowledgeScan(new Scan("{\"cost\": 2, \"extras\": { \"biomes\": [ \"OCEAN\" ], \"creeks\": [], \"sites\": []}, \"status\": \"OK\"}")));
    }

    private void scanGround(Drone... drones) {
        Arrays.stream(drones).forEach(drone -> drone.acknowledgeScan(new Scan("{\"cost\": 2, \"extras\": { \"biomes\": [ \"SHRUBLAND\", \"ALPINE\" ], \"creeks\": [], \"sites\": []}, \"status\": \"OK\"}")));
    }
}