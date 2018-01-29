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
        switch (biome) {
            //region Common biomes
            case "OCEAN":
                biomes.add(Biome.OCEAN);
                break;
            case "LAKE":
                biomes.add(Biome.LAKE);
                break;
            case "BEACH":
                biomes.add(Biome.BEACH);
                break;
            case "GRASSLAND":
                biomes.add(Biome.GRASSLAND);
                break;
            //endregion
            //region Tropical biomes
            case "MANGROVE":
                biomes.add(Biome.MANGROVE);
                break;
            case "TROPICAL_RAIN_FOREST":
                biomes.add(Biome.TROPICAL_RAIN_FOREST);
                break;
            case "TROPICAL_SEASONAL_FOREST":
                biomes.add(Biome.TROPICAL_SEASONAL_FOREST);
                break;
            //endregion
            //region Temperate biomes
            case "TEMPERATE_DECIDUOUS_FOREST":
                biomes.add(Biome.TEMPERATE_DECIDUOUS_FOREST);
                break;
            case "TEMPERATE_RAIN_FOREST":
                biomes.add(Biome.TEMPERATE_RAIN_FOREST);
                break;
            case "TEMPERATE_DESERT":
                biomes.add(Biome.TEMPERATE_DESERT);
                break;
            //endregion
            //region Nordic/montain biomes
            case "TAIGA":
                biomes.add(Biome.TAIGA);
                break;
            case "SNOW":
                biomes.add(Biome.SNOW);
                break;
            case "TUNDRA":
                biomes.add(Biome.TUNDRA);
                break;
            case "ALPINE":
                biomes.add(Biome.ALPINE);
                break;
            case "GLACIER":
                biomes.add(Biome.GLACIER);
                break;
            //endregion
            //region Subtropical biomes
            case "SHRUBLAND":
                biomes.add(Biome.SHRUBLAND);
                break;
            case "SUB_TROPICAL_DESERT":
                biomes.add(Biome.SUB_TROPICAL_DESERT);
                break;
            //endregion
            default:
                throw new IllegalArgumentException("This biome doesn't exist");
        }
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
