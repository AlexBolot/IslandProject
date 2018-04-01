package fr.unice.polytech.si3.qgl.ise.parsing;

import fr.unice.polytech.si3.qgl.ise.enums.Biome;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Parses the data obtained through the scan command
 */
public class Scan {
    private final List<String> creeks;
    private final List<String> emergencySites;
    private final List<Biome> biomes;

    public Scan(String scanResult) {
        creeks = new ArrayList<>();
        emergencySites = new ArrayList<>();
        biomes = new ArrayList<>();

        JSONObject data = new JSONObject(scanResult);

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

    public List<String> getCreeks() {
        return creeks;
    }

    public List<String> getEmergencySites() {
        return emergencySites;
    }

    public List<Biome> getBiomes() {
        return biomes;
    }

}
