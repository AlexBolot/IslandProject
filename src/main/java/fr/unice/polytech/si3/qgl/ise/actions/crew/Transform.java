package fr.unice.polytech.si3.qgl.ise.actions.crew;

import fr.unice.polytech.si3.qgl.ise.actions.CrewAction;
import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.enums.CraftedResource;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import org.json.JSONObject;

import java.util.Map;

public class Transform extends CrewAction {
    private final Map<RawResource, Integer> resourceWithQuantity;
    private int produced;

    public Transform(Crew crewToUpdate, Map<RawResource, Integer> resourceWithQuantity) {
        super(crewToUpdate);
        this.resourceWithQuantity = resourceWithQuantity;
    }

    @Override
    public String apply() {
        for (RawResource rawResource : resourceWithQuantity.keySet()) {
            getCrewToUpdate().removeFromStock(rawResource, resourceWithQuantity.get(rawResource));
        }
        return new JsonFactory().createJsonString("transform", resourceWithQuantity);
    }

    @Override
    public String acknowledgeResults(String result) {
        finish();
        JSONObject extras = new JSONObject(result).getJSONObject("extras");
        produced = extras.getInt("production");
        getCrewToUpdate().addToCraftedStock(CraftedResource.valueOf(extras.getString("kind")), extras.getInt("production"));
        return "";
    }

    public int getProduced() {
        return produced;
    }
}
