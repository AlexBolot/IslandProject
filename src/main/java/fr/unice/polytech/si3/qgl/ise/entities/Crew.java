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
    private final List<RawContract> completedRawContracts = new ArrayList<>();
    private final List<CraftedContract> completedCraftedContracts = new ArrayList<>();
    private Action lastAction;
    private String idCreek;
    private List<Action> steps;
    private Coordinates coordinates;
    private RawResource currentResource;
    private List<RawResource> wantedResources = new ArrayList<>();
    private boolean isLanded;

    public Crew(IslandMap map, List<RawContract> rawContracts, List<CraftedContract> craftedContracts) {
        this.map = map;
        this.rawContracts = rawContracts;
        this.craftedContracts = craftedContracts;
        this.stock = new EnumMap<>(RawResource.class);
        this.craftedStock = new EnumMap<>(CraftedResource.class);
        this.isLanded = false;

        computeWantedResources();
        chooseNewFocus();

        idCreek = PathFinder.findBestCreek(map, wantedResources);
        coordinates = map.getCreeks().get(idCreek);

        initActions();
    }

    /**
     * Chooses the contract with the least resources to collect
     *
     * @return the contract
     */
    private Optional<RawContract> choseBestRawContract() {
        if (rawContracts.isEmpty())
            return Optional.empty();
        return rawContracts.stream().max(Comparator.comparingInt(RawContract::getQuantity));
    }

    private Optional<CraftedContract> choseBestCraftedContract() {
        if (craftedContracts.isEmpty())
            return Optional.empty();
        return craftedContracts.stream().max(Comparator.comparingInt(CraftedContract::getRemainingQuantity));
    }

    private void initActions() {
        steps = new ArrayList<>();
        steps.add(new Land(this, idCreek));
        steps.add(new MoveExploitLoopAction(this));
        steps.add(new StopAction());
    }

    public String takeDecision() {
        String res;

        for (Action step : steps) {
            lastAction = step;

            if (lastAction.isFinished() || (lastAction instanceof Land && this.isLanded)) continue;

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

    public void removeFromStock(RawResource resource, int amount) {
        //this if should be useless, it's just here for security precaution
        if (stock.containsKey(resource)) {
            int newStock = stock.get(resource) - amount;
            stock.put(resource, newStock);
        }
    }

    public void addToCraftedStock(CraftedResource resource, int amount) {
        if (craftedStock.containsKey(resource)) amount += craftedStock.get(resource);
        craftedStock.put(resource, amount);
    }

    private void chooseNewFocus() {
        currentResource = null;

        Optional<RawContract> bestContract = choseBestRawContract();
        bestContract.ifPresent(rawContract -> currentResource = rawContract.getResource());
        if (currentResource == null) {
            Optional<CraftedContract> bestCraftedContract = choseBestCraftedContract();
            if (bestCraftedContract.isPresent()) {
                Map<RawResource, Double> resources = bestCraftedContract.get().getRemainingRawQuantities();
                for (Map.Entry<RawResource, Double> entry : resources.entrySet()) {
                    int realStock;
                    if (stock.containsKey(entry.getKey())) {
                        realStock = stock.get(entry.getKey());
                        for (RawContract rawContract : completedRawContracts) {
                            if (rawContract.getResource().equals(entry.getKey())) {
                                realStock = realStock - rawContract.getQuantity();
                            }
                        }
                        if (realStock < entry.getValue()) {
                            currentResource = entry.getKey();
                            return;
                        }
                    } else {
                        currentResource = entry.getKey();
                        return;
                    }
                }
            }
        }
    }

    public void tryToFinishContracts() {

        rawContracts.removeIf(rawContract -> {
            RawResource resource = rawContract.getResource();
            int quantity = rawContract.getQuantity();

            boolean completed = stock.containsKey(resource) && stock.get(resource) >= quantity;

            if (completed)
                completedRawContracts.add(rawContract);

            return completed;
        });

        craftedContracts.removeIf(craftedContract -> {
            CraftedResource resource = craftedContract.getResource();
            int quantity = craftedContract.getQuantity();

            boolean completed = craftedStock.containsKey(resource) && craftedStock.get(resource) >= quantity;

            if (completed)
                completedCraftedContracts.add(craftedContract);

            return completed;
        });

        computeWantedResources();

        chooseNewFocus();
    }

    public Map<RawResource, Double> tryCrafting() {
        for (CraftedContract craft : craftedContracts) {
            boolean test = true;
            Map<RawResource, Double> remainingResources = craft.getRemainingRawQuantities();
            for (Map.Entry<RawResource, Double> entry : remainingResources.entrySet()) {
                int realStock;
                if (stock.containsKey(entry.getKey()) && stock.get(entry.getKey()) >= entry.getValue()) {
                    realStock = stock.get(entry.getKey());
                    for (RawContract rawContract : completedRawContracts) {
                        if (rawContract.getResource().equals(entry.getKey())) {
                            realStock = realStock - rawContract.getQuantity();
                        }
                    }
                    test = realStock >= entry.getValue();
                } else {
                    test = false;
                }
                if (!test) {
                    break;
                }
            }
            if (test) {
                return remainingResources;
            }
        }
        return null;
    }

    private void computeWantedResources() {
        wantedResources = new ArrayList<>();
        for (RawContract raw : rawContracts) {
            wantedResources.add(raw.getResource());
        }
        for (CraftedContract craft : craftedContracts) {
            for (RawResource raw : craft.getRemainingRawQuantities().keySet()) {
                if (!wantedResources.contains(raw)) {
                    int realStock;
                    if (stock.containsKey(raw)) {
                        realStock = stock.get(raw);
                        for (RawContract rawContract : completedRawContracts) {
                            if (rawContract.getResource().equals(raw)) {
                                realStock = realStock - rawContract.getQuantity();
                            }
                        }
                        if (realStock < craft.getRemainingRawQuantities().get(raw)) {
                            wantedResources.add(raw);
                        }
                    } else {
                        wantedResources.add(raw);
                    }
                }
            }
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

    public void setCurrentResource(RawResource currentResource) {
        this.currentResource = currentResource;
    }

    public String getIdCreek() {
        return this.idCreek;
    }

    public void setIdCreek(String id) {
        this.idCreek = id;
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

    List<RawContract> getCompletedRawContracts() {
        return completedRawContracts;
    }

    List<CraftedContract> getCompletedCraftedContracts() {
        return completedCraftedContracts;
    }

    public void land() {
        this.isLanded = true;
    }
}

