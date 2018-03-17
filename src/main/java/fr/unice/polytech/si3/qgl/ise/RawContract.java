package fr.unice.polytech.si3.qgl.ise;

import fr.unice.polytech.si3.qgl.ise.enums.RawResource;

public class RawContract {
    Integer quantity;
    RawResource resource;

    public RawContract(RawResource resource, Integer quantity) {
        this.quantity = quantity;
        this.resource = resource;
    }

    public int getQuantity() {
        return quantity;
    }

    public RawResource getResource() {
        return resource;
    }
}
