package fr.unice.polytech.si3.qgl.ise.parsing;

import fr.unice.polytech.si3.qgl.ise.enums.Abundance;
import fr.unice.polytech.si3.qgl.ise.enums.Exploitability;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.RawResource;
import org.json.JSONArray;
import org.json.JSONObject;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;

import static fr.unice.polytech.si3.qgl.ise.parsing.externalresources.ExtResSelector.bundle;

/**
 * Parses the data obtained through the echo command
 */
public class ExploreParsing {
    private final Map<RawResource, Tuple2<Abundance, Exploitability>> resources;

    public ExploreParsing(String exploreResult) {
        resources = new HashMap<>();

        JSONObject data = new JSONObject(exploreResult);
        JSONObject extras = data.getJSONObject("extras");
        JSONArray resourcesJson = extras.getJSONArray("resources");
        for (int i = 0; i < resourcesJson.length(); i++) {
            JSONObject temp = (JSONObject) resourcesJson.get(i);
            resources.put(bundle().getRawRes(temp.getString("resource")), new Tuple2<>(Abundance.valueOf(temp.getString("amount")), Exploitability.valueOf(temp.getString("cond"))));
            //need to add cond when needed
        }
    }

    public Map<RawResource, Tuple2<Abundance, Exploitability>> getResources() {
        return resources;
    }
}
