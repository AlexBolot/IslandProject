package fr.unice.polytech.si3.qgl.ise;

import fr.unice.polytech.si3.qgl.ise.enums.CraftedResource;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Contract {
    private int men;
    private String heading;
    private int actions;
    private Map<RawResource, Integer> totalToCollect;
    private Map<CraftedResource, Integer> withCraftedRessource;

    public Contract(String stringToParse) {
        totalToCollect = new HashMap<>();
        withCraftedRessource = new HashMap<>();
        JSONObject data = new JSONObject(stringToParse);
        men = data.getInt("men");
        heading = data.getString("heading");
        actions = data.getInt("budget");
        parseContracts(data.getJSONArray("contracts"));
    }

    private void parseContracts(JSONArray contracts) {
        for (int i = 0; i < contracts.length(); ++i) {
            JSONObject obj = contracts.getJSONObject(i);
            if (RawResource.contains(obj.getString("resource"))) {
                addToTotal(RawResource.valueOf(obj.getString("resource")), obj.getInt("amount"));
            } else {
                parseCraftedRessource(obj);
            }
        }
    }

    private void parseCraftedRessource(JSONObject craftedRessource) {
        CraftedResource.valueOf(craftedRessource.getString("resource"));
        if (CraftedResource.contains(craftedRessource.getString("resource"))) {
            Map<RawResource, Double> values = CraftedResource.getValueOf(craftedRessource.getString("resource"));
            withCraftedRessource.put(CraftedResource.valueOf(craftedRessource.getString("resource")), craftedRessource.getInt("amount"));
            for (Map.Entry<RawResource, Double> val : values.entrySet()) {
                addToTotal(val.getKey(), (int) (val.getValue() * craftedRessource.getInt("amount")));
            }
        } else {
            throw new IllegalArgumentException("This resource isn't in the enum");
        }
    }

    private void addToTotal(RawResource resource, int quantity) {
        if (totalToCollect.containsKey(resource))
            totalToCollect.put(resource, totalToCollect.get(resource) + quantity);
        else
            totalToCollect.put(resource, quantity);
    }

    public int getMen() {
        return men;
    }

    public int getBudget() {
        return actions;
    }

    public Map<RawResource, Integer> getTotalToCollect() {
        return totalToCollect;
    }

    public Map<CraftedResource, Integer> getCraftedRessource() {
        return withCraftedRessource;
    }

    public String getHeading() {
        return heading;
    }

    private class subContract {
        private RawResource ressource;
        private int quantitiy;

        public subContract(RawResource ressource, int quantitiy) {
            this.ressource = ressource;
            this.quantitiy = quantitiy;
        }

    }

    public int getAmountOf(RawResource ressource) {
        return totalToCollect.get(ressource);
    }
}
