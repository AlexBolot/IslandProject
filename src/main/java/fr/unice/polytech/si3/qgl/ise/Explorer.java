package fr.unice.polytech.si3.qgl.ise;

import eu.ace_design.island.bot.IExplorerRaid;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.maps.DroneMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;

public class Explorer implements IExplorerRaid {

    private static Logger logger = LogManager.getLogger(Explorer.class);
    private Drone drone;
    private DroneMap droneMap;

    @Override
    public void initialize(String contract) {
        logger.debug("Initializing the Explorer");
        logger.trace("Contract: " + contract);
        JSONObject data = new JSONObject(contract);
        // process data here ...

        NSEW orientation = NSEW.valueOf(data.getString("heading"));
        drone = new Drone(droneMap,orientation);
    }

    @Override
    public String takeDecision() {
        logger.info("Taking a decision now");
        JSONObject decision = new JSONObject().put("action", "stop");
        return decision.toString();
    }

    @Override
    public void acknowledgeResults(String results) {
        logger.info("Acknowledging results");
        JSONObject data = new JSONObject(results);
        // process data here ...
    }

    @Override
    public String deliverFinalReport() {
        String report = "EMERGENCY: ???\nCREEK: ???";
        return report;
    }
}
