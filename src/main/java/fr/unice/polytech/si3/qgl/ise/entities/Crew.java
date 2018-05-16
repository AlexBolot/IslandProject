package fr.unice.polytech.si3.qgl.ise.entities;

import fr.unice.polytech.si3.qgl.ise.actions.Action;
import fr.unice.polytech.si3.qgl.ise.actions.CrewAction;
import fr.unice.polytech.si3.qgl.ise.actions.StopAction;
import fr.unice.polytech.si3.qgl.ise.actions.crew.Land;
import fr.unice.polytech.si3.qgl.ise.actions.loop.MoveExploitLoopAction;
import fr.unice.polytech.si3.qgl.ise.contracts.CraftedContract;
import fr.unice.polytech.si3.qgl.ise.contracts.RawContract;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import fr.unice.polytech.si3.qgl.ise.map.Forecaster;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.map.PathFinder;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.CraftedResource;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.RawResource;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Drone entitity, coordinates actions in {@link fr.unice.polytech.si3.qgl.ise.actions.crew}
 * and in {@link fr.unice.polytech.si3.qgl.ise.actions.loop}
 */
public class Crew {

    private final List<RawContract> rawContracts;
    private final List<CraftedContract> craftedContracts;
    private final Map<RawResource, Integer> stock;
    private final Map<CraftedResource, Integer> craftedStock;
    private final IslandMap map;
    private final List<RawContract> completedRawContracts = new ArrayList<>();
    private final List<CraftedContract> completedCraftedContracts = new ArrayList<>();
    private final List<RawContract> abortedRawContracts = new ArrayList<>();
    private final List<CraftedContract> abortedCraftedContracts = new ArrayList<>();
    private final Forecaster forecaster;

    private Action lastAction;
    private String idCreek;
    private List<Action> steps;
    private Coordinates coordinates;
    private RawResource currentResource;
    private List<RawResource> wantedResources = new ArrayList<>();

    private boolean isLanded;
    private boolean doNotEstimate;


    public Crew(IslandMap map, List<RawContract> rawContracts, List<CraftedContract> craftedContracts) {
        this.map = map;
        this.rawContracts = rawContracts;
        this.craftedContracts = craftedContracts;
        this.stock = new HashMap<>();
        this.craftedStock = new HashMap<>();
        this.doNotEstimate = false;
        PathFinder pathFinder = new PathFinder(map, 50, 80);
        this.forecaster = new Forecaster();

        computeWantedResources();

        idCreek = pathFinder.findBestCreek(wantedResources);
        coordinates = map.getCreeks().get(idCreek);

        initActions();
    }

    /**
     * Main method, do the current step
     *
     * @return a JSON formatted String to do the action
     */
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

    /**
     * Acknoledge the result of the previous action done
     *
     * @param results of the previous action
     */
    public void acknowledgeResults(String results) {
        if (lastAction instanceof CrewAction) {
            ((CrewAction) lastAction).acknowledgeResults(results);
        }
        //else would be only for stop action <=> do nothing
    }

    /**
     * Add to inventory
     *
     * @param resource kind of resource
     * @param amount   how much to add
     */
    public void addToStock(RawResource resource, int amount) {
        if (stock.containsKey(resource)) amount += stock.get(resource);
        stock.put(resource, amount);
        rawContracts.stream()
                .filter(contract -> stock.containsKey(contract.getResource()))
                .forEach(contract -> contract.updateRemainingQuantity(stock.get(contract.getResource())));

        craftedContracts.stream()
                .filter(contract -> contract.getRawQuantities().entrySet().stream()
                        .allMatch(entry -> stock.containsKey(entry.getKey())))
                .forEach(contract -> contract.getRawQuantities().forEach((rawResource, quantity) -> contract.updateRemainingQuantityMinusStock(resource, stock.get(resource))));
    }

    /**
     * Remove from inventory
     *
     * @param resource kind of resource
     * @param amount   how much to remove
     */
    public void removeFromStock(RawResource resource, int amount) {
        if (stock.containsKey(resource)) {
            int newStock = stock.get(resource) - amount;
            stock.put(resource, newStock);
        }
    }

    /**
     * Add to crafted inventory
     *
     * @param resource kind of resource
     * @param amount   how much to add
     */
    public void addToCraftedStock(CraftedResource resource, int amount) {
        if (craftedStock.containsKey(resource)) amount += craftedStock.get(resource);
        craftedStock.put(resource, amount);
    }

    /**
     * Update the focused contract
     */
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
    }

    /**
     * @return remaining ressource if we craft everything we have to and that we can
     */
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

    /**
     * Init the craw
     *
     * @param creekId id of the creek to land on
     */
    public void land(String creekId) {
        if (!doNotEstimate) sortContractsAfterIslandData();
        this.isLanded = true;
        map.setShip(map.getCreeks().get(creekId));
    }

    /**
     * Aborts contracts if their cost is higher than the remaining budget
     *
     * @param remainingBudget : the number of points remaining
     */
    public void sortContractsAfterCost(int remainingBudget) {
        Map<RawContract, Double> rawContractsCosts = new HashMap<>();
        Map<CraftedContract, Double> craftedContractsCosts = new HashMap<>();
        double entireCost = 0;

        rawContracts.forEach(contract -> rawContractsCosts.put(contract, forecaster.estimateCost(contract)));

        abortedRawContracts.addAll(rawContracts.stream()
                .filter(contract -> rawContractsCosts.get(contract) > remainingBudget)
                .collect(Collectors.toList()));

        craftedContracts.forEach(contract -> craftedContractsCosts.put(contract, forecaster.estimateCost(contract)));

        abortedCraftedContracts.addAll(craftedContracts.stream()
                .filter(contract -> craftedContractsCosts.get(contract) > remainingBudget)
                .collect(Collectors.toList()));

        entireCost += rawContractsCosts.values().stream().mapToDouble(cost -> cost).sum();
        entireCost += craftedContractsCosts.values().stream().mapToDouble(cost -> cost).sum();

        while (entireCost > remainingBudget) {
            Map.Entry<RawContract, Double> worstRawContract = rawContractsCosts.entrySet().stream()
                    .max(Comparator.comparingDouble(Map.Entry::getValue))
                    .orElse(null);

            Map.Entry<CraftedContract, Double> worstCraftedContract = craftedContractsCosts.entrySet().stream()
                    .max(Comparator.comparingDouble(Map.Entry::getValue))
                    .orElse(null);

            if (worstCraftedContract == null || worstRawContract == null) {
                throw new NullPointerException("Problem while aborting contract!");
            }

            if (worstCraftedContract.getValue() > worstRawContract.getValue() * 2) {
                abortedCraftedContracts.add(worstCraftedContract.getKey());
                entireCost -= worstCraftedContract.getValue();
            } else {
                abortedRawContracts.add(worstRawContract.getKey());
                entireCost -= worstRawContract.getValue();
            }
        }

        rawContracts.removeAll(abortedRawContracts);
        craftedContracts.removeAll(abortedCraftedContracts);
    }


    private void initActions() {
        steps = new ArrayList<>();
        steps.add(new Land(this, idCreek));
        steps.add(new MoveExploitLoopAction(this));
        steps.add(new StopAction());
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


    private void sortContractsAfterIslandData() {
        Map<RawResource, Double> foretoldResources = forecaster.estimateResources(map);


        abortedRawContracts.addAll(rawContracts.stream()
                .filter(contract -> !foretoldResources.containsKey(contract.getResource()) || contract.getQuantity() > foretoldResources.get(contract.getResource()))
                .collect(Collectors.toList()));

        rawContracts.removeAll(abortedRawContracts);

        abortedCraftedContracts.addAll(craftedContracts.stream()
                .filter(contract -> contract.getRawQuantities().entrySet().stream()
                        .anyMatch(entry -> !foretoldResources.containsKey(entry.getKey()) || entry.getValue() > foretoldResources.get(entry.getKey())))
                .collect(Collectors.toList()));

        craftedContracts.removeAll(abortedCraftedContracts);
    }

    //region ========= Getters & Setters ========
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

    public void setDoNotEstimate(boolean doNotEstimate) {
        this.doNotEstimate = doNotEstimate;
    }

    List<RawContract> getCompletedRawContracts() {
        return completedRawContracts;
    }

    List<CraftedContract> getCompletedCraftedContracts() {
        return completedCraftedContracts;
    }

    //endregion
}

