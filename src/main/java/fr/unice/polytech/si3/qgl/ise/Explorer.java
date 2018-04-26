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
    private static final int LIMIT = 500;
    private Drone drone;
    private Crew crew;
    private IslandMap map;
    private int remainingBudget;
    private int totalBudget;
    private ContractParser contractParser;
    private boolean shouldStop;
    private boolean notFullyScanned;

    @Override
    public void initialize(String contract) {
        try {
            logger.debug("Initializing the Explorer");
            logger.trace("Contract: " + contract);

            contractParser = new ContractParser(contract);

            String heading = contractParser.getHeading();
            remainingBudget = contractParser.getBudget();
            totalBudget = contractParser.getBudget();

            NSEW orientation = NSEW.getFromValue(heading);
            map = new IslandMap();
            drone = new Drone(map, orientation);
            shouldStop = false;
            notFullyScanned = false;
        } catch (Exception e) {
            shouldStop = true;
            logger.error("Error in init : ", e);
        }
    }

    @Override
    public String takeDecision() {
        if (shouldStop) {
            notFullyScanned = true;
            return triggerEmergency();
        }

        try {
            if (remainingBudget > LIMIT) {
                String decision;

                if (drone.isFlying()) {
                    if (remainingBudget < (totalBudget * 0.67) && !map.getCreeks().isEmpty()) {
                        notFullyScanned = true;
                        drone.stopFlying();

                    } else {
                        decision = drone.takeDecision();
                        logger.info("Decision :\t" + decision);
                        if (!decision.isEmpty()) return decision;
                    }
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
            return triggerEmergency();
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
                crew.sortContractsAfterCost(remainingBudget);
            }
        } catch (Exception e) {
            shouldStop = true;
            logger.error("Error in acknowledgeResults :", e);
        }
    }

    @Override
    public String deliverFinalReport() {
        return "Did everyone see that? Because we will not be doing it again!";
    }

    private void initCrew() {
        crew = new Crew(map, contractParser.getRawContracts(), contractParser.getCraftedContracts());
        crew.setDoNotEstimate(notFullyScanned);
    }

    private String triggerEmergency() {
        try {
            if (crew == null) initCrew();
            return new EmergencyAction(drone, crew).apply();
        } catch (Exception e) {
            logger.error("Error in triggerEmergency :", e);
            return new StopAction(drone).apply();
        }
    }
}
