package fr.unice.polytech.si3.qgl.ise.enums;

import java.util.EnumMap;
import java.util.Map;

/**
 * Enum describing every crafted resource, its unique id and its recipe
 */
public enum CraftedResource {
    GLASS("GLASS"),
    INGOT("INGOT"),
    PLANK("PLANK"),
    LEATHER("LEATHER"),
    RUM("RUM");

    private final String id;

    CraftedResource(String id) {
        this.id = id;
    }

    public static Map<RawResource, Double> getValueOf(String id) {
        Map<RawResource, Double> returnData = new EnumMap<>(RawResource.class);
        switch (id) {
            case "GLASS":
                returnData.put(RawResource.QUARTZ, 10d);
                returnData.put(RawResource.WOOD, 5d);
                break;
            case "INGOT":
                returnData.put(RawResource.ORE, 5d);
                returnData.put(RawResource.WOOD, 5d);
                break;
            case "PLANK":
                returnData.put(RawResource.WOOD, 0.25d);
                break;
            case "LEATHER":
                returnData.put(RawResource.FUR, 3d);
                break;
            case "RUM":
                returnData.put(RawResource.SUGAR_CANE, 10d);
                returnData.put(RawResource.FRUITS, 1d);
                break;
            default:
                throw new IllegalArgumentException("Resource does not exist");
        }
        return returnData;
    }

    public String getId() {
        return id;
    }
}
