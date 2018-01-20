package fr.unice.polytech.si3.qgl.ise.enums;

/**
 * Enum describing every crafted resource, its unique id and its recipe
 */
public enum CraftedResource {
    GLASS("Gl", "10Qu+5Wo"),
    INGOT("In", "5Or+5Wo"),
    PLANK("Pl", "0.25Wo"),
    LEATHER("Le", "3Fu"),
    RUM("Ru", "10Su+1Fr");

    private String id;
    private String recipe;

    CraftedResource(String id, String recipe) {
        this.id = id;
        this.recipe = recipe;
    }

    public String getId() {
        return id;
    }

    public String getRecipe() {
        return recipe;
    }
}
