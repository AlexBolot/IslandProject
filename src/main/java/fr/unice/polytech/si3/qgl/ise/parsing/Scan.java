package fr.unice.polytech.si3.qgl.ise.parsing;

import fr.unice.polytech.si3.qgl.ise.enums.Biome;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Parses the data obtained through the scan command
 */
public class Scan {
    private int cost;
    private ArrayList<String> creeks;
    private ArrayList<String> emergencySites;
    private ArrayList<Biome> biomes;

    public Scan(String scanResult) {
        creeks = new ArrayList<>();
        emergencySites = new ArrayList<>();
        biomes = new ArrayList<>();
        JSONObject data = new JSONObject(scanResult);
        cost = data.getInt("cost");
        JSONObject extras = data.getJSONObject("extras");
        JSONArray creeksJson = extras.getJSONArray("creeks");
        for (int i = 0; i < creeksJson.length(); i++) {
            creeks.add(creeksJson.getString(i));
        }
        JSONArray sitesJson = extras.getJSONArray("sites");
        for (int i = 0; i < sitesJson.length(); i++) {
            emergencySites.add(sitesJson.getString(i));
        }
        JSONArray biomesJson = extras.getJSONArray("biomes");
        for (int i = 0; i < biomesJson.length(); i++) {
            addBiome(biomesJson.getString(i));
        }
    }

    private void addBiome(String biome) {
        biomes.add(Biome.valueOf(biome));
    }

    public ArrayList<String> getCreeks() {
        return creeks;
    }

    public ArrayList<String> getEmergencySites() {
        return emergencySites;
    }

    public ArrayList<Biome> getBiomes() {
        return biomes;
    }
}
