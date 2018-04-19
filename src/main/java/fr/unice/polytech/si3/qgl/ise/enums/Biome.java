package fr.unice.polytech.si3.qgl.ise.enums;

import java.util.Arrays;

import static fr.unice.polytech.si3.qgl.ise.enums.RawResource.*;

/**
 * Enum describing every biome and its unique id
 */
public enum Biome {
    OCEAN(FISH),
    LAKE(FISH),
    BEACH(QUARTZ),
    GRASSLAND(FUR),

    MANGROVE(WOOD, FLOWER),
    TROPICAL_RAIN_FOREST(WOOD, FRUITS, SUGAR_CANE),
    TROPICAL_SEASONAL_FOREST(WOOD, FRUITS, SUGAR_CANE),

    TEMPERATE_DECIDUOUS_FOREST(WOOD),
    TEMPERATE_RAIN_FOREST(WOOD, FUR),
    TEMPERATE_DESERT(QUARTZ, ORE),

    TAIGA(WOOD),
    SNOW(),
    TUNDRA(FUR),
    ALPINE(FLOWER, ORE),
    GLACIER(FLOWER),

    SHRUBLAND(FUR),
    SUB_TROPICAL_DESERT(QUARTZ, ORE);

    /**
     * Unique id of a biome
     */
    private final RawResource[] resources;

    Biome(RawResource... resources) {
        this.resources = resources;
    }

    public RawResource[] getResources() {
        return resources;
    }
}
