package fr.unice.polytech.si3.qgl.ise.parsing.external_resources;

import java.util.List;
import java.util.Objects;

public class Biome {
    private String name;
    private List<RawResource> possibleResources;

    public Biome(String name, List<RawResource> possibleResources) {
        this.name = name;
        this.possibleResources = possibleResources;
    }

    public String getName() {
        return name;
    }

    public List<RawResource> getPossibleResources() {
        return possibleResources;
    }

    public boolean hasResource(RawResource resource) {
        return getPossibleResources().contains(resource);
    }

    public boolean isSame(String name) {
        return this.getName().equalsIgnoreCase(name);
    }

    @Override
    public String toString() {
        return getName() + " " + getPossibleResources().toString();
    }

    @Override
    public boolean equals(Object toCompare) {
        if (!(toCompare instanceof Biome)) return false;
        Biome biome = (Biome) toCompare;
        return this.isSame(biome.getName()) && biome.getPossibleResources().equals(getPossibleResources());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, possibleResources);
    }
}
