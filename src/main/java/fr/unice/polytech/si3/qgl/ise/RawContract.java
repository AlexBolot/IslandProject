package fr.unice.polytech.si3.qgl.ise;

import fr.unice.polytech.si3.qgl.ise.enums.RawResource;

import java.util.HashMap;
import java.util.Map;

public class RawContract implements Contract {
    private final Integer quantity;
    private final RawResource resource;
    private int remainingQuantity;

    public RawContract(RawResource resource, Integer quantity) {
        this.quantity = quantity;
        this.remainingQuantity = quantity;
        this.resource = resource;
    }

    public void updateRemainingQuantity(int stock) {
        remainingQuantity = (quantity - stock) > 0 ? quantity - stock : 0;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public RawResource getResource() {
        return resource;
    }

    @Override
    public String toString() {
        return quantity + " " + resource;
    }

    public Map<RawResource, Double> getTotalRessourcesToCollect() {
        HashMap<RawResource, Double> result = new HashMap<>();
        result.put(resource, (double) remainingQuantity);
        return result;
    }
}
