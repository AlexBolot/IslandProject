package fr.unice.polytech.si3.qgl.ise.maps;

import fr.unice.polytech.si3.qgl.ise.enums.Biome;
import fr.unice.polytech.si3.qgl.ise.enums.Exploitability;
import fr.unice.polytech.si3.qgl.ise.enums.Abundance;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;

/**
 * A 10*10 pixels tile as seen by the explorer(s), containing the information provided by the "SCOUT", "GLIMPSE" and "EXPLORE" actions.
 */
public class ExplorerTile extends Tile {
    private Map<Biome, Double> biomesPercentage;
    private Map<RawResource, Tuple2<Abundance, Exploitability>> resourcesStats;

    public ExplorerTile() {
        super();
        biomesPercentage = new HashMap<>();
        resourcesStats = new HashMap<>();
    }

    public ExplorerTile(Map<Biome, Double> biomesPercentage, Map<RawResource, Tuple2<Abundance, Exploitability>> resourcesStats) {
        this.biomesPercentage = new HashMap<>(biomesPercentage);
        this.resourcesStats = new HashMap<>(resourcesStats);
    }

    @Override
    Tile makeCopy() {
        ExplorerTile copiedTile = new ExplorerTile();
        copiedTile.biomesPercentage = new HashMap<>(biomesPercentage);
        copiedTile.resourcesStats = new HashMap<>(resourcesStats);
        return copiedTile;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ExplorerTile && ((ExplorerTile) obj).resourcesStats.equals(resourcesStats) && ((ExplorerTile) obj).biomesPercentage.equals(biomesPercentage));
    }

    @Override
    public int hashCode() {
        StringBuilder stringBuilder = new StringBuilder();

        biomesPercentage.forEach((key, value) -> stringBuilder.append(key).append(value));
        resourcesStats.forEach((key, value) -> stringBuilder.append(key).append(value._1).append(value._2));
        stringBuilder.append(getClass());

        return stringBuilder.toString().hashCode();
    }

    public Map<Biome, Double> getBiomesPercentage() {
        return new HashMap<>(biomesPercentage);
    }

    public void setBiomesPercentage(Map<Biome, Double> biomesPercentage) {
        this.biomesPercentage = new HashMap<>(biomesPercentage);
    }

    public Map<RawResource, Tuple2<Abundance, Exploitability>> getResourcesStats() {
        return new HashMap<>(resourcesStats);
    }

    public void setResourcesStats(Map<RawResource, Tuple2<Abundance, Exploitability>> resourcesStats) {
        this.resourcesStats = new HashMap<>(resourcesStats);
    }
}
