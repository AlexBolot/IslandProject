package fr.unice.polytech.si3.qgl.ise.maps;

import fr.unice.polytech.si3.qgl.ise.map.Coordinates;

/**
 * An interface that can describe any map
 */
public interface IslandMap {
    void addTile(Coordinates coordinates, Tile tile);

    Tile getTile(Coordinates coordinates);
}
