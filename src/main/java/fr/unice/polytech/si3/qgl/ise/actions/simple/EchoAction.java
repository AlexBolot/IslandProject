package fr.unice.polytech.si3.qgl.ise.actions.simple;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Action.Echo;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;

public class EchoAction extends SimpleAction
{
    public EchoAction (Drone drone)
    {
        super(drone);
    }

    @Override
    public String apply ()
    {
        return apply(getDrone().getLastEcho());
    }

    public String apply (ZQSD direction)
    {
        String res;

        switch (direction)
        {
            case FRONT:
                res = echo(getDrone().getOrientation());
                break;

            case LEFT:
                res = echo(getDrone().getOrientation().getToTheLeft());
                break;

            case RIGHT:
                res = echo(getDrone().getOrientation().getToTheRight());
                break;

            default:
                throw new IllegalArgumentException("Wrong direction to head to !");
        }

        getDrone().setLastAction(Echo);
        getDrone().setLastEcho(direction);

        return res;
    }

    private String echo (NSEW orientation)
    {
        return new JsonFactory().createJsonString("echo", "direction", orientation.getValue());
    }
}