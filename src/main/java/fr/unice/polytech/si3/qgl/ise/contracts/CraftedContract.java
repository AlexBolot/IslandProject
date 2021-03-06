package fr.unice.polytech.si3.qgl.ise.contracts;

import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.CraftedResource;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.RawResource;

import java.util.HashMap;
import java.util.Map;


public class CraftedContract {
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
        this.rawQuantities = new HashMap<>();
        this.remainingRawQuantities = new HashMap<>();
        this.remainingRawQuantitiesMinusStock = new HashMap<>();

        //Calculate the total cost in RawResource
        for (Map.Entry<RawResource, Double> cost : resource.getRecipe().entrySet()) {
            rawQuantities.put(cost.getKey(), cost.getValue() * quantity);
        }

        for (Map.Entry<RawResource, Double> cost : resource.getRecipe().entrySet()) {
            remainingRawQuantities.put(cost.getKey(), cost.getValue() * quantity);
            remainingRawQuantitiesMinusStock.put(cost.getKey(), cost.getValue() * quantity);
        }
    }

    public void updateContract(int craftedQuantity) {
        remainingQuantity = remainingQuantity - craftedQuantity;
        if (remainingQuantity <= 0) {
            return;
        }

        for (Map.Entry<RawResource, Double> cost : resource.getRecipe().entrySet()) {
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

    public CraftedResource getResource() {
        return resource;
    }

    public Map<RawResource, Double> getRemainingRawQuantities() {
        return remainingRawQuantities;
    }

    public Map<RawResource, Double> getRemainingRawQuantitiesMinusStock() {
        return remainingRawQuantitiesMinusStock;
    }

    public Map<RawResource, Double> getRawQuantities() {
        return rawQuantities;
    }

    @Override
    public String toString() {
        return quantity + " " + resource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CraftedContract that = (CraftedContract) o;

        if (!quantity.equals(that.quantity)) return false;
        return resource.equals(that.resource);
    }

    @Override
    public int hashCode() {
        int result = quantity.hashCode();
        result = 31 * result + resource.hashCode();
        return result;
    }
}
