package fr.unice.polytech.si3.qgl.ise.map;

import fr.unice.polytech.si3.qgl.ise.enums.Biome;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;

import java.util.*;
import java.util.stream.Collectors;

public class PathFinder {

    private static final double MINIMAL_PERCENTAGE = 80;

    private PathFinder() { /*Empty private constructor to hide the implicit public one*/ }

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
     * Assigns a score to a creek based on its proximity to the given resources
     *
     * @param map       : the map containing the tiles
     * @param creekId   : the creek we want to assign a score to
     * @param resources : the resources we want to be near the creek
     * @return a score for the creek
     */
    private static double calculateScore(IslandMap map, String creekId, List<RawResource> resources) {
        List<Biome> acceptableBiomes = new ArrayList<>();

        for (RawResource resource : resources) {
            acceptableBiomes.addAll(Arrays.stream(Biome.values())
                    .filter(biome -> Arrays.asList(biome.getResources()).contains(resource))
                    .collect(Collectors.toList()));
        }

        return map.getMap().entrySet().stream()
                .filter(entry -> !Collections.disjoint(entry.getValue().getPossibleBiomes(), acceptableBiomes))
                .mapToDouble(entry -> 1 / (calculateDistance(entry.getKey(), map.getCreeks().get(creekId)) + 1))
                .sum();
    }

    /**
     * Searches for the best creek that is nearest to the given resources
     *
     * @param map       : the map containing the tiles
     * @param resources : the resources we want to be near the best creek
     * @return the id of the best creek, or an empty string if there is no such creek
     */
    public static String findBestCreek(IslandMap map, List<RawResource> resources) {
        return map.getCreeks().entrySet().stream()
                .max(Comparator.comparingDouble(entry -> calculateScore(map, entry.getKey(), resources)))
                .filter(entry -> calculateScore(map, entry.getKey(), resources) > 0)
                .map(Map.Entry::getKey)
                .orElse("");
    }

    /**
     * Searches for the nearest tile belonging to the given biome from the given coordinates, excluding already explored tiles
     *
     * @param map         : the map containing the tiles
     * @param coordinates : the coordinates from where the search is led
     * @param biome       : the biome containing tiles to search for
     * @return the coordinates of the nearest tiles or null if there is no such tile
     */
    public static Coordinates findNearestTileOfBiome(IslandMap map, Coordinates coordinates, Biome biome) {
        return map.getMap().entrySet().stream()
                .filter(entry -> entry.getValue().getBiomePercentage(biome) > MINIMAL_PERCENTAGE)
                .filter(entry -> !entry.getValue().isExplored())
                .min(Comparator.comparingDouble(entry -> calculateDistance(entry.getKey(), coordinates)))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Finds the nearest tile that possibly contains the given raw resource, excluding already explored tiles
     *
     * @param map         : the map containing the tiles
     * @param coordinates : the coordinates from where the search is led
     * @param resource    : resource to look for
     * @return the coordinates of the nearest tile that possibly contains the given raw resource, or null if there is no such tile
     */
    public static Coordinates findNearestTileOfResource(IslandMap map, Coordinates coordinates, RawResource resource) {
        List<Biome> acceptableBiomes = Arrays.stream(Biome.values())
                .filter(biome -> Arrays.asList(biome.getResources()).contains(resource))
                .collect(Collectors.toList());

        return acceptableBiomes.stream()
                .map(biome -> findNearestTileOfBiome(map, coordinates, biome))
                .filter(Objects::nonNull)
                .min(Comparator.comparingDouble(tileCoordinates -> calculateDistance(tileCoordinates, coordinates)))
                .orElse(null);
    }
}
