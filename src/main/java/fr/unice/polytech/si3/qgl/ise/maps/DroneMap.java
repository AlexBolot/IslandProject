package fr.unice.polytech.si3.qgl.ise.maps;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that describes the map as seen by the drone, it contains the tiles and their coordinates.
 */
public class DroneMap implements IslandMap {
    private Map<Coordinates, DroneTile> droneTiles;

    public DroneMap() {
        droneTiles = new HashMap<>();
    }

    @Override
    public void addTile(Coordinates coordinates, Tile tile) {
        try {
            DroneTile droneTile = (DroneTile) tile;
            droneTiles.put(coordinates, droneTile);
        } catch (ClassCastException e) {
            e.printStackTrace(); //TODO rendre plus propre (utiliser logger ?)
        }

    }

    @Override
    public Tile getTile(Coordinates coordinates) {
        return droneTiles.get(coordinates).makeCopy();
    }

    public Map<Coordinates, DroneTile> getDroneTiles()
    {
        return droneTiles;
    }
}
