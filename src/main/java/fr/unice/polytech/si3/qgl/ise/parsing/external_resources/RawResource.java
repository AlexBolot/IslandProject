package fr.unice.polytech.si3.qgl.ise.parsing.external_resources;

import java.util.Objects;

public class RawResource {

    private String name;

    public RawResource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object toCompare) {
        if (toCompare instanceof String) return name.equalsIgnoreCase((String) toCompare);
        return (toCompare instanceof RawResource) && ((RawResource) toCompare).name.equalsIgnoreCase(name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}