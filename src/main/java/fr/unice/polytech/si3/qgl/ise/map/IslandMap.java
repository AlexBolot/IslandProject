package fr.unice.polytech.si3.qgl.ise.map;

import java.util.HashMap;
import java.util.Map;

public class IslandMap {
    private Map<Coordinates, Tile> tiles;
    private Map<Coordinates, String> creeks;
    private Map<Coordinates, String> sites;

    public IslandMap() {
        tiles = new HashMap<>();
        creeks = new HashMap<>();
        sites = new HashMap<>();
    }

    public void addTile(Coordinates coordinates, Tile tile) {
        tiles.put(coordinates, tile);
    }

    public Tile getTile(Coordinates coordinates) {
        return new Tile(tiles.get(coordinates));
    }

    public void addCreek(Coordinates coordinates, String creekId) {
        creeks.put(coordinates, creekId);
    }

    public Map<Coordinates, String> getCreeks() {
        return new HashMap<>(creeks);
    }

    public void addSite(Coordinates coordinates, String siteId) {
        sites.put(coordinates, siteId);
    }

    public Map<Coordinates, String> getSites() {
        return new HashMap<>(sites);
    }
}
