package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.parsing.Echo;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW.NORTH;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.BORDER;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReachIslandActionTest
{
    private Drone             drone;
    private Drone             drone2;
    private ReachIslandAction reachIslandAction;
    private LTurnAction       lTurnAction;
    private JsonFactory jsonFact = new JsonFactory();

    @Before
    public void init ()
    {
        drone = new Drone(new IslandMap(), NORTH);
        drone2 = new Drone(new IslandMap(), NORTH);

        setMargins(drone);
        setMargins(drone2);

        reachIslandAction = new ReachIslandAction(drone);
        lTurnAction = new LTurnAction(drone2);
    }

    private void setMargins (Drone drone)
    {
        drone.getMargins().setGlobal(FRONT, BORDER, 50);
        drone.getMargins().setGlobal(BACK, BORDER, 50);
        drone.getMargins().setGlobal(LEFT, BORDER, 50);
        drone.getMargins().setGlobal(RIGHT, BORDER, 50);

        drone.getMargins().setLocal(FRONT, BORDER, 50);
        drone.getMargins().setLocal(BACK, BORDER, 50);
        drone.getMargins().setLocal(LEFT, BORDER, 50);
        drone.getMargins().setLocal(RIGHT, BORDER, 50);
    }

    @Test
    public void apply ()
    {
        for (NSEW ori : NSEW.values())
        {
            for (ZQSD dir : new ZQSD[]{LEFT, RIGHT})
            {
                drone.setOrientation(ori);
                drone.setLastEcho(dir);
                drone.setLastTurn(getOpposite(dir));

                drone2.setOrientation(ori);
                drone2.setLastEcho(dir);
                drone2.setLastTurn(getOpposite(dir));

                while (!lTurnAction.isFinished())
                {
                    assertEquals(lTurnAction.apply(), reachIslandAction.apply());
                }

                String json = jsonFact.createJsonString("echo", "direction", ori.getToThe(dir).getValue());
                String result = reachIslandAction.apply();
                assertEquals(json, result);

                int i = new Random().nextInt(2) + 1;

                drone.acknowledgeEcho(new Echo("{ \"cost\": 1, \"extras\": { \"range\": " + i + ", \"found\": \"GROUND\" }, \"status\": \"OK\" }"));

                for (int j = 0; j <= i; j++)
                {
                    json = jsonFact.createJsonString("fly");
                    result = reachIslandAction.apply();
                    assertEquals(json, result);
                }

                assertTrue(reachIslandAction.apply().isEmpty());
                assertTrue(reachIslandAction.isFinished());

                reachIslandAction.reset();
                lTurnAction.reset();
            }
        }
    }
}