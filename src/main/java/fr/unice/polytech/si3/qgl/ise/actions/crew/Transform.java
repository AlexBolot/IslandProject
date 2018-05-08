package fr.unice.polytech.si3.qgl.ise.actions.crew;

import fr.unice.polytech.si3.qgl.ise.CraftedContract;
import fr.unice.polytech.si3.qgl.ise.actions.CrewAction;
import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.RawResource;
import org.json.JSONObject;

import java.util.Map;

import static fr.unice.polytech.si3.qgl.ise.parsing.externalresources.ExtResSelector.bundle;

public class Transform extends CrewAction {
    private final Map<RawResource, Integer> resourceWithQuantity;

    public Transform(Crew crewToUpdate, Map<RawResource, Integer> resourceWithQuantity) {
        super(crewToUpdate);
        this.resourceWithQuantity = resourceWithQuantity;
    }

    @Override
    public String apply() {
        for (Map.Entry<RawResource, Integer> entry : resourceWithQuantity.entrySet()) {
            getCrewToUpdate().removeFromStock(entry.getKey(), entry.getValue());
        }
        return new JsonFactory().createJsonString("transform", resourceWithQuantity);
    }

    @Override
    public String acknowledgeResults(String result) {
        finish();
        JSONObject extras = new JSONObject(result).getJSONObject("extras");
        getCrewToUpdate().addToCraftedStock(bundle().getCraftedRes(extras.getString("kind")), extras.getInt("production"));
        for (CraftedContract craftedContract : getCrewToUpdate().getCraftedContracts()) {
            if (craftedContract.getResource().equals(bundle().getCraftedRes(extras.getString("kind")))) {
                craftedContract.updateContract(extras.getInt("production"));
                break;
            }
        }
        return "";
    }
}
