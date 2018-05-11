package fr.unice.polytech.si3.qgl.ise.parsing.externalresources;

import java.util.Objects;

public abstract class Resource {

    private String name;

    Resource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean sameName(String name) {
        return this.getName().equalsIgnoreCase(name);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object toCompare) {
        return (toCompare instanceof Resource) && sameName(((Resource) toCompare).getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
