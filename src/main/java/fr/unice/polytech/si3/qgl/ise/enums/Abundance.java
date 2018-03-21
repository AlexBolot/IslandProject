package fr.unice.polytech.si3.qgl.ise.enums;

/**
 * Enum describing the abundance of a resource in a tile
 */
public enum Abundance {

    HIGH("HIGH"),
    MEDIUM("MEDIUM"),
    LOW("LOW");

    private String id;

    Abundance(String id) {
        this.id = id;
    }
}
