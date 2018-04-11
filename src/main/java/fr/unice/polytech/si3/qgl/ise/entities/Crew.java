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
    private int currentQuantity;
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
        currentQuantity = 0;

        Optional<RawContract> bestContract = choseBestRawContract();
        bestContract.ifPresent(rawContract -> currentResource = rawContract.getResource());
        bestContract.ifPresent(rawContract -> currentQuantity = rawContract.getQuantity());
        if (currentResource == null) {
            Optional<CraftedContract> bestCraftedContract = choseBestCraftedContract();
            if (bestCraftedContract.isPresent()) {
                Map<RawResource, Double> resources = bestCraftedContract.get().getRemainingRawQuantities();
                for (RawResource rawResource : resources.keySet()) {
                    int realStock;
                    if (stock.containsKey(rawResource)) {
                        realStock = stock.get(rawResource);
                        for (RawContract rawContract : completedRawContracts) {
                            if (rawContract.getResource().equals(rawResource)) {
                                realStock = realStock - rawContract.getQuantity();
                            }
                        }
                        if (realStock < resources.get(rawResource)) {
                            currentResource = rawResource;
                            currentQuantity = resources.get(rawResource).intValue();
                            return;
                        }
                    } else {
                        currentResource = rawResource;
                        currentQuantity = resources.get(rawResource).intValue();
                        return;
                    }
                }
            }
        }
    }

    private void finishRawContract(RawContract rawContract) {
        completedRawContracts.add(rawContract);
        for (int i = 0; i < rawContracts.size(); i++) {
            if (rawContracts.get(i).getResource().equals(rawContract.getResource())) {
                rawContracts.remove(i);
                break;
            }
        }
        computeWantedResources();
    }

    private void finishCraftedContract(CraftedContract craftedContract) {
        completedCraftedContracts.add(craftedContract);
        for (int i = 0; i < craftedContracts.size(); i++) {
            if (craftedContracts.get(i).getResource().equals(craftedContract.getResource())) {
                craftedContracts.remove(i);
                break;
            }
        }
        computeWantedResources();
    }

    public void tryToFinishContracts() {
        RawContract raw;
        int size = rawContracts.size();
        for (int i = 0; i < size; i++) {
            raw = rawContracts.get(i);
            RawResource rawResource = raw.getResource();
            int rawQuantity = raw.getQuantity();
            if (stock.containsKey(rawResource) && stock.get(rawResource) >= rawQuantity) {
                size--;
                i--;
                finishRawContract(raw);
            }
        }
        for (CraftedContract craft : craftedContracts) {
            CraftedResource craftedResource = craft.getResource();
            int craftedQuantity = craft.getQuantity();
            if (craftedStock.containsKey(craftedResource) && craftedStock.get(craftedResource) >= craftedQuantity) {
                finishCraftedContract(craft);
            }
        }

        chooseNewFocus();
    }

    public Map<RawResource, Double> tryCrafting() {
        for (CraftedContract craft : craftedContracts) {
            boolean test = true;
            Map<RawResource, Double> remainingResources = craft.getRemainingRawQuantities();
            for (RawResource rawResource : remainingResources.keySet()) {
                int realStock;
                if (stock.containsKey(rawResource) && stock.get(rawResource) >= remainingResources.get(rawResource)) {
                    realStock = stock.get(rawResource);
                    for (RawContract rawContract : completedRawContracts) {
                        if (rawContract.getResource().equals(rawResource)) {
                            realStock = realStock - rawContract.getQuantity();
                        }
                    }
                    test = realStock >= remainingResources.get(rawResource);
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
            for (RawResource raw : craft.getRawQuantities().keySet()) {
                if (!wantedResources.contains(raw)) {
                    wantedResources.add(raw);
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

    public void setIdCreek(String id) {
        this.idCreek = id;
    }

    public String getIdCreek() {
        return this.idCreek;
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

    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
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

    public List<RawContract> getCompletedRawContracts() {
        return completedRawContracts;
    }

    public List<CraftedContract> getCompletedCraftedContracts() {
        return completedCraftedContracts;
    }

    public void land() {
        this.isLanded = true;
    }
}

