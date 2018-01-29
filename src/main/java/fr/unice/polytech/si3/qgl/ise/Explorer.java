package fr.unice.polytech.si3.qgl.ise;

import eu.ace_design.island.bot.IExplorerRaid;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.parsing.Echo;
import fr.unice.polytech.si3.qgl.ise.parsing.Scan;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;
import static org.apache.logging.log4j.LogManager.getLogger;

public class Explorer implements IExplorerRaid
{
    private static Logger logger = getLogger(Explorer.class);
    private Drone     drone;
    private IslandMap map;

    @Override
    public void initialize (String contract)
    {
        logger.debug("Initializing the Explorer");
        logger.trace("Contract: " + contract);
        JSONObject data = new JSONObject(contract);

        String heading = data.getString("heading");

        NSEW orientation = NSEW.getFromValue(heading);
        map = new IslandMap();
        drone = new Drone(map, orientation);
    }

    @Override
    public String takeDecision ()
    {
        String s = drone.takeDecision();

        logger.info("Decision :\t" + s);

        return s;
    }

    @Override
    public void acknowledgeResults (String results)
    {
        JSONObject data = new JSONObject(results);

        logger.info("Réponse :\t" + data);

        switch (drone.getLastAction())
        {
            case Scan:
                drone.acknowledgeScan(new Scan(data.toString()));
                break;
            case Echo:
                drone.acknowledgeEcho(new Echo(data.toString()));
                break;
        }
    }

    @Override
    public String deliverFinalReport ()
    {
        StringBuilder str = new StringBuilder();

        str.append("CREEKS = \n");
        map.getCreeks().forEach((key, value) -> str.append(value).append("\n"));

        str.append("SITE = \n");
        map.getSites().forEach((key, value) -> str.append(value).append("\n"));

        return str.toString();
    }
}
