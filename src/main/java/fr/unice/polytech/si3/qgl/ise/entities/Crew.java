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

    private final List<RawContract> rawContracts;
    private final List<CraftedContract> craftedContracts;
    private final Map<RawResource, Integer> stock;
    private final Map<CraftedResource, Integer> craftedStock;
    private final IslandMap map;
    private Action lastAction;
    private String idCreek;
    private List<Action> steps;
    private Coordinates coordinates;
    private RawResource currentResource;
    private List<RawResource> wantedResources = new ArrayList<>();
    private final List<RawContract> completedRawContracts = new ArrayList<>();
    private final List<CraftedContract> completedCraftedContracts = new ArrayList<>();
    private int currentQuantity;

    public Crew(IslandMap map, List<RawContract> rawContracts, List<CraftedContract> craftedContracts) {
        this.map = map;
        this.rawContracts = rawContracts;
        this.craftedContracts = craftedContracts;
        this.stock = new EnumMap<>(RawResource.class);
        this.craftedStock = new EnumMap<>(CraftedResource.class);

        for (RawContract raw : rawContracts) {
            wantedResources.add(raw.getResource());
        }

        idCreek = PathFinder.findBestCreek(map, wantedResources);
        coordinates = map.getCreeks().get(idCreek);

        initActions();
    }

    /**
     * Chooses the contract with the least resources to collect
     *
     * @return the contract
     */
    public Optional<RawContract> choseBestRawContract() {
        if (rawContracts.isEmpty())
            return Optional.empty();
        return rawContracts.stream().max(Comparator.comparingInt(RawContract::getQuantity));
    }

    private void initActions() {
        steps = new ArrayList<>();
        steps.add(new Land(this, idCreek));
        //steps.add(new MoveTo(this, objective));
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

    public void finishRawContract(RawContract rawContract) {
        completedRawContracts.add(rawContract);
        for (int i = 0; i < rawContracts.size(); i++) {
            if (rawContracts.get(i).getResource().equals(rawContract.getResource())) {
                rawContracts.remove(i);
                break;
            }
        }
        wantedResources = new ArrayList<>();
        for (RawContract raw : rawContracts) {
            wantedResources.add(raw.getResource());
        }
    }

    public IslandMap getMap() {
        return map;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
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

    public Map<CraftedResource, Integer> getCraftedStock() {
        return craftedStock;
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

    public int getCraftedResourceQuantity(CraftedResource resource) {
        return craftedStock.get(resource);
    }

    public List<RawResource> getWantedResources() {
        return wantedResources;
    }

    public void setCurrentResource(RawResource currentResource) {
        this.currentResource = currentResource;
    }

    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public List<RawContract> getCompletedRawContracts() {
        return completedRawContracts;
    }

    public List<CraftedContract> getCompletedCraftedContracts() {
        return completedCraftedContracts;
    }
}

