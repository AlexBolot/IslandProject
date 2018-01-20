package fr.unice.polytech.si3.qgl.ise.maps;

/**
 * A parent-class for all kinds of tiles
 */
public abstract class Tile {
    Tile() {
    }

    abstract Tile makeCopy();
}
