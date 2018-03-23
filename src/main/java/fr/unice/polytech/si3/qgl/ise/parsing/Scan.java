package fr.unice.polytech.si3.qgl.ise.parsing;

import fr.unice.polytech.si3.qgl.ise.enums.Biome;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.stream.IntStream;

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
        JSONArray sitesJson = extras.getJSONArray("sites");
        JSONArray biomesJson = extras.getJSONArray("biomes");

        IntStream.range(0, creeksJson.length()).forEach(i -> creeks.add(creeksJson.getString(i)));
        IntStream.range(0, sitesJson.length()).forEach(i -> emergencySites.add(sitesJson.getString(i)));
        IntStream.range(0, biomesJson.length()).mapToObj(biomesJson::getString).forEach(this::addBiome);
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

    public int getCost() {
        return cost;
    }
}
