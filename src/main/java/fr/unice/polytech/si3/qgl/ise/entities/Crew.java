package fr.unice.polytech.si3.qgl.ise.entities;

import fr.unice.polytech.si3.qgl.ise.CraftedContract;
import fr.unice.polytech.si3.qgl.ise.RawContract;
import fr.unice.polytech.si3.qgl.ise.actions.Action;
import fr.unice.polytech.si3.qgl.ise.actions.CrewAction;
import fr.unice.polytech.si3.qgl.ise.actions.StopAction;
import fr.unice.polytech.si3.qgl.ise.actions.crew.Land;
import fr.unice.polytech.si3.qgl.ise.actions.loop.MoveExploitLoopAction;
import fr.unice.polytech.si3.qgl.ise.enums.CraftedResource;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.map.PathFinder;

import java.util.*;

public class Crew {

    private Action lastAction;

    private String idCreek;
    private final List<RawContract> rawContracts;
    private final List<CraftedContract> craftedContracts;
    private List<Action> steps;
    private final Map<RawResource, Integer> stock;
    private final Map<CraftedResource, Integer> craftedStock;

    private final IslandMap map;
    private Coordinates coords;
    private RawResource currentResource;
    private int currentQuantity;

    public Crew(IslandMap map, List<RawContract> rawContracts, List<CraftedContract> craftedContracts) {
        this.map = map;
        this.rawContracts = rawContracts;
        this.craftedContracts = craftedContracts;
        this.stock = new EnumMap<>(RawResource.class);
        this.craftedStock = new EnumMap<>(CraftedResource.class);

        Optional<RawContract> bestContract = choseBestRawContract();
        bestContract.ifPresent(rawContract -> currentResource = rawContract.getResource());
        bestContract.ifPresent(rawContract -> currentQuantity = rawContract.getQuantity());

        idCreek = PathFinder.findNearestCreekOfResource(map, currentResource);
        coords = map.getCreeks().get(idCreek);

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
        steps.add(new Land(this, idCreek));
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
            ((CrewAction) lastAction).acknowledgeResults(results);
        }
        //else would be only for stop action <=> do nothing
    }

    public void addToStock(RawResource resource, int amount) {
        if (stock.containsKey(resource)) amount += stock.get(resource);
        stock.put(resource, amount);
    }

    public void addToCraftedStock(CraftedResource resource, int amount) {
        if (craftedStock.containsKey(resource)) amount += craftedStock.get(resource);
        craftedStock.put(resource, amount);
    }

    public IslandMap getMap() {
        return map;
    }

    public Coordinates getCoords() {
        return coords;
    }

    public void setCoords(Coordinates coords) {
        this.coords = coords;
    }

    public RawResource getCurrentResource() {
        return currentResource;
    }

    public void setIdCreek(String id) {
        this.idCreek = id;
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

    public int getCraftedRessourceQuantity(CraftedResource resource) {
        return craftedStock.get(resource);
    }
}

