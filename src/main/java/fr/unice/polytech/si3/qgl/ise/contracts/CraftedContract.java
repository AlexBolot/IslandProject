package fr.unice.polytech.si3.qgl.ise.contracts;

import fr.unice.polytech.si3.qgl.ise.enums.CraftedResource;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;

import java.util.EnumMap;
import java.util.Map;

public class CraftedContract implements Contract {
    private final Integer quantity;
    private final CraftedResource resource;
    private final Map<RawResource, Double> rawQuantities;
    private final Map<RawResource, Double> remainingRawQuantities;
    private final Map<RawResource, Double> remainingRawQuantitiesMinusStock;
    private int remainingQuantity;

    public CraftedContract(CraftedResource resource, Integer quantity) {
        this.quantity = quantity;
        this.remainingQuantity = quantity;
        this.resource = resource;
        rawQuantities = new EnumMap<>(RawResource.class);
        remainingRawQuantities = new EnumMap<>(RawResource.class);
        remainingRawQuantitiesMinusStock = new EnumMap<>(RawResource.class);

        //Calculate the total cost in RawResource
        for (Map.Entry<RawResource, Double> cost : CraftedResource.getValueOf(resource.getId()).entrySet()) {
            rawQuantities.put(cost.getKey(), cost.getValue() * quantity);
        }
        for (Map.Entry<RawResource, Double> cost : CraftedResource.getValueOf(resource.getId()).entrySet()) {
            remainingRawQuantities.put(cost.getKey(), cost.getValue() * quantity);
            remainingRawQuantitiesMinusStock.put(cost.getKey(), cost.getValue() * quantity);
        }
    }

    public void updateContract(int craftedQuantity) {
        remainingQuantity = remainingQuantity - craftedQuantity;
        if (remainingQuantity <= 0) {
            return;
        }
        for (Map.Entry<RawResource, Double> cost : CraftedResource.getValueOf(resource.getId()).entrySet()) {
            remainingRawQuantities.put(cost.getKey(), cost.getValue() * remainingQuantity);
            remainingRawQuantitiesMinusStock.put(cost.getKey(), cost.getValue() * remainingQuantity);
        }
    }

    public void updateRemainingQuantityMinusStock(RawResource resource, int stock) {
        if (remainingRawQuantities.containsKey(resource))
            remainingRawQuantitiesMinusStock.put(resource, (remainingRawQuantities.get(resource) - stock) > 0 ? remainingRawQuantities.get(resource) - stock : 0);
    }

    public Integer getQuantity() {
        return quantity;
    }

    int getRemainingQuantity() {
        return remainingQuantity;
    }

    public CraftedResource getResource() {
        return resource;
    }

    public Map<RawResource, Double> getRemainingRawQuantities() {
        return remainingRawQuantities;
    }

    public Map<RawResource, Double> getRemainingRawQuantitiesMinusStock() {
        return remainingRawQuantitiesMinusStock;
    }

    @Override
    public Map<RawResource, Double> getTotalResourcesToCollect() {
        return rawQuantities;
    }

    @Override
    public String toString() {
        return quantity + " " + resource;
    }
}
