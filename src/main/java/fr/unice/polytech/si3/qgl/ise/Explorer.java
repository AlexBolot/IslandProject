package fr.unice.polytech.si3.qgl.ise;

import eu.ace_design.island.bot.IExplorerRaid;
import fr.unice.polytech.si3.qgl.ise.actions.EmergencyAction;
import fr.unice.polytech.si3.qgl.ise.actions.StopAction;
import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.parsing.ContractParser;
import fr.unice.polytech.si3.qgl.ise.parsing.Echo;
import fr.unice.polytech.si3.qgl.ise.parsing.Scan;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;
import static org.apache.logging.log4j.LogManager.getLogger;

public class Explorer implements IExplorerRaid {
    private static final Logger logger = getLogger(Explorer.class);
    private static final int LIMIT = 250;
    private Drone drone;
    private Crew crew;
    private IslandMap map;
    private int remainingBudget;
    private ContractParser contractParser;
    private boolean shouldStop;

    @Override
    public void initialize(String contract) {
        try {
            logger.debug("Initializing the Explorer");
            logger.trace("Contract: " + contract);

            contractParser = new ContractParser(contract);

            String heading = contractParser.getHeading();
            remainingBudget = contractParser.getBudget();

            NSEW orientation = NSEW.getFromValue(heading);
            map = new IslandMap();
            drone = new Drone(map, orientation);
            shouldStop = false;
        } catch (Exception e) {
            shouldStop = true;
            logger.error("Error in init : ", e);
        }
    }

    @Override
    public String takeDecision() {
        if (shouldStop) return triggerEmergecy();

        try {
            if (remainingBudget > LIMIT) {
                String decision;

                if (drone.isFlying()) {
                    decision = drone.takeDecision();
                    logger.info("Decision :\t" + decision);
                    if (!decision.isEmpty()) return decision;
                }

                if (crew == null) initCrew();

                decision = crew.takeDecision();
                logger.info("Crew :\t\t" + decision);

                return decision.isEmpty() ? new StopAction().apply() : decision;
            } else {
                logger.info("No more budget!");
                return new StopAction(drone).apply();
            }
        } catch (Exception e) {
            logger.error("Error in takeDecision :", e);
            return triggerEmergecy();
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
                    case SCAN:
                        drone.acknowledgeScan(new Scan(data.toString()));
                        break;
                    case ECHO:
                        drone.acknowledgeEcho(new Echo(data.toString()));
                        break;
                    default:
                        break;
                }
            } else {
                crew.acknowledgeResults(results);
            }
        } catch (Exception e) {
            shouldStop = true;
            logger.error("Error in acknowledgeResults :", e);
        }
    }

    @Override
    public String deliverFinalReport() {
        try {
            StringBuilder str2 = new StringBuilder();

            str2.append("Report:").append("\n");

            str2.append("Creeks found: ").append(drone.getMap().getCreeks().size()).append("\n");

            str2.append("Points earned: ").append(crew.getCompletedRawContracts().size() + crew.getCompletedCraftedContracts().size() * 2).append("\n");

            str2.append("Raw Contracts: ").append(crew.getCompletedRawContracts().size()).append("/").append(crew.getCompletedRawContracts().size() + crew.getRawContracts().size()).append("\n");

            str2.append("Crafted Contracts: ").append(crew.getCompletedCraftedContracts().size()).append("/").append(crew.getCompletedCraftedContracts().size() + crew.getCraftedContracts().size()).append("\n");

            str2.append("Inventory:").append("\n");

            crew.getStock().forEach((key, value) -> str2.append(key).append(" - ").append(value).append("\n"));

            crew.getCompletedRawContracts().forEach(rawContract -> str2.append("Raw contract completed: ").append(rawContract).append("\n"));

            crew.getCompletedCraftedContracts().forEach(craftedContract -> str2.append("Crafted contract completed: ").append(craftedContract).append("\n"));

            crew.getRawContracts().forEach(rawContract -> str2.append("Raw contract failed: ").append(rawContract).append("\n"));

            crew.getCraftedContracts().forEach(craftedContract -> str2.append("Crafted contract failed: ").append(craftedContract).append("\n"));

            str2.append("Remaining points: ").append(remainingBudget).append("\n");

            logger.info(str2.toString());

        } catch (Exception e) {
            logger.error("Error in final report :", e);
        }

        return "Did everyone see that? Because we will not be doing it again!";
    }

    private void initCrew() {
        crew = new Crew(map, contractParser.getRawContracts(), contractParser.getCraftedContracts());
    }

    private String triggerEmergecy() {
        try {
            if (crew == null) initCrew();
            return new EmergencyAction(drone, crew).apply();
        } catch (Exception e) {
            logger.error("Error in triggerEmergency :", e);
            return new StopAction(drone).apply();
        }
    }
}
