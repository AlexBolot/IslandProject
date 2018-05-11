package fr.unice.polytech.si3.qgl.ise.entities;

import fr.unice.polytech.si3.qgl.ise.actions.Action;
import fr.unice.polytech.si3.qgl.ise.actions.CrewAction;
import fr.unice.polytech.si3.qgl.ise.actions.StopAction;
import fr.unice.polytech.si3.qgl.ise.actions.crew.Land;
import fr.unice.polytech.si3.qgl.ise.actions.loop.MoveExploitLoopAction;
import fr.unice.polytech.si3.qgl.ise.contracts.*;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import fr.unice.polytech.si3.qgl.ise.map.Forecaster;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.map.PathFinder;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.CraftedResource;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.RawResource;

import java.util.*;
import java.util.stream.Collectors;

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

        computeWantedResources();
        chooseNewFocus(howToChoseContract());

        idCreek = PathFinder.findBestCreek(map, wantedResources);
        coordinates = map.getCreeks().get(idCreek);

        initActions();
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
        rawContracts.stream()
                .filter(contract -> stock.containsKey(contract.getResource()))
                .forEach(contract -> contract.updateRemainingQuantity(stock.get(contract.getResource())));

        craftedContracts.stream()
                .filter(contract -> contract.getTotalResourcesToCollect().entrySet().stream()
                        .allMatch(entry -> stock.containsKey(entry.getKey())))
                .forEach(contract -> contract.getTotalResourcesToCollect().forEach((rawResource, quantity) -> contract.updateRemainingQuantityMinusStock(resource, stock.get(resource))));
    }

    public void removeFromStock(RawResource resource, int amount) {
        if (stock.containsKey(resource)) {
            int newStock = stock.get(resource) - amount;
            stock.put(resource, newStock);
        }
    }

    public void addToCraftedStock(CraftedResource resource, int amount) {
        if (craftedStock.containsKey(resource)) amount += craftedStock.get(resource);
        craftedStock.put(resource, amount);
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
        chooseNewFocus(howToChoseContract());
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

    public void land(String creekId) {
        if (!doNotEstimate) sortContractsAfterIslandData();
        this.isLanded = true;
        map.setShip(map.getCreeks().get(creekId));
    }

    public void sortContractsAfterCost(int remainingBudget) {
        abortedRawContracts.addAll(rawContracts.stream()
                .filter(contract -> Forecaster.estimateCost(contract) > remainingBudget)
                .collect(Collectors.toList()));

        rawContracts.removeAll(abortedRawContracts);

        abortedCraftedContracts.addAll(craftedContracts.stream()
                .filter(contract -> Forecaster.estimateCost(contract) > remainingBudget)
                .collect(Collectors.toList()));

        craftedContracts.removeAll(abortedCraftedContracts);
    }


    private void initActions() {
        steps = new ArrayList<>();
        steps.add(new Land(this, idCreek));
        steps.add(new MoveExploitLoopAction(this));
        steps.add(new StopAction());
    }

    private void chooseNewFocus(ContractChooser howToDertemine) {
        currentResource = null;

        Optional<Contract> bestContract = howToDertemine.chooseBestContract();
        bestContract.ifPresent(contract -> {
            int i = 0;
            int size = contract.getTotalResourcesToCollect().entrySet().size();
            for (Map.Entry<RawResource, Double> entry : contract.getTotalResourcesToCollect().entrySet()) {
                ++i;
                int realStock;
                // If we don't have it in stock, we focus it
                if (stock.containsKey(entry.getKey())) {
                    realStock = stock.get(entry.getKey());
                    // Else, we calculate if the stock isn't really enough

                    // We calculate the amount of the resource used in rawContracts
                    for (RawContract rawContract : completedRawContracts) {
                        if (rawContract.getResource().equals(entry.getKey())) {
                            realStock = realStock - rawContract.getQuantity();
                        }
                    }
                    if (realStock < entry.getValue() || i == size) {
                        currentResource = entry.getKey();
                        return;
                    }
                } else {
                    currentResource = entry.getKey();
                    return;
                }
            }
        });
    }

    /**
     * We decide here how to determine the next Best contract based on contracts left and current crew
     *
     * @return the ContractChose that we need at the moment
     */
    private ContractChooser howToChoseContract() {
        List<RawContract> rawContractsLeft = rawContracts;
        rawContractsLeft.removeAll(abortedRawContracts);
        rawContractsLeft.removeAll(completedRawContracts);

        List<CraftedContract> craftedContractsLeft = craftedContracts;
        craftedContractsLeft.removeAll(abortedCraftedContracts);
        craftedContractsLeft.removeAll(completedCraftedContracts);
        return new BasicContractChooser(rawContractsLeft, craftedContractsLeft);
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
        Map<RawResource, Double> foretoldResources = Forecaster.estimateResources(map);


        abortedRawContracts.addAll(rawContracts.stream()
                .filter(contract -> !foretoldResources.containsKey(contract.getResource()) || contract.getQuantity() > foretoldResources.get(contract.getResource()))
                .collect(Collectors.toList()));

        rawContracts.removeAll(abortedRawContracts);

        abortedCraftedContracts.addAll(craftedContracts.stream()
                .filter(contract -> contract.getTotalResourcesToCollect().entrySet().stream()
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

