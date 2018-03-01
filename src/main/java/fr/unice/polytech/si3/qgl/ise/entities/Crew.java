package fr.unice.polytech.si3.qgl.ise.entities;

import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;

public class Crew {
    private static final int movementUnit = 1;

    private JsonFactory json;

    private DroneEnums.Action lastAction;

    private boolean isLanded;
    private String idCreek;
    private Integer crewSize;

    private IslandMap map;
    private Coordinates coords;

    /**
     * While the coord is an hypothesis and we didn't have a situation that can makes us sure of where we are
     */
    private boolean knowExactPosition;

    /**
     * Objective where we want the crew to go.
     */
    private Coordinates objective;

    public Crew(IslandMap map, String creekId, int crewSize) {
        this.map = map;
        this.idCreek = creekId;
        this.coords = map.getCreeks().get(creekId);
        knowExactPosition = false;
        this.crewSize = crewSize;
    }

    public String takeDecision() {
        if (!isLanded)
            return json.createJsonString("land", "creek", idCreek,
                    "people", crewSize.toString());

        return json.createJsonString("stop");
    }

    public boolean isLanded() {
        return isLanded;
    }
}

