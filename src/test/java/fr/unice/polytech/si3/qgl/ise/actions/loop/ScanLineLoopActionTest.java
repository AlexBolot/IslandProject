package fr.unice.polytech.si3.qgl.ise.actions.loop;

import fr.unice.polytech.si3.qgl.ise.actions.drone.ChangeLineAction;
import fr.unice.polytech.si3.qgl.ise.actions.drone.ScanLineAction;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import org.junit.Before;
import org.junit.Test;

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
    private JsonFactory jsonFact = new JsonFactory();

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

                    while (!scanLineAction.isFinished()) {
                        assertEquals(scanLineAction.apply(), scanLineLoopAction.apply());
                    }

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
}