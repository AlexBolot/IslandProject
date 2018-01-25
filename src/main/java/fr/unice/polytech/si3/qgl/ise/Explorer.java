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

public class Explorer implements IExplorerRaid {

    private static Logger logger = getLogger(Explorer.class);
    private Drone drone;
    private IslandMap map;

    @Override
    public void initialize(String contract) {
        logger.debug("Initializing the Explorer");
        logger.trace("Contract: " + contract);
        JSONObject data = new JSONObject(contract);

        NSEW orientation = NSEW.valueOf(data.getString("heading"));
        map = new IslandMap();
        drone = new Drone(map, orientation);
    }

    @Override
    public String takeDecision() {
        logger.info("Taking a decision now");

        return drone.takeDecision();
    }

    @Override
    public void acknowledgeResults(String results) {
        logger.info("Acknowledging results");
        JSONObject data = new JSONObject(results);

        drone.acknowledgeEcho(new Echo(data.toString()));
        drone.acknowledgeScan(new Scan(data.toString()));
    }

    @Override
    public String deliverFinalReport() {
        StringBuilder str = new StringBuilder();

        map.getCreeks().forEach((key, value) -> str.append("CREEKS = ").append(value));
        map.getSites().forEach((key, value) -> str.append("SITE = ").append(value));

        return str.toString();
    }
}
