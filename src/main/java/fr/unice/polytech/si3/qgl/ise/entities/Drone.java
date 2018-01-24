package fr.unice.polytech.si3.qgl.ise.entities;

import fr.unice.polytech.si3.qgl.ise.maps.Coordinates;
import org.json.JSONObject;

public class Drone {
    private Coordinates coords;
    private boolean isFlying;
    private String orientation;

    private String lastAction;

    public Drone() {
        coords = new Coordinates(0, 0);
        isFlying = true;
    }

    /**
     * Create a JSON formatted string doing action with parameters
     *
     * @param functionName      action name
     * @param parameterAndValue name of parameter then value
     * @return String matching the action requested
     */
    private String createFunctionWithParams(String functionName, String... parameterAndValue) {
        if (parameterAndValue.length % 2 != 0)
            throw new IllegalArgumentException("The parameters name and value must be in same number");
        JSONObject jsonReturn = new JSONObject();
        jsonReturn.put("action", functionName);
        if (parameterAndValue.length > 0) {
            JSONObject params = new JSONObject();
            for (int i = 0; i < parameterAndValue.length - 1; i += 2) {
                params.put(parameterAndValue[i], parameterAndValue[i + 1]);
            }
            jsonReturn.put("parameters", parameterAndValue);
        }
        return jsonReturn.toString();
    }

    private String fly() {
        return createFunctionWithParams("fly");
    }

    private String heading(String direction) {
        lastAction = "heading";
        return createFunctionWithParams("heading", "direction", direction);
    }

    private String echo(String direction) {
        lastAction = "echo";
        return createFunctionWithParams("echo", "direction", direction);
    }

    private String scan(String direction) {
        lastAction = "scan";
        return createFunctionWithParams("scan");
    }

    public String takeDecision() {
        //TODO Function body
        return "";
    }

    //region ===== Getters =====

    public boolean isFlying() {
        return isFlying;
    }

    //endregion
}