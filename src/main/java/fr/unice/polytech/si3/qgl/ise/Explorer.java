package fr.unice.polytech.si3.qgl.ise;

import eu.ace_design.island.bot.IExplorerRaid;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.maps.DroneMap;
import fr.unice.polytech.si3.qgl.ise.parsing.Echo;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;
import static org.apache.logging.log4j.LogManager.getLogger;

public class Explorer implements IExplorerRaid {

    private static Logger logger = getLogger(Explorer.class);
    private Drone drone;
    private DroneMap droneMap;

    @Override
    public void initialize(String contract) {
        logger.debug("Initializing the Explorer");
        logger.trace("Contract: " + contract);
        JSONObject data = new JSONObject(contract);

        NSEW orientation = NSEW.valueOf(data.getString("heading"));
        drone = new Drone(droneMap,orientation);
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

        drone.acknowledgeResults(new Echo(data.toString()));
    }

    @Override
    public String deliverFinalReport() {
        String report = "EMERGENCY: ???\nCREEK: ???";
        return report;
    }
}
