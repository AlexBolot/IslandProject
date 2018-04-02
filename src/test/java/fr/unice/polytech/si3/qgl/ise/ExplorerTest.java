package fr.unice.polytech.si3.qgl.ise;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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

        assertNotEquals("{\"action\":\"stop\"}", explorer.takeDecision());

        explorer.acknowledgeResults("vbyufus");

        assertEquals("{\"action\":\"stop\"}", explorer.takeDecision());
    }

    @Test
    public void reportNotReadyTest() {
        Explorer explorer = new Explorer();

        assertEquals("Did everyone see that? Because we will not be doing it again!", explorer.deliverFinalReport());
    }

    @Test
    public void OzTest() {
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

        assertEquals("{\"action\":\"echo\",\"parameters\":{\"direction\":\"N\"}}", explorer.takeDecision());

        explorer.acknowledgeResults("{ \"cost\": 1, \"extras\": { \"range\": 0, \"found\": \"GROUND\" }, \"status\": \"OK\" }");

        assertEquals("{\"action\":\"echo\",\"parameters\":{\"direction\":\"E\"}}", explorer.takeDecision());

        explorer.acknowledgeResults("{ \"cost\": 1, \"extras\": { \"range\": 0, \"found\": \"GROUND\" }, \"status\": \"OK\" }");

        assertEquals("{\"action\":\"echo\",\"parameters\":{\"direction\":\"W\"}}", explorer.takeDecision());

        explorer.acknowledgeResults("{ \"cost\": 1, \"extras\": { \"range\": 0, \"found\": \"GROUND\" }, \"status\": \"OK\" }");

        assertEquals("{\"action\":\"stop\"}", explorer.takeDecision());
    }
}
