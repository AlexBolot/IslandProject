package fr.unice.polytech.si3.qgl.ise.actions;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Action.Stop;

public class StopAction extends Action
{
    private Drone drone;

    public StopAction (Drone drone)
    {
        this.drone = drone;
    }

    public StopAction ()
    {

    }

    protected Drone getDrone ()
    {
        return drone;
    }

    @Override
    public String apply ()
    {
        if (drone != null) drone.setLastAction(Stop);
        return new JsonFactory().createJsonString("stop");
    }
}
