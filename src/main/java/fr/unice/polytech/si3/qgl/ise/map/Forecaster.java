package fr.unice.polytech.si3.qgl.ise.map;

import fr.unice.polytech.si3.qgl.ise.contracts.CraftedContract;
import fr.unice.polytech.si3.qgl.ise.contracts.RawContract;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.Biome;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.RawResource;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;

import static fr.unice.polytech.si3.qgl.ise.parsing.externalresources.ExtResSelector.bundle;

public class Forecaster {
    private static final Map<RawResource, Tuple2<Integer, Double>> resourceData = new HashMap<>();
    private static final Map<Biome, Map<RawResource, Double>> biomeData = new HashMap<>();

    private Forecaster() { /*Empty private constructor to hide the implicit public one*/ }

    /**
     * Fills the maps with generic island stats
     */
    private static void fillStats() {

        RawResource quartz = rawRes("QUARTZ");
        RawResource fruits = rawRes("FRUITS");
        RawResource sugarCane = rawRes("SUGAR_CANE");
        RawResource flower = rawRes("FLOWER");

        resourceData.put(rawRes("FISH"), new Tuple2<>(40, 0.7727665564172457));
        resourceData.put(quartz, new Tuple2<>(5, 3.495537794063535));
        resourceData.put(rawRes("ORE"), new Tuple2<>(15, 1.0)); //No data
        resourceData.put(rawRes("WOOD"), new Tuple2<>(40, 0.42887111592539784));
        resourceData.put(fruits, new Tuple2<>(10, 2.1337618836808363));
        resourceData.put(sugarCane, new Tuple2<>(20, 0.840776217239025));
        resourceData.put(flower, new Tuple2<>(1, 11.183116999843994));
        resourceData.put(rawRes("FUR"), new Tuple2<>(5, 2.6972614067667204));

        bundle().getBiomes().forEach(biome -> biomeData.put(biome, new HashMap<>()));

        biomeData.get(biome("MANGROVE")).put(rawRes("WOOD"), 0.6);
        biomeData.get(biome("MANGROVE")).put(flower, 0.4);

        Biome tropicalRainForest = biome("TROPICAL_RAIN_FOREST");
        biomeData.get(tropicalRainForest).put(rawRes("WOOD"), 0.4);
        biomeData.get(tropicalRainForest).put(sugarCane, 0.4);
        biomeData.get(tropicalRainForest).put(fruits, 0.2);

        Biome tropicalSeasonalForest = biome("TROPICAL_SEASONAL_FOREST");
        biomeData.get(tropicalSeasonalForest).put(rawRes("WOOD"), 0.4);
        biomeData.get(tropicalSeasonalForest).put(sugarCane, 0.5);
        biomeData.get(tropicalSeasonalForest).put(fruits, 0.1);

        biomeData.get(biome("TAIGA")).put(rawRes("WOOD"), 1.0);

        biomeData.get(biome("TEMPERATE_RAIN_FOREST")).put(rawRes("WOOD"), 0.8);
        biomeData.get(biome("TEMPERATE_RAIN_FOREST")).put(rawRes("FUR"), 0.2);

        biomeData.get(biome("TEMPERATE_DECIDUOUS_FOREST")).put(rawRes("WOOD"), 1.0);

        biomeData.get(biome("GRASSLAND")).put(rawRes("FUR"), 1.0);

        biomeData.get(biome("SHRUBLAND")).put(rawRes("FUR"), 1.0);

        biomeData.get(biome("TUNDRA")).put(rawRes("FUR"), 1.0);

        biomeData.get(biome("ALPINE")).put(rawRes("ORE"), 0.2);
        biomeData.get(biome("ALPINE")).put(flower, 0.05);

        biomeData.get(biome("BEACH")).put(quartz, 0.2);

        biomeData.get(biome("SUB_TROPICAL_DESERT")).put(rawRes("ORE"), 0.2);
        biomeData.get(biome("SUB_TROPICAL_DESERT")).put(quartz, 0.4);

        biomeData.get(biome("TEMPERATE_DESERT")).put(rawRes("ORE"), 0.3);
        biomeData.get(biome("TEMPERATE_DESERT")).put(quartz, 0.3);

        biomeData.get(biome("OCEAN")).put(rawRes("FISH"), 0.9);

        biomeData.get(biome("LAKE")).put(rawRes("FISH"), 0.8);

        biomeData.get(biome("GLACIER")).put(flower, 0.05);
    }

    //region --- utils for lisibility ---
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

    /**
     * Estimates the cost of a crafted contract
     *
     * @param contract : the contract to be evaluated
     * @return an estimation of the number of points needed to finish the contract
     */
    public static double estimateCost(CraftedContract contract) {
        fillStats();
        return contract.getRemainingRawQuantitiesMinusStock().entrySet().stream()
                .mapToDouble(entry -> entry.getValue() * resourceData.get(entry.getKey())._2 * 3)
                .sum();
    }

    /**
     * Estimates the cost of a raw contract
     *
     * @param contract : the contract to be evaluated
     * @return an estimation of the number of points needed to finish the contract
     */
    public static double estimateCost(RawContract contract) {
        fillStats();
        return contract.getRemainingQuantity() * resourceData.get(contract.getResource())._2 * 3;
    }
}