package fr.unice.polytech.si3.qgl.ise.actions.crew;

import fr.unice.polytech.si3.qgl.ise.actions.CrewAction;
import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.enums.CraftedResource;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import org.json.JSONObject;

import java.util.HashMap;

public class Transform extends CrewAction {
    private final HashMap<RawResource, Integer> resourceWithQuantity;
    private int producted;

    public Transform(Crew crewToUpdate, HashMap<RawResource, Integer> resourceWithQuantity) {
        super(crewToUpdate);
        this.resourceWithQuantity = resourceWithQuantity;
    }

    @Override
    public String apply() {
        return new JsonFactory().createJsonString("transform", resourceWithQuantity);
    }

    @Override
    public String acknowledgeResults(String result) {
        finish();
        JSONObject extras = new JSONObject(result).getJSONObject("extras");
        producted = extras.getInt("production");
        getCrewToUpdate().addToCraftedStock(CraftedResource.valueOf(extras.getString("kind")), extras.getInt("production"));
        return "";
    }

    public int getProducted() {
        return producted;
    }
}
