package fr.unice.polytech.si3.qgl.ise.factories;

import org.json.JSONObject;

import java.util.Map;

public class JsonFactory {

    /**
     * Create a JSON formatted string doing action with parameters
     *
     * @param functionName      action name
     * @param parameterAndValue name of parameter then value
     * @return String matching the action requested
     */
    public String createJsonString(String functionName, String... parameterAndValue) {

        if (parameterAndValue.length % 2 != 0)
            throw new IllegalArgumentException("The parameters name and value must be in same number");

        JSONObject jsonReturn = new JSONObject();
        jsonReturn.put("action", functionName);

        if (parameterAndValue.length > 0) {

            JSONObject params = new JSONObject();

            for (int i = 0; i < parameterAndValue.length - 1; i += 2) {

                params.put(parameterAndValue[i], parameterAndValue[i + 1]);
            }

            jsonReturn.put("parameters", params);
        }

        return jsonReturn.toString();
    }

    public String createJsonString(String functionName, Map<?, ?> parameterAndValue) {

        if (parameterAndValue == null)
            throw new IllegalArgumentException("The parameters name and value are null");

        JSONObject jsonReturn = new JSONObject();
        jsonReturn.put("action", functionName);

        JSONObject params = new JSONObject();

        for (Map.Entry map : parameterAndValue.entrySet()) {

            params.put(map.getKey().toString(), map.getValue().toString());
        }

        jsonReturn.put("parameters", params);

        return jsonReturn.toString();
    }
}
