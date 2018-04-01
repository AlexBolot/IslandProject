package fr.unice.polytech.si3.qgl.ise.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum describing every crafted resource, its unique id and its recipe
 */
public enum CraftedResource {
    GLASS("GLASS", "10Qu+5Wo"),
    INGOT("INGOT", "5Or+5Wo"),
    PLANK("PLANK", "0.25Wo"),
    LEATHER("LEATHER", "3Fu"),
    RUM("RUM", "10Su+1Fr");

    private final String id;
    private final String recipe;

    CraftedResource(String id, String recipe) {
        this.id = id;
        this.recipe = recipe;
    }

    public static Map<RawResource, Double> getValueOf(String id) {
        Map<RawResource, Double> returnData = new HashMap<>();
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
        }
        return returnData;
    }

    public static boolean contains(String id) {
        for (CraftedResource c : CraftedResource.values())
            if (c.name().equals(id))
                return true;
        return false;
    }

    public String getId() {
        return id;
    }

    public String getRecipe() {
        return recipe;
    }
}
