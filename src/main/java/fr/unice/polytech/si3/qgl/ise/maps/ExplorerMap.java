package fr.unice.polytech.si3.qgl.ise.maps;

import fr.unice.polytech.si3.qgl.ise.map.Coordinates;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that describes the map as seen by the explorer(s), it contains the tiles and their coordinates.
 */
public class ExplorerMap implements IslandMap {
    private Map<Coordinates, ExplorerTile> explorerTiles;

    public ExplorerMap() {
        explorerTiles = new HashMap<>();
    }

    @Override
    public void addTile(Coordinates coordinates, Tile tile) {
        try {
            ExplorerTile explorerTile = (ExplorerTile)tile;
            explorerTiles.put(coordinates, explorerTile);
        } catch (ClassCastException e) {
            e.printStackTrace(); //TODO rendre plus propre (utiliser logger ?)
        }

    }

    @Override
    public Tile getTile(Coordinates coordinates) {
        return explorerTiles.get(coordinates).makeCopy();
    }
}
