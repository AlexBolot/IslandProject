package fr.unice.polytech.si3.qgl.ise.map;

import fr.unice.polytech.si3.qgl.ise.Explorer;
import fr.unice.polytech.si3.qgl.ise.contracts.CraftedContract;
import fr.unice.polytech.si3.qgl.ise.contracts.RawContract;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.Biome;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.RawResource;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import scala.Tuple2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.unice.polytech.si3.qgl.ise.parsing.externalresources.ExtResSelector.bundle;
import static org.apache.logging.log4j.LogManager.getLogger;

public class Forecaster {
    private static final Logger logger = getLogger(Explorer.class);
    private final Map<RawResource, Tuple2<Integer, Double>> resourcesData;
    private final Map<Biome, Map<RawResource, Double>> biomesData;

    public Forecaster() {
        resourcesData = new HashMap<>();
        biomesData = new HashMap<>();
        fillStats();
    }

    //region ========= utils for readability =====
    private static RawResource rawRes(String name) {
        return bundle().getRawRes(name);
    }

    private static Biome biome(String name) {
        return bundle().getBiome(name);
    }
    //endregion

    /**
     * Estimates the actual resources on the island
     *
     * @param map : the map containing the tiles
     * @return a map with resources as keys and their respective amounts on the island as values
     */
    public Map<RawResource, Double> estimateResources(IslandMap map) {
        Map<RawResource, Double> foretoldQuantities = new HashMap<>();

        map.getMap().entrySet().stream().map(Map.Entry::getValue)
                .forEach(tile -> {
                    Map<RawResource, Double> resourcesProbabilities = new HashMap<>();
                    tile.getBiomesPercentage().forEach((biome, percentage) -> biomesData.get(biome).forEach((resource, probability) -> resourcesProbabilities.put(resource, probability * ((percentage > 80) ? 1 : 0))));
                    resourcesProbabilities.forEach((resource, probability) -> {
                        if (!foretoldQuantities.containsKey(resource))
                            foretoldQuantities.put(resource, probability * resourcesData.get(resource)._1);
                        else {
                            double formerQuantity = foretoldQuantities.get(resource);
                            foretoldQuantities.put(resource, formerQuantity + probability * resourcesData.get(resource)._1);
                        }
                    });
                });

        return foretoldQuantities;
    }

    /**
     * Estimates the cost of a crafted contract
     *
     * @param contract : the contract to be evaluated
     * @return an estimation of the number of points needed to finish the contract
     */
    public double estimateCost(CraftedContract contract) {
        return contract.getRemainingRawQuantitiesMinusStock().entrySet().stream()
                .mapToDouble(entry -> entry.getValue() * resourcesData.get(entry.getKey())._2 * 3)
                .sum();
    }

    /**
     * Estimates the cost of a raw contract
     *
     * @param contract : the contract to be evaluated
     * @return an estimation of the number of points needed to finish the contract
     */
    public double estimateCost(RawContract contract) {
        return contract.getRemainingQuantity() * resourcesData.get(contract.getResource())._2 * 3;
    }

    /**
     * Fills the maps with generic island data from resources/forecaster-data.json
     */
    private void fillStats() {
        String builder = "";

        try (InputStream is = Forecaster.class.getClassLoader().getResourceAsStream("forecaster-data.json")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            builder = reader.lines().map(str -> str + "\n").collect(Collectors.joining());
        } catch (IOException e) {
            logger.error("Problem while reading forecaster data: ", e);
        }

        JSONObject data = new JSONObject(builder);

        JSONObject jsonResourcesData = data.getJSONObject("resources-data");
        JSONObject jsonBiomesData = data.getJSONObject("biomes-data");

        jsonResourcesData.keySet().forEach(resource -> {
            JSONObject resourceData = jsonResourcesData.getJSONObject(resource);
            int amount = resourceData.getInt("amount");
            double cost = resourceData.getDouble("cost");
            resourcesData.put(rawRes(resource), new Tuple2<>(amount, cost));
        });

        jsonBiomesData.keySet().forEach(biome -> {
            JSONObject biomeData = jsonBiomesData.getJSONObject(biome);
            biomesData.put(biome(biome), new HashMap<>());
            biomeData.keySet().forEach(resource -> biomesData.get(biome(biome)).put(rawRes(resource), biomeData.getDouble(resource)));
        });
    }
}
