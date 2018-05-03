package fr.unice.polytech.si3.qgl.ise;

import java.util.List;
import java.util.Optional;

public abstract class ContractChoser {
    private List<RawContract> rawContracts;
    private List<CraftedContract> craftedContracts;

    public ContractChoser(List<RawContract> rawContracts, List<CraftedContract> craftedContracts) {
        this.rawContracts = rawContracts;
        this.craftedContracts = craftedContracts;
    }

    public List<RawContract> getRawContracts() {
        return rawContracts;
    }

    public List<CraftedContract> getCraftedContracts() {
        return craftedContracts;
    }

    public abstract Optional<Contract> chooseBestContract();
}
