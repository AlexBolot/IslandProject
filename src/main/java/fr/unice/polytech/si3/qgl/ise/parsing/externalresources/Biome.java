package fr.unice.polytech.si3.qgl.ise.parsing.externalresources;

import java.util.List;
import java.util.Objects;

public class Biome implements Comparable<Biome> {
    private String name;
    private List<RawResource> possibleResources;

    public Biome(String name, List<RawResource> possibleResources) {
        this.name = name;
        this.possibleResources = possibleResources;
    }

    public String getName() {
        return name;
    }

    public List<RawResource> getResources() {
        return possibleResources;
    }

    public boolean hasResource(RawResource resource) {
        return getResources().contains(resource);
    }

    public boolean sameName(String name) {
        return this.getName().equalsIgnoreCase(name);
    }

    @Override
    public int compareTo(Biome that) {
        boolean thisContainsThat = this.getResources().containsAll(that.getResources());
        boolean thatContainsThis = that.getResources().containsAll(this.getResources());

        if (!thisContainsThat && !thatContainsThis) return -2;
        else return Boolean.compare(thisContainsThat, thatContainsThis);
    }

    @Override
    public String toString() {
        return getName() + " " + getResources().toString();
    }

    @Override
    public boolean equals(Object toCompare) {
        return toCompare instanceof Biome && this.sameName(((Biome) toCompare).getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, possibleResources);
    }

}
