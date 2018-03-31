package fr.unice.polytech.si3.qgl.ise.parsing;

import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import org.json.JSONObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JsonFactoryTest {

    @Test
    public void testJsonParseStrings() {
        String string = new JsonFactory().createJsonString("action", "param1", "value1", "param2", "value2");
        JSONObject jsonObject = new JSONObject(string);
        assertEquals("action", jsonObject.getString("action"));
        assertEquals("value1", jsonObject.getJSONObject("parameters").getString("param1"));
        assertEquals("value2", jsonObject.getJSONObject("parameters").getString("param2"));
    }

    @Test
    public void testJsonParseFromMap() {
        Map<String, String> params = new HashMap<>();
        params.put("param1", "value1");
        params.put("param2", "value2");
        params.put("param3", "value3");
        String string = new JsonFactory().createJsonString("action", params);
        JSONObject jsonObject = new JSONObject(string);
        assertEquals("action", jsonObject.getString("action"));
        assertEquals("value1", jsonObject.getJSONObject("parameters").getString("param1"));
        assertEquals("value2", jsonObject.getJSONObject("parameters").getString("param2"));
        assertEquals("value3", jsonObject.getJSONObject("parameters").getString("param3"));
    }
}
