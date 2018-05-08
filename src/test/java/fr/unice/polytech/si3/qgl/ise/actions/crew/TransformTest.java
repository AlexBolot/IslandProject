package fr.unice.polytech.si3.qgl.ise.actions.crew;

import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.RawResource;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static fr.unice.polytech.si3.qgl.ise.parsing.externalresources.ExtResSelector.bundle;
import static org.junit.Assert.assertEquals;

public class TransformTest {

    @Test
    public void testApply() {
        HashMap<RawResource, Integer> resourcesWithQuantity = new HashMap<>();
        resourcesWithQuantity.put(bundle().getRawRes("WOOD"), 100);
        resourcesWithQuantity.put(bundle().getRawRes("QUARTZ"), 20);
        Transform action = new Transform(new Crew(new IslandMap(), new ArrayList<>(), new ArrayList<>()), resourcesWithQuantity);
        JSONObject actionString = new JSONObject(action.apply());
        assertEquals("transform", actionString.getString("action"));
        assertEquals(100, actionString.getJSONObject("parameters").getInt(bundle().getRawRes("WOOD").getName()));
        assertEquals(20, actionString.getJSONObject("parameters").getInt(bundle().getRawRes("QUARTZ").getName()));
    }

    @Test
    public void testAcknowledgeResults() {
        HashMap<RawResource, Integer> resourcesWithQuantity = new HashMap<>();
        Crew crew = new Crew(new IslandMap(), new ArrayList<>(), new ArrayList<>());
        Transform action = new Transform(crew, resourcesWithQuantity);
        action.acknowledgeResults("{ \"cost\": 5, \"extras\": { \"production\": 1, \"kind\": \"GLASS\" },\"status\": \"OK\" }");
        assertEquals(1, crew.getCraftedResourceQuantity(bundle().getCraftedRes("GLASS")));
    }
}
