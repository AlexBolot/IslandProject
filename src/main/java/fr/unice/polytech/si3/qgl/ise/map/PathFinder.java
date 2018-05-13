package fr.unice.polytech.si3.qgl.ise.map;

import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.Biome;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.RawResource;

import java.util.*;
import java.util.stream.Collectors;

import static fr.unice.polytech.si3.qgl.ise.parsing.externalresources.ExtResSelector.bundle;

public class PathFinder {

    private final double minThreshold;
    private final double maxThreshold;
    private final IslandMap map;
    private double actualThreshold;

    /**
     * Creates a pathfinder object
     *
     * @param map          : the map containing the tiles
     * @param minThreshold : the minimum probability threshold allowed
     * @param maxThreshold : the base probability threshold allowed
     */
    public PathFinder(IslandMap map, double minThreshold, double maxThreshold) {
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;
        this.actualThreshold = maxThreshold;
        this.map = map;
    }

    /**
     * Calculates the distance between two points on the map
     *
     * @param c1 : the coordinates of one point on the map
     * @param c2 : the coordinates of another point on the map
     * @return the distance between these two points
     */
    public static double calculateDistance(Coordinates c1, Coordinates c2) {
        if (c1.equals(c2)) return 0;
        return Math.sqrt(Math.pow((double) c1.getX() - c2.getX(), 2) + Math.pow((double) c1.getY() - c2.getY(), 2));
    }

    /**
     * Searches for the best creek that is nearest to the given resources
     *
     * @param resources : the resources we want to be near the best creek
     * @return the id of the best creek, or an empty string if there is no such creek
     */
    public String findBestCreek(List<RawResource> resources) {
        return map.getCreeks().entrySet().stream()
                .max(Comparator.comparingDouble(entry -> calculateScore(entry.getKey(), resources)))
                .filter(entry -> calculateScore(entry.getKey(), resources) > 0)
                .map(Map.Entry::getKey)
                .orElse("");
    }

    /**
     * Searches for the nearest tile belonging to the given biome from the given coordinates, excluding already explored tiles
     *
     * @param coordinates : the coordinates from where the search is led
     * @param biome       : the biome containing tiles to search for
     * @return the coordinates of the nearest tiles or null if there is no such tile
     */
    public Coordinates findNearestTileOfBiome(Coordinates coordinates, Biome biome) {
        return map.getMap().entrySet().stream()
                .filter(entry -> entry.getValue().getBiomePercentage(biome) > actualThreshold)
                .filter(entry -> !entry.getValue().isExplored())
                .min(Comparator.comparingDouble(entry -> calculateDistance(entry.getKey(), coordinates) + 0.001 * calculateDistance(entry.getKey(), map.getShip())))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Finds the nearest tile that possibly contains the given raw resource, excluding already explored tiles
     *
     * @param coordinates : the coordinates from where the search is led
     * @param resource    : resource to look for
     * @return the coordinates of the nearest tile that possibly contains the given raw resource, or null if there is no such tile
     */
    public Coordinates findNearestTileOfResource(Coordinates coordinates, RawResource resource) {
        List<Biome> acceptableBiomes = bundle().getBiomes().stream()
                .filter(biome -> biome.hasResource(resource))
                .collect(Collectors.toList());

        return acceptableBiomes.stream()
                .map(biome -> findNearestTileOfBiome(coordinates, biome))
                .filter(Objects::nonNull)
                .min(Comparator.comparingDouble((Coordinates tileCoordinates) -> calculateDistance(tileCoordinates, coordinates) + 0.001 * calculateDistance(tileCoordinates, map.getShip())))
                .orElse(null);
    }

    /**
     * Finds the nearest tile that possibly contains any of the given raw resources, excluding already explored tiles
     *
     * @param coordinates : the coordinates from where the search is led
     * @param resources   : resources to look for
     * @return the coordinates of the nearest tile that possibly contains any of the given raw resources, or null if there is no such tile
     */
    public Coordinates findNearestTileOfAnyResource(Coordinates coordinates, List<RawResource> resources) {
        Coordinates nearest = resources.stream()
                .map(resource -> findNearestTileOfResource(coordinates, resource))
                .filter(Objects::nonNull)
                .min(Comparator.comparingDouble((Coordinates tileCoordinates) -> calculateDistance(tileCoordinates, coordinates) + 0.001 * calculateDistance(tileCoordinates, map.getShip())))
                .orElse(null);

        if (nearest == null && actualThreshold > minThreshold) {
            actualThreshold -= 5;
            return findNearestTileOfAnyResource(coordinates, resources);
        } else {
            actualThreshold = maxThreshold;
            return nearest;
        }
    }

    /**
     * Assigns a score to a creek based on its proximity to the given resources
     *
     * @param creekId   : the creek we want to assign a score to
     * @param resources : the resources we want to be near the creek
     * @return a score for the creek
     */
    private double calculateScore(String creekId, List<RawResource> resources) {
        List<Biome> acceptableBiomes = new ArrayList<>();

        for (RawResource resource : resources) {
            acceptableBiomes.addAll(bundle().getBiomes().stream()
                    .filter(biome -> biome.hasResource(resource))
                    .collect(Collectors.toList()));
        }

        return map.getMap().entrySet().stream()
                .filter(entry -> !Collections.disjoint(entry.getValue().getPossibleBiomes(), acceptableBiomes))
                .mapToDouble(entry -> 1 / (calculateDistance(entry.getKey(), map.getCreeks().get(creekId)) + 1))
                .sum();
    }
}
