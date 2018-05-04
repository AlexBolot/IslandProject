package fr.unice.polytech.si3.qgl.ise.parsing.external_resources;

import java.util.List;

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
        return possibleResources.contains(resource);
    }

    @Override
    public String toString() {
        return  name + " " + possibleResources.toString();
    }
}
