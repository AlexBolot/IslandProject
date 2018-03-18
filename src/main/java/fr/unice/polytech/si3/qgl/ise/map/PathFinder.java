package fr.unice.polytech.si3.qgl.ise.map;

import fr.unice.polytech.si3.qgl.ise.enums.Biome;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;

import java.util.*;
import java.util.stream.Collectors;

public class PathFinder
{

    /**
     Calculates the distance between two points on the map

     @param c1 : the coordinates of one point on the map
     @param c2 : the coordinates of another point on the map
     @return the distance between these two points
     */
    public static double calculateDistance (Coordinates c1, Coordinates c2)
    {
        if (c1.equals(c2)) return 0;
        return Math.sqrt(Math.pow((double) c1.getX() - c2.getX(), 2) + Math.pow((double) c1.getY() - c2.getY(), 2));
    }

    /**
     Searches for the creek nearest to the given point on the island from what's recorded by the drone

     @param creeks      : map containing the recorded creeks (ids and coordinates)
     @param coordinates : one point on the map
     @return the id of the creek nearest to the given point, or an empty string if there is no creek
     */
    public static String findNearestCreek (Map<String, Coordinates> creeks, Coordinates coordinates)
    {
        return creeks.entrySet()
                     .stream()
                     .min(Comparator.comparingDouble(entry -> calculateDistance(entry.getValue(), coordinates)))
                     .map(Map.Entry::getKey)
                     .orElse("");
    }

    /**
     Searches for the nearest tile belonging to the given biome from the given coordinates

     @param map         : the map containing the tiles
     @param coordinates : the coordinates from where the search is led
     @param biome       : the biome containing tiles to search for
     @return the coordinates of the nearest tiles or null if there is no such tile
     */
    public static Coordinates findNearestTileOfBiome (IslandMap map, Coordinates coordinates, Biome biome)
    {
        return map.getMap()
                  .entrySet()
                  .stream()
                  .filter(entry -> entry.getValue().getPossibleBiomes().contains(biome))
                  .min(Comparator.comparingDouble(entry -> calculateDistance(entry.getKey(), coordinates)))
                  .map(Map.Entry::getKey)
                  .orElse(null);
    }

    /**
     Searches for the creek nearest to given coordinates

     @param creeks      : map containing the recorded creeks (ids and coordinates)
     @param coordinates : coordinates of the point to base onto
     @return the id of the nearst creek to the point of given coordinates
     */
    public static String findNearesCreek (Map<String, Coordinates> creeks, Coordinates coordinates)
    {
        return creeks.entrySet()
                     .stream()
                     .sorted(Comparator.comparingDouble(creekCoords -> calculateDistance(creekCoords.getValue(),
                                                                                         coordinates)))
                     .map(Map.Entry::getKey)
                     .collect(Collectors.toList())
                     .get(0);
    }

    /**
     Finds the first tile that possibly contains the given rawResource

     @param rawResource : Resource to look for
     @param map         : Scanned Map that contains the biomes found
     @return the coordinates of the Tile that possibly contains the given rawResource
     */
    public static Optional<Coordinates> findRessourceTile (RawResource rawResource, IslandMap map)
    {
        ArrayList<Biome> acceptableBiomes = Arrays.stream(Biome.values())
                                                  .filter(biome -> Arrays.asList(biome.getResources())
                                                                         .contains(rawResource))
                                                  .collect(Collectors.toCollection(ArrayList::new));

        for (Map.Entry<Coordinates, Tile> entry : map.getMap().entrySet())
        {
            for (Biome acceptableBiome : acceptableBiomes)
            {
                for (Biome biome : entry.getValue().getPossibleBiomes())
                {
                    if (biome == acceptableBiome) return Optional.of(entry.getKey());
                }
            }
        }

        return Optional.empty();
    }
    //TODO findNearestTileOfResource
}
