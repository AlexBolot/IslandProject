package fr.unice.polytech.si3.qgl.ise.enums;

/**
 * Enum describing every biome and its unique id
 */
public enum Biome {

    ALPINE("ALPINE"),
    SNOW("SNOW"),
    BEACH("BEACH"),
    TROPICAL_RAIN_FOREST("TROPICAL_RAIN_FOREST"),
    MANGROVE("MANGROVE"),
    TUNDRA("TUNDRA"),
    GRASSLAND("GRASSLAND"),
    TROPICAL_SEASONAL_FOREST("TROPICAL_SEASONAL_FOREST"),
    TEMPERATE_DESERT("TEMPERATE_DESERT"),
    TAIGA("TAIGA"),
    SUB_TROPICAL_DESERT("SUB_TROPICAL_DESERT"),
    TEMPERATE_RAIN_FOREST("TEMPERATE_RAIN_FOREST"),
    SHRUBLAND("SHRUBLAND"),
    TEMPERATE_DECIDUOUS_FOREST("TEMPERATE_DECIDUOUS_FOREST"),
    OCEAN("OCEAN"),
    LAKE("LAKE"),
    GLACIER("GLACIER");

    /**
     * Unique id of a biome
     */
    private String id;

    Biome(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
