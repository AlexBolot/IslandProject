package fr.unice.polytech.si3.qgl.ise;

import eu.ace_design.island.bot.IExplorerRaid;
import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.map.PathFinder;
import fr.unice.polytech.si3.qgl.ise.parsing.ContractParser;
import fr.unice.polytech.si3.qgl.ise.parsing.Echo;
import fr.unice.polytech.si3.qgl.ise.parsing.Scan;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import javax.swing.*;
import java.util.List;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;
import static org.apache.logging.log4j.LogManager.getLogger;

public class Explorer implements IExplorerRaid {
    private static Logger logger = getLogger(Explorer.class);
    private Drone drone;
    private IslandMap map;
    private int remainingBudget;
    private List<RawContract> rawConrtacts;
    private List<CraftedContract> craftedContracts;
    private int crewNumber;
    private Crew crew;

    @Override
    public void initialize(String contract) {
        logger.debug("Initializing the Explorer");
        logger.trace("Contract: " + contract);
        JSONObject data = new JSONObject(contract);

        ContractParser contractParser = new ContractParser(contract);

        String heading = contractParser.getHeading();
        remainingBudget = contractParser.getBudget();
        crewNumber = contractParser.getMen();

        NSEW orientation = NSEW.getFromValue(heading);
        map = new IslandMap();
        drone = new Drone(map, orientation);
        crew = new Crew(map, contractParser.getMen(), contractParser.getRawContracts(), contractParser.getCraftedContracts());

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

            if (drone.isFlying()) {
                switch (drone.getLastAction()) {
                    case Scan:
                        drone.acknowledgeScan(new Scan(data.toString()));
                        break;
                    case Echo:
                        drone.acknowledgeEcho(new Echo(data.toString()));
                        break;
                }
            } else {
                crew.acknowledgeResults(results);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }


    }

    @Override
    public String deliverFinalReport() {
        StringBuilder str = new StringBuilder();

        str.append("CREEKS = ");
        map.getCreeks().forEach((key, value) -> str.append(key).append(", "));

        str.append(System.getProperty("line.separator"));

        str.append("SITE = ");
        if (map.getEmergencySite() != null) str.append(map.getEmergencySite()._1);
        else str.append("NOT FOUND");

        str.append(System.getProperty("line.separator"));

        if (map.getEmergencySite() != null)
            str.append("Nearest creek to emergency site : ").append(PathFinder.findNearestCreek(map.getCreeks(), map.getEmergencySite()._2));
        else str.append("Emergency site not found");

        logger.info("Report:");
        logger.info(str.toString());
        logger.info("Remaining points : " + remainingBudget);

        return "At least we didn't crash... Right?";
    }
}
