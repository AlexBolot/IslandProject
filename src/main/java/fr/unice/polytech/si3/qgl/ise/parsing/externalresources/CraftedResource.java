package fr.unice.polytech.si3.qgl.ise.parsing.externalresources;

import java.util.Map;
import java.util.Objects;

public class CraftedResource extends Resource implements Comparable<CraftedResource> {

    private final Map<RawResource, Double> recipe;


    CraftedResource(String name, Map<RawResource, Double> recipe) {
        super(name);
        this.recipe = recipe;
    }

    public Map<RawResource, Double> getRecipe() {
        return recipe;
    }

    public boolean requiresResource(RawResource resource) {
        return this.getRecipe().containsKey(resource);
    }

    public double amountRequired(RawResource resource) {
        return (requiresResource(resource)) ? recipe.get(resource) : 0;
    }

    @Override
    public int compareTo(CraftedResource that) {
        boolean thisContainsThat = this.getRecipe().entrySet().containsAll(that.getRecipe().entrySet());
        boolean thatContainsThis = that.getRecipe().entrySet().containsAll(this.getRecipe().entrySet());

        if (!thisContainsThat && !thatContainsThis) return -2;
        else return Boolean.compare(thisContainsThat, thatContainsThis);
    }

    @Override
    public String toString() {
        return super.toString() + " : " + getRecipe().entrySet();
    }

    @Override
    public boolean equals(Object toCompare) {
        return super.equals(toCompare) && toCompare instanceof CraftedResource;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getRecipe());
    }
}
