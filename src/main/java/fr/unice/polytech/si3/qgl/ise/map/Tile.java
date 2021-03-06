package fr.unice.polytech.si3.qgl.ise.map;

import fr.unice.polytech.si3.qgl.ise.enums.Abundance;
import fr.unice.polytech.si3.qgl.ise.enums.Exploitability;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.Biome;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.RawResource;
import scala.Tuple2;

import java.util.*;

public class Tile {
    private final Map<Biome, Double> biomesPercentage;
    private Map<RawResource, Tuple2<Abundance, Exploitability>> resourcesStats;
    private boolean isExplored = false;

    Tile() {
        biomesPercentage = new HashMap<>();
        resourcesStats = new HashMap<>();
    }

    Tile(Map<Biome, Double> possibleBiomes) {
        biomesPercentage = possibleBiomes;
        resourcesStats = new HashMap<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return Objects.equals(biomesPercentage, tile.biomesPercentage) &&
                Objects.equals(resourcesStats, tile.resourcesStats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(biomesPercentage, resourcesStats);
    }

    public List<Biome> getPossibleBiomes() {
        return new ArrayList<>(biomesPercentage.keySet());
    }

    public Map<Biome, Double> getBiomesPercentage() {
        return biomesPercentage;
    }

    public double getBiomePercentage(Biome biome) {
        return biomesPercentage.containsKey(biome) ? biomesPercentage.get(biome) : 0;
    }

    public void addBiomesPercentage(Map<Biome, Double> biomesPercentage) {
        for (Map.Entry<Biome, Double> biome : biomesPercentage.entrySet()) {
            if (this.biomesPercentage.containsKey(biome.getKey())) {
                double oldValue = this.biomesPercentage.get(biome.getKey());
                double layerProb = biome.getValue();
                double increment = (((100d - oldValue) * layerProb) / 100d);
                this.biomesPercentage.put(biome.getKey(), oldValue + increment);
            } else {
                this.biomesPercentage.put(biome.getKey(), biome.getValue());
            }
        }
    }

    public Map<RawResource, Tuple2<Abundance, Exploitability>> getResourcesStats() {
        return resourcesStats;
    }

    public void setResourcesStats(Map<RawResource, Tuple2<Abundance, Exploitability>> resourcesStats) {
        this.resourcesStats = resourcesStats;
    }

    public boolean isExplored() {
        return isExplored;
    }

    public void setExplored(boolean explored) {
        isExplored = explored;
    }
}
