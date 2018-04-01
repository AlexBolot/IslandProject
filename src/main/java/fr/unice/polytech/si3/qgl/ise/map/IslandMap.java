package fr.unice.polytech.si3.qgl.ise.map;

import scala.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IslandMap {
    //FIXME Discuter de ces probabilités
    public static final double[] percentageOfLayerForUpdate = {100, 70, 60, 50, 40, 30, 20};

    private final Map<Coordinates, Tile> tiles;
    private final Map<String, Coordinates> creeks;
    private Tuple2<String, Coordinates> emergencySite;

    public IslandMap() {
        tiles = new HashMap<>();
        creeks = new HashMap<>();
        emergencySite = null;
    }

    public void addTile(Coordinates coordinates, Tile tile) {
        tiles.put(coordinates, tile);
    }

    public void addCreek(Coordinates coordinates, String creekId) {
        creeks.put(creekId, coordinates);
    }

    public void addCreeks(Coordinates coordinates, ArrayList<String> creekIds) {
        creekIds.forEach(id -> addCreek(coordinates, id));
    }

    public Map<String, Coordinates> getCreeks() {
        return new HashMap<>(creeks);
    }

    public void addSite(Coordinates coordinates, String siteId) {
        emergencySite = new Tuple2<>(siteId, coordinates);
    }

    public Tuple2<String, Coordinates> getEmergencySite() {
        return emergencySite;
    }

    public Tile getTile(Coordinates coordinates) {
        if (tiles.containsKey(coordinates))
            return tiles.get(coordinates);
        else {
            Tile tile = new Tile();
            tiles.put(coordinates, tile);
            return tile;
        }
    }

    Map<Coordinates, Tile> getMap() {
        return tiles;
    }

    /**
     * Return all Tile to update with a layer accuracy system, the layer 1 is 100% sure, the 7th is almost unknown
     *
     * @param x center of the 3*3 drone map
     * @param y center of the 3*3 drone map
     * @return List of List of tile correspondings of layers to update
     */
    public List<List<Tile>> getTileToUpdateFrom(int x, int y) {
        List<List<Tile>> layers = new ArrayList<>();
        //region layer 1
        List<Tile> layer1 = new ArrayList<>();
        for (int i = x - 1; i <= x + 1; ++i)
            for (int j = y - 1; j <= y + 1; ++j)
                layer1.add(getTile(new Coordinates(i, j)));
        layers.add(layer1);
        //endregion
        //region layer 2
        List<Tile> layer2 = new ArrayList<>();
        layer2.add(getTile(new Coordinates(x - 2, y - 1)));
        layer2.add(getTile(new Coordinates(x - 2, y)));
        layer2.add(getTile(new Coordinates(x - 2, y + 1)));
        layer2.add(getTile(new Coordinates(x + 2, y - 1)));
        layer2.add(getTile(new Coordinates(x + 2, y)));
        layer2.add(getTile(new Coordinates(x + 2, y + 1)));
        layer2.add(getTile(new Coordinates(x - 1, y - 2)));
        layer2.add(getTile(new Coordinates(x, y - 2)));
        layer2.add(getTile(new Coordinates(x + 1, y - 2)));
        layer2.add(getTile(new Coordinates(x - 1, y + 2)));
        layer2.add(getTile(new Coordinates(x, y + 2)));
        layer2.add(getTile(new Coordinates(x + 1, y + 2)));
        layers.add(layer2);
        //endregion
        //region layer 3
        List<Tile> layer3 = new ArrayList<>();
        layer3.add(getTile(new Coordinates(x - 3, y - 1)));
        layer3.add(getTile(new Coordinates(x - 3, y)));
        layer3.add(getTile(new Coordinates(x - 3, y + 1)));
        layer3.add(getTile(new Coordinates(x + 3, y - 1)));
        layer3.add(getTile(new Coordinates(x + 3, y)));
        layer3.add(getTile(new Coordinates(x + 3, y + 1)));
        layer3.add(getTile(new Coordinates(x - 1, y - 3)));
        layer3.add(getTile(new Coordinates(x, y - 3)));
        layer3.add(getTile(new Coordinates(x + 1, y - 3)));
        layer3.add(getTile(new Coordinates(x - 1, y + 3)));
        layer3.add(getTile(new Coordinates(x, y + 3)));
        layer3.add(getTile(new Coordinates(x + 1, y + 3)));
        layer3.add(getTile(new Coordinates(x - 2, y - 2)));
        layer3.add(getTile(new Coordinates(x - 2, y + 2)));
        layer3.add(getTile(new Coordinates(x + 2, y - 2)));
        layer3.add(getTile(new Coordinates(x + 2, y + 2)));
        layers.add(layer3);
        //endregion
        //region layer 4
        List<Tile> layer4 = new ArrayList<>();
        layer4.add(getTile(new Coordinates(x - 4, y - 1)));
        layer4.add(getTile(new Coordinates(x - 4, y)));
        layer4.add(getTile(new Coordinates(x - 4, y + 1)));
        layer4.add(getTile(new Coordinates(x + 4, y - 1)));
        layer4.add(getTile(new Coordinates(x + 4, y)));
        layer4.add(getTile(new Coordinates(x + 4, y + 1)));
        layer4.add(getTile(new Coordinates(x - 1, y - 4)));
        layer4.add(getTile(new Coordinates(x, y - 4)));
        layer4.add(getTile(new Coordinates(x + 1, y - 4)));
        layer4.add(getTile(new Coordinates(x - 1, y + 4)));
        layer4.add(getTile(new Coordinates(x, y + 4)));
        layer4.add(getTile(new Coordinates(x + 1, y + 4)));
        layer4.add(getTile(new Coordinates(x - 3, y + 2)));
        layer4.add(getTile(new Coordinates(x - 2, y + 3)));
        layer4.add(getTile(new Coordinates(x + 2, y + 3)));
        layer4.add(getTile(new Coordinates(x + 3, y + 2)));
        layer4.add(getTile(new Coordinates(x - 3, y - 2)));
        layer4.add(getTile(new Coordinates(x - 2, y - 3)));
        layer4.add(getTile(new Coordinates(x + 2, y - 3)));
        layer4.add(getTile(new Coordinates(x + 3, y - 2)));
        layers.add(layer4);
        //endregion
        //region layer 5
        List<Tile> layer5 = new ArrayList<>();
        layer5.add(getTile(new Coordinates(x - 4, y + 2)));
        layer5.add(getTile(new Coordinates(x - 3, y + 3)));
        layer5.add(getTile(new Coordinates(x - 2, y + 4)));
        layer5.add(getTile(new Coordinates(x - 4, y - 2)));
        layer5.add(getTile(new Coordinates(x - 3, y - 3)));
        layer5.add(getTile(new Coordinates(x - 2, y - 4)));
        layer5.add(getTile(new Coordinates(x + 2, y + 4)));
        layer5.add(getTile(new Coordinates(x + 3, y + 3)));
        layer5.add(getTile(new Coordinates(x + 4, y + 2)));
        layer5.add(getTile(new Coordinates(x + 2, y - 4)));
        layer5.add(getTile(new Coordinates(x + 3, y - 3)));
        layer5.add(getTile(new Coordinates(x + 4, y - 2)));

        layers.add(layer5);
        //endregion
        //region layer 6
        List<Tile> layer6 = new ArrayList<>();
        layer6.add(getTile(new Coordinates(x - 4, y + 3)));
        layer6.add(getTile(new Coordinates(x - 3, y + 4)));
        layer6.add(getTile(new Coordinates(x - 4, y - 3)));
        layer6.add(getTile(new Coordinates(x - 3, y - 4)));
        layer6.add(getTile(new Coordinates(x + 3, y + 4)));
        layer6.add(getTile(new Coordinates(x + 4, y + 3)));
        layer6.add(getTile(new Coordinates(x + 3, y - 4)));
        layer6.add(getTile(new Coordinates(x + 4, y - 3)));
        layers.add(layer6);
        //endregion
        //region layer 7
        List<Tile> layer7 = new ArrayList<>();
        layer7.add(getTile(new Coordinates(x - 4, y - 4)));
        layer7.add(getTile(new Coordinates(x - 4, y + 4)));
        layer7.add(getTile(new Coordinates(x + 4, y - 4)));
        layer7.add(getTile(new Coordinates(x + 4, y + 4)));
        layers.add(layer7);
        //endregion
        return layers;
    }
}
