package fr.unice.polytech.si3.qgl.ise.enums;

/**
 * Enum describing every biome and its unique id
 */
public enum Biome {

    ALPINE("ALP"),
    SNOW("SNO"),
    BEACH("BEA"),
    TROPICAL_RAIN_FOREST("trF"),
    MANGROVE("MAN"),
    TUNDRA("TUN"),
    GRASSLAND("GRA"),
    TROPICAL_SEASONAL_FOREST("trS"),
    TEMPERATE_DESERT("teD"),
    TAIGA("TAI"),
    SUB_TROPICAL_DESERT("STD"),
    TEMPERATE_RAIN_FOREST("teR"),
    SHRUBLAND("SHR"),
    TEMPERATE_DECIDUOUS_FOREST("teF"),
    OCEAN("OCE"),
    LAKE("LAK"),
    GLACIER("GLA");

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
