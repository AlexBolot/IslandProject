package fr.unice.polytech.si3.qgl.ise.entities;

import fr.unice.polytech.si3.qgl.ise.CraftedContract;
import fr.unice.polytech.si3.qgl.ise.RawContract;
import fr.unice.polytech.si3.qgl.ise.actions.Action;
import fr.unice.polytech.si3.qgl.ise.actions.StopAction;
import fr.unice.polytech.si3.qgl.ise.actions.crew.Land;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;

import java.util.List;

public class Crew {
    private static final int movementUnit = 1;

    private JsonFactory json;

    private Action lastAction;

    private boolean isLanded;
    private String idCreek;
    private final List<RawContract> rawContracts;
    private final List<CraftedContract> craftedContracts;
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

    public Crew(IslandMap map, String creekId, int crewSize, List<RawContract> rawContracts, List<CraftedContract> craftedContracts) {
        this.map = map;
        this.idCreek = creekId;
        this.rawContracts = rawContracts;
        this.craftedContracts = craftedContracts;
        this.coords = map.getCreeks().get(creekId);
        knowExactPosition = false;
        this.crewSize = crewSize;
    }

    public String takeDecision() {
        if (!isLanded) {
            lastAction = new Land(this, idCreek, crewSize);
            return lastAction.apply();
        }

        return new StopAction().apply();
    }

    public boolean isLanded() {
        return isLanded;
    }

    public Coordinates getCoords() {
        return coords;
    }
}

