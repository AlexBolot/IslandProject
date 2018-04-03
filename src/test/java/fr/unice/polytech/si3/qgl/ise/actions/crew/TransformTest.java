package fr.unice.polytech.si3.qgl.ise.actions.crew;

import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.enums.CraftedResource;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class TransformTest {

    @Test
    public void testApply() {
        HashMap<RawResource, Integer> resourcesWithQuantity = new HashMap<>();
        resourcesWithQuantity.put(RawResource.WOOD, 100);
        resourcesWithQuantity.put(RawResource.QUARTZ, 20);
        Transform action = new Transform(new Crew(new IslandMap(), new ArrayList<>(), new ArrayList<>()), resourcesWithQuantity);
        JSONObject actionString = new JSONObject(action.apply());
        assertEquals("transform", actionString.getString("action"));
        assertEquals(100, actionString.getJSONObject("parameters").getInt(RawResource.WOOD.name()));
        assertEquals(20, actionString.getJSONObject("parameters").getInt(RawResource.QUARTZ.name()));
    }

    @Test
    public void testAcknowledgeResults() {
        HashMap<RawResource, Integer> resourcesWithQuantity = new HashMap<>();
        Crew crew = new Crew(new IslandMap(), new ArrayList<>(), new ArrayList<>());
        Transform action = new Transform(crew, resourcesWithQuantity);
        action.acknowledgeResults("{ \"cost\": 5, \"extras\": { \"production\": 1, \"kind\": \"GLASS\" },\"status\": \"OK\" }");
        assertEquals(1, crew.getCraftedResourceQuantity(CraftedResource.GLASS));
    }
}
