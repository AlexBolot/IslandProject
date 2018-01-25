package fr.unice.polytech.si3.qgl.ise.map;

import fr.unice.polytech.si3.qgl.ise.enums.Abundance;
import fr.unice.polytech.si3.qgl.ise.enums.Biome;
import fr.unice.polytech.si3.qgl.ise.enums.Exploitability;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;
import scala.Tuple2;

import java.util.*;

public class Tile {
    private List<Biome> possibleBiomes;
    private Map<Biome, Double> biomesPercentage;
    private Map<RawResource, Tuple2<Abundance, Exploitability>> resourcesStats;

    public Tile() {
        possibleBiomes = new ArrayList<>();
        biomesPercentage = new EnumMap<>(Biome.class);
        resourcesStats = new EnumMap<>(RawResource.class);
    }

    public Tile(Tile tile) {
        possibleBiomes = new ArrayList<>(tile.possibleBiomes);
        biomesPercentage = new EnumMap<>(tile.biomesPercentage);
        resourcesStats = new EnumMap<>(tile.resourcesStats);
    }

    public Tile(List<Biome> possibleBiomes) {
        this.possibleBiomes = new ArrayList<>(possibleBiomes);
        biomesPercentage = new EnumMap<>(Biome.class);
        resourcesStats = new EnumMap<>(RawResource.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return Objects.equals(possibleBiomes, tile.possibleBiomes) &&
                Objects.equals(biomesPercentage, tile.biomesPercentage) &&
                Objects.equals(resourcesStats, tile.resourcesStats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(possibleBiomes, biomesPercentage, resourcesStats);
    }

    public List<Biome> getPossibleBiomes() {
        return possibleBiomes;
    }

    public void setPossibleBiomes(List<Biome> possibleBiomes) {
        this.possibleBiomes = possibleBiomes;
    }

    public Map<Biome, Double> getBiomesPercentage() {
        return biomesPercentage;
    }

    public void setBiomesPercentage(Map<Biome, Double> biomesPercentage) {
        this.biomesPercentage = biomesPercentage;
    }

    public Map<RawResource, Tuple2<Abundance, Exploitability>> getResourcesStats() {
        return resourcesStats;
    }

    public void setResourcesStats(Map<RawResource, Tuple2<Abundance, Exploitability>> resourcesStats) {
        this.resourcesStats = resourcesStats;
    }
}
