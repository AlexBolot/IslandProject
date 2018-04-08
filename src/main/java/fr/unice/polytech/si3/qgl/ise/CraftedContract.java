package fr.unice.polytech.si3.qgl.ise;

import fr.unice.polytech.si3.qgl.ise.enums.CraftedResource;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;

import java.util.EnumMap;
import java.util.Map;

public class CraftedContract {
    private final Integer quantity;
    private final CraftedResource resource;
    private final Map<RawResource, Double> rawQuantities;
    private int remainingQuantity;
    private Map<RawResource, Double> remainingRawQuantities;

    public CraftedContract(CraftedResource resource, Integer quantity) {
        this.quantity = quantity;
        this.remainingQuantity = quantity;
        this.resource = resource;
        rawQuantities = new EnumMap<>(RawResource.class);
        remainingRawQuantities = new EnumMap<>(RawResource.class);

        //Calculate the total cost in RawResource
        for (Map.Entry<RawResource, Double> cost : CraftedResource.getValueOf(resource.getId()).entrySet()) {
            rawQuantities.put(cost.getKey(), cost.getValue() * quantity);
        }
        for (Map.Entry<RawResource, Double> cost : CraftedResource.getValueOf(resource.getId()).entrySet()) {
            remainingRawQuantities.put(cost.getKey(), cost.getValue() * quantity);
        }
    }

    public boolean updateContract(int craftedQuantity) {
        remainingQuantity = remainingQuantity - craftedQuantity;
        if (remainingQuantity <= 0) {
            return true;
        }
        for (Map.Entry<RawResource, Double> cost : CraftedResource.getValueOf(resource.getId()).entrySet()) {
            remainingRawQuantities.put(cost.getKey(), cost.getValue() * remainingQuantity);
        }
        return false;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public CraftedResource getResource() {
        return resource;
    }

    public Map<RawResource, Double> getRawQuantities() {
        return rawQuantities;
    }

    public Map<RawResource, Double> getRemainingRawQuantities() {
        return remainingRawQuantities;
    }

    @Override
    public String toString() {
        return quantity + " " + resource;
    }
}
