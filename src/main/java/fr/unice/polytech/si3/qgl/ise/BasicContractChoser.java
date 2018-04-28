package fr.unice.polytech.si3.qgl.ise;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class BasicContractChoser extends ContractChoser {
    public BasicContractChoser(List<RawContract> rawContracts, List<CraftedContract> craftedContracts) {
        super(rawContracts, craftedContracts);
    }

    /**
     * Chooses the contract with the least resources to collect
     *
     * @return the contract
     */
    private Contract choseBestRawContract() {
        if (getRawContracts().isEmpty())
            return null;
        return getRawContracts().stream().max(Comparator.comparingInt(RawContract::getQuantity)).orElse(null);
    }

    private Contract choseBestCraftedContract() {
        if (getCraftedContracts().isEmpty())
            return null;
        return getCraftedContracts().stream().max(Comparator.comparingInt(CraftedContract::getRemainingQuantity)).orElse(null);
    }

    @Override
    public Optional<Contract> chooseBestContract() {
        Contract bestRawContract = choseBestRawContract();
        Contract bestCraftedContract = choseBestCraftedContract();
        return bestRawContract != null ?
                Optional.of(bestRawContract) :
                bestCraftedContract != null ?
                        Optional.of(bestCraftedContract) : Optional.empty();
    }
}
