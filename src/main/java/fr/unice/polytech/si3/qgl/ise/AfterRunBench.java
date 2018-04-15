package fr.unice.polytech.si3.qgl.ise;

import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.logging.log4j.LogManager.getLogger;

public class AfterRunBench {

    private static final Logger logger = getLogger(AfterRunBench.class);
    private Map<String, Long> inventory = new HashMap<>();
    private Map<String, Long> contracts = new HashMap<>();
    private Map<String, Long> rawContracts = new HashMap<>();
    private Map<String, Long> craftedContracts = new HashMap<>();
    private Map<String, Long> completedRawContracts = new HashMap<>();
    private Map<String, Long> completedCraftedContracts = new HashMap<>();
    private Map<String, Integer> actions = new HashMap<>();
    private List<String> creeks = new ArrayList<>();
    private long remainingPoints;
    private File jsonLog = new File("Explorer_ise.json");
    private JSONParser jsonParser = new JSONParser();

    public void fill() {
        if (jsonLog.exists() && !jsonLog.isDirectory()) {
            try {
                JSONArray list = (JSONArray) jsonParser.parse(new FileReader(jsonLog));

                JSONObject initData = (JSONObject) ((JSONObject) list.get(0)).get("data");
                remainingPoints = (long) initData.get("budget");
                JSONArray contracts = (JSONArray) initData.get("contracts");
                for (Object contract : contracts) {
                    this.contracts.put((String) ((JSONObject) contract).get("resource"), (long) ((JSONObject) contract).get("amount"));
                }

                for (int i = 1; i < list.size(); i++) {
                    JSONObject jsonObject = (JSONObject) list.get(i);

                    if (((JSONObject) jsonObject.get("data")).containsKey("action")) {
                        String action = (String) ((JSONObject) jsonObject.get("data")).get("action");
                        JSONObject actionResults = (JSONObject) list.get(i + 1);

                        long cost = (long) ((JSONObject) actionResults.get("data")).get("cost");

                        remainingPoints -= cost;

                        if (((JSONObject) jsonObject.get("data")).get("action").equals("exploit")) {
                            long amount = (long) ((JSONObject) ((JSONObject) actionResults.get("data")).get("extras")).get("amount");
                            String resource = (String) ((JSONObject) ((JSONObject) jsonObject.get("data")).get("parameters")).get("resource");

                            if (!inventory.containsKey(resource)) {
                                inventory.put(resource, amount);
                            } else {
                                long formerStock = inventory.get(resource);
                                inventory.put(resource, formerStock + amount);
                            }
                        } else if (((JSONObject) jsonObject.get("data")).get("action").equals("scan")) {
                            JSONArray creeks = (JSONArray) ((JSONObject) ((JSONObject) actionResults.get("data")).get("extras")).get("creeks");
                            for (Object creek : creeks) {
                                this.creeks.add((String) creek);
                            }
                        } else if (((JSONObject) jsonObject.get("data")).get("action").equals("transform")) {
                            JSONObject usedResources = (JSONObject) ((JSONObject) jsonObject.get("data")).get("parameters");
                            JSONObject producedResource = (JSONObject) ((JSONObject) actionResults.get("data")).get("extras");

                            if (!inventory.containsKey((String) producedResource.get("kind"))) {
                                inventory.put((String) producedResource.get("kind"), (long) producedResource.get("production"));
                            } else {
                                long formerStock = inventory.get((String) producedResource.get("kind"));
                                inventory.put((String) producedResource.get("kind"), (long) producedResource.get("production") + formerStock);
                            }

                            for (Object o : usedResources.entrySet()) {
                                Map.Entry entry = (Map.Entry) o;
                                String resource = (String) entry.getKey();
                                long amount = (long) entry.getValue();

                                long formerStock = inventory.get(resource);
                                inventory.put(resource, formerStock - amount);
                            }
                        }

                        if (!actions.containsKey(action)) {
                            actions.put(action, 1);
                        } else {
                            int formerNumberOfActions = actions.get(action);
                            actions.put(action, formerNumberOfActions + 1);
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void computeContracts() {
        contracts.forEach((resource, amount) -> {
            if (resource.equals("GLASS") || resource.equals("INGOT") || resource.equals("LEATHER") || resource.equals("PLANK") || resource.equals("RUM")) {
                if (inventory.get(resource) >= amount) {
                    completedCraftedContracts.put(resource, amount);
                } else {
                    craftedContracts.put(resource, amount);
                }
            } else {
                if (inventory.get(resource) >= amount) {
                    completedRawContracts.put(resource, amount);
                } else {
                    rawContracts.put(resource, amount);
                }
            }
        });
    }

    public void display() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Report:").append("\n");

        stringBuilder.append("Creeks found: ").append(creeks.size()).append("\n");

        stringBuilder.append("Points earned: ").append(completedCraftedContracts.size() * 2 + completedRawContracts.size()).append("\n");

        stringBuilder.append("Raw Contracts: ").append(completedRawContracts.size()).append("/").append(completedRawContracts.size() + rawContracts.size()).append("\n");

        stringBuilder.append("Crafted Contracts: ").append(completedCraftedContracts.size()).append("/").append(completedCraftedContracts.size() + craftedContracts.size()).append("\n");

        stringBuilder.append("Inventory:").append("\n");

        inventory.forEach((resource, amount) -> stringBuilder.append(resource).append(" - ").append(amount).append("\n"));

        completedRawContracts.forEach((resource, amount) -> stringBuilder.append("Raw contract completed: ").append(resource).append(" - ").append(amount).append("\n"));

        completedCraftedContracts.forEach((resource, amount) -> stringBuilder.append("Crafted contract completed: ").append(resource).append(" - ").append(amount).append("\n"));

        rawContracts.forEach((resource, amount) -> stringBuilder.append("Raw contract failed: ").append(resource).append(" - ").append(amount).append("\n"));

        craftedContracts.forEach((resource, amount) -> stringBuilder.append("Crafted contract failed: ").append(resource).append(" - ").append(amount).append("\n"));

        stringBuilder.append("Remaining points: ").append(remainingPoints).append("\n");

        logger.info(stringBuilder.toString());
    }
}
