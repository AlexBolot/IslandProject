package fr.unice.polytech.si3.qgl.ise.parsing.external_resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CraftedResource extends Resource {

    private HashMap<RawResource, Double> recipe;

    public CraftedResource(String name, HashMap<RawResource, Double> recipe) {
        super(name);
        this.recipe = recipe;
    }

    public HashMap<RawResource, Double> getRecipe() {
        return recipe;
    }

    public List<RawResource> getRawRessources() {
        return new ArrayList<>(getRecipe().keySet());
    }

    public boolean requiresResource(RawResource resource) {
        return this.getRecipe().containsKey(resource);
    }

    @Override
    public String toString() {
        return super.toString() + " : " + getRecipe().entrySet();
    }

    @Override
    public boolean equals(Object toCompare) {
        return super.equals(toCompare)
                && toCompare instanceof CraftedResource
                && ((CraftedResource) toCompare).getRecipe().equals(getRecipe());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getRecipe());
    }
}
