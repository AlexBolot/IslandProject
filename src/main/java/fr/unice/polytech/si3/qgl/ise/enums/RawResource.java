package fr.unice.polytech.si3.qgl.ise.enums;

/**
 * Enum describing every raw resource and its unique id
 */
public enum RawResource {
    FISH("Fi"),
    QUARTZ("Qu"),
    ORE("Or"),
    WOOD("Wo"),
    FRUITS("Fr"),
    SUGAR_CANE("Su"),
    FLOWER("Fl"),
    FUR("Fu");

    private String id;

    RawResource(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
