package fr.unice.polytech.si3.qgl.ise.entities;

import fr.unice.polytech.si3.qgl.ise.CraftedContract;
import fr.unice.polytech.si3.qgl.ise.RawContract;
import fr.unice.polytech.si3.qgl.ise.actions.Action;
import fr.unice.polytech.si3.qgl.ise.actions.CrewAction;
import fr.unice.polytech.si3.qgl.ise.actions.StopAction;
import fr.unice.polytech.si3.qgl.ise.actions.crew.ExploitTile;
import fr.unice.polytech.si3.qgl.ise.actions.crew.Land;
import fr.unice.polytech.si3.qgl.ise.actions.crew.Move_to;
import fr.unice.polytech.si3.qgl.ise.enums.Abundance;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.map.PathFinder;

import java.util.*;

public class Crew
{
    private static final int movementUnit = 1;

    private JsonFactory json;

    private Action lastAction;

    private boolean                         isLanded;
    private String                          idCreek;
    private List<RawContract>               rawContracts;
    private List<CraftedContract>           craftedContracts;
    private Integer                         crewSize;
    private HashMap<RawResource, Abundance> lastExplore;
    private ArrayList<Action>               steps;

    private IslandMap   map;
    private Coordinates coords;
    private RawResource currentResource;

    /**
     While the coord is an hypothesis and we didn't have a situation that can makes us sure of where we are
     */
    private boolean knowExactPosition;

    /**
     Objective where we want the crew to go.
     */
    private Coordinates objective;

    public Crew (IslandMap map, int crewSize, List<RawContract> rawContracts, List<CraftedContract> craftedContracts)
    {
        this.map = map;
        this.rawContracts = rawContracts;
        this.craftedContracts = craftedContracts;
        knowExactPosition = false;
        this.crewSize = crewSize;

        Optional<RawContract> bestContract = rawContracts.stream().min(Comparator.comparingInt(RawContract::getQuantity));
        bestContract.ifPresent(rawContract -> currentResource = rawContract.getResource());

        idCreek = PathFinder.findNearestCreekOfResource(map, currentResource);
        coords = map.getCreeks().get(idCreek);
        objective = PathFinder.findNearestTileOfResource(map, coords, currentResource);

        initActions();
    }

    private void initActions ()
    {
        steps = new ArrayList<>();
        steps.add(new Land(this, idCreek, crewSize));
        steps.add(new Move_to(this, objective));
        steps.add(new ExploitTile(this, currentResource));
        steps.add(new StopAction());
    }

    public String takeDecision ()
    {
        String res;

        for (Action step : steps)
        {
            lastAction = step;

            if (lastAction.isFinished()) continue;

            res = lastAction.apply();

            if (res.isEmpty()) continue;

            return res;
        }

        return new StopAction().apply();
    }

    public void acknowledgeResults (String results)
    {
        if (lastAction instanceof CrewAction)
        {
            ((CrewAction) lastAction).acknowledgeResults(this, results);
        }
        //else would be only for stop action <=> do nothing
    }

    public IslandMap getMap ()
    {
        return map;
    }

    public boolean isLanded ()
    {
        return isLanded;
    }

    public Coordinates getCoords ()
    {
        return coords;
    }

    public void setCoords (Coordinates coords)
    {
        this.coords = coords;
    }

    public void setLastExplore (HashMap<RawResource, Abundance> resources)
    {
        this.lastExplore = resources;
    }

    public HashMap<RawResource, Abundance> getLastExplore ()
    {
        return lastExplore;
    }

    public void setCurrentResource (RawResource currentResource)
    {
        this.currentResource = currentResource;
    }

    public RawResource getCurrentResource ()
    {
        return currentResource;
    }

    public void setIdCreek (String id)
    {
        this.idCreek = id;
    }

    public void setCrewSize (int size)
    {
        this.crewSize = size;
    }
}

