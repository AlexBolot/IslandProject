package fr.unice.polytech.si3.qgl.ise;

import eu.ace_design.island.bot.IExplorerRaid;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.parsing.Echo;
import fr.unice.polytech.si3.qgl.ise.parsing.Scan;
import fr.unice.polytech.si3.qgl.ise.utilities.PathFinder;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;
import static org.apache.logging.log4j.LogManager.getLogger;

public class Explorer implements IExplorerRaid {
    private static Logger logger = getLogger(Explorer.class);
    private Drone drone;
    private IslandMap map;
    private int remainingBudget;

    @Override
    public void initialize(String contract) {
        logger.debug("Initializing the Explorer");
        logger.trace("Contract: " + contract);
        JSONObject data = new JSONObject(contract);

        String heading = data.getString("heading");
        remainingBudget = data.getInt("budget");

        NSEW orientation = NSEW.getFromValue(heading);
        map = new IslandMap();
        drone = new Drone(map, orientation);
    }

    @Override
    public String takeDecision() {
        try {
            String s = drone.takeDecision();
            logger.info("Decision :\t" + s);
            return s;
        } catch (Exception e) {
            logger.info(e.getMessage());

            JSONObject data = new JSONObject();
            data.put("action", "stop");
            return data.toString();
        }

    }

    @Override
    public void acknowledgeResults(String results) {
        try {
            JSONObject data = new JSONObject(results);

            logger.info("Réponse :\t" + data);

            remainingBudget -= data.getInt("cost");

            switch (drone.getLastAction()) {
                case Scan:
                    drone.acknowledgeScan(new Scan(data.toString()));
                    break;
                case Echo:
                    drone.acknowledgeEcho(new Echo(data.toString()));
                    break;
            }
        } catch (Exception e) {
            logger.info("Something failed while processing the results");
        }
    }

    @Override
    public String deliverFinalReport() {
        StringBuilder str = new StringBuilder();

        str.append("CREEKS = ");
        map.getCreeks().forEach((key, value) -> str.append(value).append(", "));

        str.append("SITE = ");
        if (map.getEmergencySite() != null) str.append(map.getEmergencySite()._2);
        else str.append("NOT FOUND");

        str.append(System.getProperty("line.separator"));

        if (map.getEmergencySite() != null)
            str.append("Nearest creek to emergency site : ").append(PathFinder.findNearestCreek(map.getCreeks(), map.getEmergencySite()));
        else str.append("Emergency site not found");

        logger.info(str.toString());

        return str.toString();
    }
}
