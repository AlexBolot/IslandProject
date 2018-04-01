package fr.unice.polytech.si3.qgl.ise.enums;

import java.util.Arrays;

/**
 * Enum describing the abundance of a resource in a tile
 */
public enum Abundance {

    HIGH("HIGH"),
    MEDIUM("MEDIUM"),
    LOW("LOW");

    private final String id;

    Abundance(String id) {
        this.id = id;
    }

    public Abundance getFromId(String id) {
        return Arrays.stream(Abundance.values())
                .filter(abundance -> abundance.id.equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("This abundance does not exist"));
    }
}
