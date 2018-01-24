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

    private String fly() {
        JSONObject jsonReturn = new JSONObject();
        jsonReturn.put("action", "fly");
        lastAction = "fly";
        return jsonReturn.toString();
    }

    private String heading(String direction) {
        JSONObject jsonReturn = new JSONObject();
        jsonReturn.put("action", "heading");
        jsonReturn.put("parameters", new JSONObject().put("direction", direction));
        lastAction = "heading";
        return jsonReturn.toString();
    }

    private String echo(String direction) {
        JSONObject jsonReturn = new JSONObject();
        jsonReturn.put("action", "echo");
        jsonReturn.put("parameters", new JSONObject().put("direction", direction));
        lastAction = "echo";
        return jsonReturn.toString();
    }

    private String scan(String direction) {
        JSONObject jsonReturn = new JSONObject();
        jsonReturn.put("action", "scan");
        jsonReturn.put("parameters", new JSONObject().put("direction", direction));
        lastAction = "scan";
        return jsonReturn.toString();
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