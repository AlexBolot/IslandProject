package fr.unice.polytech.si3.qgl.ise.actions.crew;

import fr.unice.polytech.si3.qgl.ise.entities.Crew;
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
        HashMap<RawResource, Integer> ressourcesWithQuantity = new HashMap<>();
        ressourcesWithQuantity.put(RawResource.WOOD, 100);
        ressourcesWithQuantity.put(RawResource.QUARTZ, 20);
        Transform action = new Transform(new Crew(new IslandMap(), 1, new ArrayList<>(), new ArrayList<>()), ressourcesWithQuantity);
        JSONObject actionString = new JSONObject(action.apply());
        assertEquals("transform", actionString.getString("action"));
        assertEquals(100, actionString.getJSONObject("parameters").getInt(RawResource.WOOD.name()));
        assertEquals(20, actionString.getJSONObject("parameters").getInt(RawResource.QUARTZ.name()));
    }
}
