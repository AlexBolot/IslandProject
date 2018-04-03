package fr.unice.polytech.si3.qgl.ise;

import fr.unice.polytech.si3.qgl.ise.enums.CraftedResource;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;

import java.util.EnumMap;
import java.util.Map;

public class CraftedContract {
    private final Integer quantity;
    private final CraftedResource resource;
    private final Map<RawResource, Double> rawQuantities;

    public CraftedContract(CraftedResource resource, Integer quantity) {
        this.quantity = quantity;
        this.resource = resource;
        rawQuantities = new EnumMap<>(RawResource.class);

        //Calculate the total cost in RawResource
        for (Map.Entry<RawResource, Double> cost : CraftedResource.getValueOf(resource.getId()).entrySet()) {
            rawQuantities.put(cost.getKey(), cost.getValue() * quantity);
        }
    }

    public Integer getQuantity() {
        return quantity;
    }

    public CraftedResource getResource() {
        return resource;
    }

    public Map<RawResource, Double> getRawQuantities() {
        return rawQuantities;
    }

    @Override
    public String toString() {
        return quantity + " " + resource;
    }
}
