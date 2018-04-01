package fr.unice.polytech.si3.qgl.ise.parsing;

import fr.unice.polytech.si3.qgl.ise.CraftedContract;
import fr.unice.polytech.si3.qgl.ise.RawContract;
import fr.unice.polytech.si3.qgl.ise.enums.CraftedResource;
import fr.unice.polytech.si3.qgl.ise.enums.RawResource;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
            if (RawResource.contains(obj.getString(RESOURCE))) {
                rawContracts.add(new RawContract(RawResource.valueOf(obj.getString(RESOURCE)), obj.getInt("amount")));
            } else {
                craftedContracts.add(new CraftedContract(CraftedResource.valueOf(obj.getString(RESOURCE)), obj.getInt("amount")));
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
