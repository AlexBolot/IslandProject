package fr.unice.polytech.si3.qgl.ise.map;

import fr.unice.polytech.si3.qgl.ise.CraftedContract;
import fr.unice.polytech.si3.qgl.ise.Explorer;
import fr.unice.polytech.si3.qgl.ise.RawContract;
import fr.unice.polytech.si3.qgl.ise.enums.Biome;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;
import org.apache.logging.log4j.Logger;
import scala.Tuple2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.apache.logging.log4j.LogManager.getLogger;

public class Forecaster {
    private static final Map<RawResource, Tuple2<Integer, Double>> resourceData = new HashMap<>();
    private static final Map<Biome, Map<RawResource, Double>> biomeData = new HashMap<>();
    private static final Logger logger = getLogger(Explorer.class);
    private Forecaster() { /*Empty private constructor to hide the implicit public one*/ }

    /**
     * Fills the maps with generic island stats
     */
    private static void fillStats() {
        resourceData.put(RawResource.FISH, new Tuple2<>(40, 0.7727665564172457));
        resourceData.put(RawResource.QUARTZ, new Tuple2<>(5, 3.495537794063535));
        resourceData.put(RawResource.ORE, new Tuple2<>(15, 1.0)); //No data
        resourceData.put(RawResource.WOOD, new Tuple2<>(40, 0.42887111592539784));
        resourceData.put(RawResource.FRUITS, new Tuple2<>(10, 2.1337618836808363));
        resourceData.put(RawResource.SUGAR_CANE, new Tuple2<>(20, 0.840776217239025));
        resourceData.put(RawResource.FLOWER, new Tuple2<>(1, 11.183116999843994));
        resourceData.put(RawResource.FUR, new Tuple2<>(5, 2.6972614067667204));

        Arrays.stream(Biome.values()).forEach(biome -> biomeData.put(biome, new HashMap<>()));

        biomeData.get(Biome.MANGROVE).put(RawResource.WOOD, 0.6);
        biomeData.get(Biome.MANGROVE).put(RawResource.FLOWER, 0.4);

        biomeData.get(Biome.TROPICAL_RAIN_FOREST).put(RawResource.WOOD, 0.4);
        biomeData.get(Biome.TROPICAL_RAIN_FOREST).put(RawResource.SUGAR_CANE, 0.4);
        biomeData.get(Biome.TROPICAL_RAIN_FOREST).put(RawResource.FRUITS, 0.2);

        biomeData.get(Biome.TROPICAL_SEASONAL_FOREST).put(RawResource.WOOD, 0.4);
        biomeData.get(Biome.TROPICAL_SEASONAL_FOREST).put(RawResource.SUGAR_CANE, 0.5);
        biomeData.get(Biome.TROPICAL_SEASONAL_FOREST).put(RawResource.FRUITS, 0.1);

        biomeData.get(Biome.TAIGA).put(RawResource.WOOD, 1.0);

        biomeData.get(Biome.TEMPERATE_RAIN_FOREST).put(RawResource.WOOD, 0.8);
        biomeData.get(Biome.TEMPERATE_RAIN_FOREST).put(RawResource.FUR, 0.2);

        biomeData.get(Biome.TEMPERATE_DECIDUOUS_FOREST).put(RawResource.WOOD, 1.0);

        biomeData.get(Biome.GRASSLAND).put(RawResource.FUR, 1.0);

        biomeData.get(Biome.SHRUBLAND).put(RawResource.FUR, 1.0);

        biomeData.get(Biome.TUNDRA).put(RawResource.FUR, 1.0);

        biomeData.get(Biome.ALPINE).put(RawResource.ORE, 0.2);
        biomeData.get(Biome.ALPINE).put(RawResource.FLOWER, 0.05);

        biomeData.get(Biome.BEACH).put(RawResource.QUARTZ, 0.2);

        biomeData.get(Biome.SUB_TROPICAL_DESERT).put(RawResource.ORE, 0.2);
        biomeData.get(Biome.SUB_TROPICAL_DESERT).put(RawResource.QUARTZ, 0.4);

        biomeData.get(Biome.TEMPERATE_DESERT).put(RawResource.ORE, 0.3);
        biomeData.get(Biome.TEMPERATE_DESERT).put(RawResource.QUARTZ, 0.3);

        biomeData.get(Biome.OCEAN).put(RawResource.FISH, 0.9);

        biomeData.get(Biome.LAKE).put(RawResource.FISH, 0.8);

        biomeData.get(Biome.GLACIER).put(RawResource.FLOWER, 0.05);
    }

    /**
     * Estimates the actual resources on the island
     *
     * @param map : the map containing the tiles
     * @return a map with resources as keys and their respective amounts on the island as values
     */
    public static Map<RawResource, Double> estimateResources(IslandMap map) {
        fillStats();
        Map<RawResource, Double> foretoldQuantities = new HashMap<>();

        map.getMap().entrySet().stream().map(Map.Entry::getValue)
                .forEach(tile -> {
                    Map<RawResource, Double> resourcesProbabilities = new HashMap<>();
                    tile.getBiomesPercentage().forEach((biome, percentage) -> biomeData.get(biome).forEach((resource, probability) -> resourcesProbabilities.put(resource, probability * ((percentage > 80) ? 1 : 0))));
                    resourcesProbabilities.forEach((resource, probability) -> {
                        if (!foretoldQuantities.containsKey(resource))
                            foretoldQuantities.put(resource, probability * resourceData.get(resource)._1);
                        else {
                            double formerQuantity = foretoldQuantities.get(resource);
                            foretoldQuantities.put(resource, formerQuantity + probability * resourceData.get(resource)._1);
                        }
                    });
                });

        return foretoldQuantities;
    }

    public static double estimateCost(CraftedContract contract) {
        fillStats();
        double cost = contract.getRemainingRawQuantitiesMinusStock().entrySet().stream()
                .mapToDouble(entry -> entry.getValue() * resourceData.get(entry.getKey())._2 * 3)
                .sum();

        logger.info("ESTIMATED COST FOR " + contract.getResource() + " " + contract.getRemainingQuantity() + " : " + cost);

        return cost;
    }

    public static double estimateCost(RawContract contract) {
        fillStats();
        double cost = contract.getRemainingQuantity() * resourceData.get(contract.getResource())._2 * 3;

        logger.info("ESTIMATED COST FOR " + contract.getResource() + " " + contract.getRemainingQuantity() + " : " + cost);

        return cost;
    }
}
