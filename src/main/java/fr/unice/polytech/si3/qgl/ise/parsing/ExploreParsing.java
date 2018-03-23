package fr.unice.polytech.si3.qgl.ise.parsing;

import fr.unice.polytech.si3.qgl.ise.enums.Abundance;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Parses the data obtained through the echo command
 */
public class ExploreParsing {
    private int cost;
    private HashMap<RawResource, Abundance> resources;

    public ExploreParsing(String exploreResult) {
        resources = new HashMap<>();

        JSONObject data = new JSONObject(exploreResult);
        cost = data.getInt("cost");
        JSONObject extras = data.getJSONObject("extras");
        JSONArray resourcesJson = extras.getJSONArray("resources");
        for (int i = 0; i < resourcesJson.length(); i++) {
            JSONObject temp = (JSONObject) resourcesJson.get(i);
            resources.put(RawResource.valueOf(temp.getString("resource")), Abundance.valueOf(temp.getString("amount")));
            //need to add cond when needed
        }
    }

    public int getCost() {
        return cost;
    }

    public HashMap<RawResource, Abundance> getResources() {
        return resources;
    }
}
