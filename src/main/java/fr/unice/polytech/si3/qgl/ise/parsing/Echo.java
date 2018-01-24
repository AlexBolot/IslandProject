package fr.unice.polytech.si3.qgl.ise.parsing;

import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import org.json.JSONObject;

public class Echo {
    private int cost;
    private int range;
    private DroneEnums.Obstacle obstacle;

    public Echo(String echoResult) {
        JSONObject data = new JSONObject(echoResult);
        cost = data.getInt("cost");
        JSONObject extras = data.getJSONObject("extras");
        range = extras.getInt("range");
        obstacle = DroneEnums.Obstacle.valueOf(extras.getString("found"));
    }

    public int getCost() {
        return cost;
    }

    public int getRange() {
        return range;
    }

    public DroneEnums.Obstacle getObstacle() {
        return obstacle;
    }
}
