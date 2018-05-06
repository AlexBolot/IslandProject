package fr.unice.polytech.si3.qgl.ise.contracts;

import java.util.List;
import java.util.Optional;

public abstract class ContractChooser {
    private final List<RawContract> rawContracts;
    private final List<CraftedContract> craftedContracts;

    ContractChooser(List<RawContract> rawContracts, List<CraftedContract> craftedContracts) {
        this.rawContracts = rawContracts;
        this.craftedContracts = craftedContracts;
    }

    List<RawContract> getRawContracts() {
        return rawContracts;
    }

    List<CraftedContract> getCraftedContracts() {
        return craftedContracts;
    }

    public abstract Optional<Contract> chooseBestContract();
}
