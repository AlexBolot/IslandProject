package fr.unice.polytech.si3.qgl.ise.parsing;

import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle;
import org.json.JSONObject;

/**
 * Parses the data obtained through the echo command
 */
public class Echo {
    private int cost;
    private int range;
    private Obstacle obstacle;

    public Echo(String echoResult) {
        JSONObject data = new JSONObject(echoResult);
        cost = data.getInt("cost");
        JSONObject extras = data.getJSONObject("extras");
        range = extras.getInt("range");
        obstacle = Obstacle.valueOf(extras.getString("found"));
    }

    public int getCost() {
        return cost;
    }

    public int getRange() {
        return range;
    }

    public Obstacle getObstacle() {
        return obstacle;
    }
}
