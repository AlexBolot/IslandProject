package fr.unice.polytech.si3.qgl.ise;

import eu.ace_design.island.bot.IExplorerRaid;
import fr.unice.polytech.si3.qgl.ise.actions.StopAction;
import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.parsing.ContractParser;
import fr.unice.polytech.si3.qgl.ise.parsing.Echo;
import fr.unice.polytech.si3.qgl.ise.parsing.Scan;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.List;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.NSEW;
import static org.apache.logging.log4j.LogManager.getLogger;

public class Explorer implements IExplorerRaid {
    private static Logger logger = getLogger(Explorer.class);
    private Drone drone;
    private Crew crew;
    private IslandMap map;
    private int remainingBudget;
    private List<RawContract> rawConrtacts;
    private List<CraftedContract> craftedContracts;
    private int crewNumber;
    private ContractParser contractParser;

    @Override
    public void initialize(String contract) {
        logger.debug("Initializing the Explorer");
        logger.trace("Contract: " + contract);

        contractParser = new ContractParser(contract);

        String heading = contractParser.getHeading();
        remainingBudget = contractParser.getBudget();
        crewNumber = contractParser.getMen();

        NSEW orientation = NSEW.getFromValue(heading);
        map = new IslandMap();
        drone = new Drone(map, orientation);
    }

    @Override
    public String takeDecision() {
        try {
            if (remainingBudget > 150) {
                String decision;

                if (drone.isFlying())
                {
                    decision = drone.takeDecision();
                    logger.info("Decision :\t" + decision);
                    if (!decision.isEmpty()) return decision;
                }

                if (crew == null) crew = new Crew(map,
                                                  contractParser.getMen(),
                                                  contractParser.getRawContracts(),
                                                  contractParser.getCraftedContracts());

                decision = crew.takeDecision();
                logger.info("Crew :\t\t" + decision);

                return decision.isEmpty() ? new StopAction().apply() : decision;
            }
            else
            {
                logger.info("No more budget!");
                return new StopAction(drone).apply();
            }
        }
        catch (Exception e)
        {
            logger.info(e.getMessage());
            return new StopAction(drone).apply();
        }
    }

    @Override
    public void acknowledgeResults(String results) {
        try {
            JSONObject data = new JSONObject(results);

            logger.info("Réponse :\t" + data);

            remainingBudget -= data.getInt("cost");

            if (drone.isFlying())
            {
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
            else
            {
                crew.acknowledgeResults(results);
            }
        }
        catch (Exception e)
        {
            logger.info(e.getMessage());
        }
    }

    @Override
    public String deliverFinalReport() {
        StringBuilder str2 = new StringBuilder();

        crew.getStock().forEach((key, value) -> str2.append(key).append(" - ").append(value).append("\n"));

        logger.info("Report:");
        logger.info(str2.toString());
        logger.info("Remaining points : " + remainingBudget);

        return str2.toString();
    }
}
