package fr.unice.polytech.si3.qgl.ise.parsing;

import fr.unice.polytech.si3.qgl.ise.contracts.CraftedContract;
import fr.unice.polytech.si3.qgl.ise.contracts.RawContract;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static fr.unice.polytech.si3.qgl.ise.parsing.externalresources.ExtResSelector.bundle;

public class ContractParser {

    private static final String RESOURCE = "resource";
    private final int men;
    private final String heading;
    private final int budget;
    private final List<RawContract> rawContracts;
    private final List<CraftedContract> craftedContracts;

    public ContractParser(String stringToParse) {
        JSONObject data = new JSONObject(stringToParse);
        men = data.getInt("men");
        heading = data.getString("heading");
        budget = data.getInt("budget");
        rawContracts = new ArrayList<>();
        craftedContracts = new ArrayList<>();
        parseContracts(data.getJSONArray("contracts"));
    }

    private void parseContracts(JSONArray contracts) {
        for (int i = 0; i < contracts.length(); ++i) {
            JSONObject obj = contracts.getJSONObject(i);

            String resourceName = obj.getString(RESOURCE);

            if (bundle().hasRawRes(resourceName)) {
                rawContracts.add(new RawContract(bundle().getRawRes(resourceName), obj.getInt("amount")));
            } else if (bundle().hasCraftedRes(resourceName)) {
                craftedContracts.add(new CraftedContract(bundle().getCraftedRes(resourceName), obj.getInt("amount")));
            }
        }
    }

    public int getMen() {
        return men;
    }

    public String getHeading() {
        return heading;
    }

    public int getBudget() {
        return budget;
    }

    public List<RawContract> getRawContracts() {
        return rawContracts;
    }

    public List<CraftedContract> getCraftedContracts() {
        return craftedContracts;
    }
}
