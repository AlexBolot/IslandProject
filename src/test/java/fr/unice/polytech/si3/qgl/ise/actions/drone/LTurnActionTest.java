package fr.unice.polytech.si3.qgl.ise.actions.drone;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import org.junit.Before;
import org.junit.Test;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW.NORTH;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.BORDER;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;
import static org.junit.Assert.*;

public class LTurnActionTest
{
    private Drone       drone;
    private LTurnAction lTurnAction;
    private JsonFactory jsonFact = new JsonFactory();

    @Before
    public void init ()
    {
        drone = new Drone(new IslandMap(), NORTH);

        drone.getMargins().setGlobal(FRONT, BORDER, 50);
        drone.getMargins().setGlobal(BACK, BORDER, 50);
        drone.getMargins().setGlobal(LEFT, BORDER, 50);
        drone.getMargins().setGlobal(RIGHT, BORDER, 50);

        drone.getMargins().setLocal(FRONT, BORDER, 50);
        drone.getMargins().setLocal(BACK, BORDER, 50);
        drone.getMargins().setLocal(LEFT, BORDER, 50);
        drone.getMargins().setLocal(RIGHT, BORDER, 50);

        lTurnAction = new LTurnAction(drone);
    }

    private String getOrientation (ZQSD direction)
    {
        if (direction == RIGHT) return drone.getOrientation().getToTheRight().getValue();

        if (direction == LEFT) return drone.getOrientation().getToTheLeft().getValue();

        throw new IllegalArgumentException("Gave wrong direction");
    }

    @Test
    public void apply ()
    {
        for (NSEW ori : NSEW.values())
        {
            for (ZQSD dir : new ZQSD[]{RIGHT, LEFT})
            {
                drone.setOrientation(ori);
                drone.setLastEcho(dir);

                String json = jsonFact.createJsonString("heading", "direction", getOrientation(dir));
                String result = lTurnAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("heading", "direction", getOrientation(dir));
                result = lTurnAction.apply();
                assertEquals(json, result);

                json = jsonFact.createJsonString("heading", "direction", getOrientation(getOpposite(dir)));
                result = lTurnAction.apply();
                assertEquals(json, result);

                assertTrue(lTurnAction.isFinished());

                lTurnAction.reset();
            }
        }
    }

    @Test
    public void reset ()
    {
        while (!lTurnAction.isFinished()) lTurnAction.apply(LEFT);

        assertTrue(lTurnAction.isFinished());

        lTurnAction.reset();

        assertFalse(lTurnAction.isFinished());
    }
}