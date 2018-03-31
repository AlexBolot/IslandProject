package fr.unice.polytech.si3.qgl.ise;

import fr.unice.polytech.si3.qgl.ise.enums.CraftedResource;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;

import java.util.HashMap;
import java.util.Map;

public class CraftedContract {
    private Integer quantity;
    private CraftedResource resource;
    private Map<RawResource, Double> rawQuantities;

    public CraftedContract(CraftedResource resource, Integer quantity) {
        this.quantity = quantity;
        this.resource = resource;
        rawQuantities = new HashMap<>();

        //Calculate the total cost in RawRessource
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
}
