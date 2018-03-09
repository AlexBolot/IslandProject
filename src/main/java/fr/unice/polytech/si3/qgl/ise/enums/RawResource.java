package fr.unice.polytech.si3.qgl.ise.enums;

/**
 * Enum describing every raw resource and its unique id
 */
public enum RawResource {
    FISH("FISH"),
    QUARTZ("QUARTZ"),
    ORE("ORE"),
    WOOD("WOOD"),
    FRUITS("FRUITS"),
    SUGAR_CANE("SUGAR_CANE"),
    FLOWER("FLOWER"),
    FUR("FUR");

    private String id;

    RawResource(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static boolean contains(String id) {
        for (RawResource c : RawResource.values())
            if (c.name().equals(id))
                return true;
        return false;
    }
}
