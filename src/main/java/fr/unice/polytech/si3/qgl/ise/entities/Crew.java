package fr.unice.polytech.si3.qgl.ise.entities;

import fr.unice.polytech.si3.qgl.ise.CraftedContract;
import fr.unice.polytech.si3.qgl.ise.RawContract;
import fr.unice.polytech.si3.qgl.ise.actions.Action;
import fr.unice.polytech.si3.qgl.ise.actions.CrewAction;
import fr.unice.polytech.si3.qgl.ise.actions.StopAction;
import fr.unice.polytech.si3.qgl.ise.actions.crew.Land;
import fr.unice.polytech.si3.qgl.ise.actions.loop.MoveExploitLoopAction;
import fr.unice.polytech.si3.qgl.ise.enums.Abundance;
import fr.unice.polytech.si3.qgl.ise.enums.Exploitability;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.map.PathFinder;
import scala.Tuple2;

import java.util.*;

public class Crew {
    private static final int movementUnit = 1;

    private JsonFactory json;

    private Action lastAction;

    private boolean isLanded;
    private String idCreek;
    private List<RawContract> rawContracts;
    private List<CraftedContract> craftedContracts;
    private Integer crewSize;
    private Map<RawResource, Tuple2<Abundance, Exploitability>> lastExplore;
    private List<Action> steps;
    private Map<RawResource, Integer> stock;

    private IslandMap map;
    private Coordinates coords;
    private RawResource currentResource;
    private int currentQuantity;

    /**
     * While the coord is an hypothesis and we didn't have a situation that can makes us sure of where we are
     */
    private boolean knowExactPosition;

    /**
     * Objective where we want the crew to go.
     */
    private Coordinates objective;

    public Crew(IslandMap map, int crewSize, List<RawContract> rawContracts, List<CraftedContract> craftedContracts) {
        this.map = map;
        this.rawContracts = rawContracts;
        this.craftedContracts = craftedContracts;
        this.knowExactPosition = false;
        this.crewSize = crewSize;
        this.stock = new EnumMap<>(RawResource.class);

        Optional<RawContract> bestContract = choseBestRawContract();
        bestContract.ifPresent(rawContract -> currentResource = rawContract.getResource());
        bestContract.ifPresent(rawContract -> currentQuantity = rawContract.getQuantity());

        idCreek = PathFinder.findNearestCreekOfResource(map, currentResource);
        coords = map.getCreeks().get(idCreek);
        objective = PathFinder.findNearestTileOfResource(map, coords, currentResource);

        initActions();
    }


    private List<RawContract> getRawContractsLeft() {
        List<RawContract> contracts = new ArrayList<>();
        for (RawContract contract : rawContracts) {
            if (!stock.containsKey(contract.getResource()) || stock.get(contract.getResource()) < contract.getQuantity())
                contracts.add(contract);
        }
        return contracts;
    }

    /**
     * Chose the contract with the less ressource to collect
     *
     * @return the contract
     */
    private Optional<RawContract> choseBestRawContract() {
        if (rawContracts.size() == 0)
            return Optional.empty();
        return getRawContractsLeft().stream().max(Comparator.comparingInt(RawContract::getQuantity));
    }

    private void initActions() {
        steps = new ArrayList<>();
        steps.add(new Land(this, idCreek, crewSize));
        //steps.add(new Move_to(this, objective));
        steps.add(new MoveExploitLoopAction(this));
        steps.add(new StopAction());
    }

    public String takeDecision() {
        String res;

        for (Action step : steps) {
            lastAction = step;

            if (lastAction.isFinished()) continue;

            res = lastAction.apply();

            if (res.isEmpty()) continue;

            return res;
        }

        return new StopAction().apply();
    }

    public void acknowledgeResults(String results) {
        if (lastAction instanceof CrewAction) {
            ((CrewAction) lastAction).acknowledgeResults(this, results);
        }
        //else would be only for stop action <=> do nothing
    }

    public void addToStock(RawResource resource, int amount) {
        if (stock.containsKey(resource)) amount += stock.get(resource);
        stock.put(resource, amount);
    }

    public IslandMap getMap() {
        return map;
    }

    public boolean isLanded() {
        return isLanded;
    }

    public Coordinates getCoords() {
        return coords;
    }

    public void setCoords(Coordinates coords) {
        this.coords = coords;
    }

    public void setCurrentResource(RawResource currentResource) {
        this.currentResource = currentResource;
    }

    public RawResource getCurrentResource() {
        return currentResource;
    }

    public void setIdCreek(String id) {
        this.idCreek = id;
    }

    public void setCrewSize(int size) {
        this.crewSize = size;
    }

    public Map<RawResource, Integer> getStock() {
        return stock;
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public List<RawContract> getRawContracts() {
        return rawContracts;
    }

    public List<CraftedContract> getCraftedContracts() {
        return craftedContracts;
    }
}

