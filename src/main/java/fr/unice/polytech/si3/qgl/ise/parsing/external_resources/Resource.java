package fr.unice.polytech.si3.qgl.ise.parsing.external_resources;

import java.util.Objects;

public abstract class Resource {

    private String name;

    public Resource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isSame(String name) {
        return this.getName().equalsIgnoreCase(name);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object toCompare) {
        return (toCompare instanceof Resource) && isSame(((Resource) toCompare).getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
