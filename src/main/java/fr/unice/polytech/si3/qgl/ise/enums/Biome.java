package fr.unice.polytech.si3.qgl.ise.enums;

import java.util.Arrays;

import static fr.unice.polytech.si3.qgl.ise.enums.RawResource.*;

/**
 * Enum describing every biome and its unique id
 */
public enum Biome {
    OCEAN("OCEAN", FISH),
    LAKE("LAKE", FISH),
    BEACH("BEACH", QUARTZ),
    GRASSLAND("GRASSLAND", FUR),

    MANGROVE("MANGROVE", WOOD, FLOWER),
    TROPICAL_RAIN_FOREST("TROPICAL_RAIN_FOREST", WOOD, FRUITS, SUGAR_CANE),
    TROPICAL_SEASONAL_FOREST("TROPICAL_SEASONAL_FOREST", WOOD, FRUITS, SUGAR_CANE),

    TEMPERATE_DECIDUOUS_FOREST("TEMPERATE_DECIDUOUS_FOREST", WOOD),
    TEMPERATE_RAIN_FOREST("TEMPERATE_RAIN_FOREST", WOOD, FUR),
    TEMPERATE_DESERT("TEMPERATE_DESERT", QUARTZ, ORE),

    TAIGA("TAIGA", WOOD),
    SNOW("SNOW"),
    TUNDRA("TUNDRA", FUR),
    ALPINE("ALPINE", FLOWER, ORE),
    GLACIER("GLACIER", FLOWER),

    SHRUBLAND("SHRUBLAND", FUR),
    SUB_TROPICAL_DESERT("SUB_TROPICAL_DESERT", QUARTZ, ORE);

    /**
     * Unique id of a biome
     */
    private final String id;
    private final RawResource[] resources;

    Biome(String id, RawResource... resources) {
        this.id = id;
        this.resources = resources;
    }

    public static Biome getFromId(String id) {
        return Arrays.stream(Biome.values())
                .filter(biome -> biome.id.equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("This biome does not exist"));
    }

    public RawResource[] getResources() {
        return resources;
    }
}
