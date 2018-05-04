package fr.unice.polytech.si3.qgl.ise.parsing.external_resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CraftedResource {

    private String name;
    private HashMap<RawResource, Double> recipe;

    public CraftedResource(String name, HashMap<RawResource, Double> recipe) {
        this.name = name;
        this.recipe = recipe;
    }

    public String getName() {
        return name;
    }

    public HashMap<RawResource, Double> getRecipe() {
        return recipe;
    }

    public List<RawResource> getRawRessources() {
        return new ArrayList<>(recipe.keySet());
    }

    public boolean requiresResource(RawResource resource) {
        return this.recipe.containsKey(resource);
    }

    @Override
    public String toString() {
        return name + " : " + recipe.entrySet();
    }

    @Override
    public boolean equals(Object toCompare) {
        if (toCompare instanceof String) return name.equalsIgnoreCase((String) toCompare);
        CraftedResource that = (CraftedResource) toCompare;
        return Objects.equals(name, that.name) && Objects.equals(recipe, that.recipe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
