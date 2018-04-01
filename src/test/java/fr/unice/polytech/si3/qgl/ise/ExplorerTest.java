package fr.unice.polytech.si3.qgl.ise;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExplorerTest {

    @Test
    public void wrongContractTest() {
        Explorer explorer = new Explorer();

        explorer.initialize("vbhrukebkv");

        assertEquals("{\"action\":\"stop\"}", explorer.takeDecision());
    }

    @Test
    public void wrongResultTest() {
        Explorer explorer = new Explorer();

        explorer.initialize("{\n" +
                "    \"heading\": \"N\",\n" +
                "    \"men\": 15,\n" +
                "    \"contracts\": [\n" +
                "      {\n" +
                "        \"amount\": 2,\n" +
                "        \"resource\": \"RUM\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"amount\": 400,\n" +
                "        \"resource\": \"QUARTZ\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"amount\": 100,\n" +
                "        \"resource\": \"FUR\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"amount\": 2000,\n" +
                "        \"resource\": \"WOOD\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"budget\": 30000\n" +
                "}");

        explorer.takeDecision();

        explorer.acknowledgeResults("vbyufus");

        assertEquals("{\"action\":\"stop\"}", explorer.takeDecision());
    }
}
