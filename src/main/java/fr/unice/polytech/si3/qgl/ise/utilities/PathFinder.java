package fr.unice.polytech.si3.qgl.ise.utilities;

import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import scala.Tuple2;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class PathFinder {

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
     * Searches for the creek nearest to the emergency site from what's recorded by the drone
     *
     * @param creeks        : map containing the recorded creeks (ids and coordinates)
     * @param emergencySite : tuple containing the recorded emergency site (id and coordinates)
     * @return the id of the creek nearest to the emergency site
     */
    public static String findNearestCreek(Map<String, Coordinates> creeks, Tuple2<String, Coordinates> emergencySite) {
        return creeks.entrySet().stream()
                .sorted(Comparator.comparingDouble(entry2 -> calculateDistance(entry2.getValue(), emergencySite._2)))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .get(0);
    }
}
