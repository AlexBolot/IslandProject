package fr.unice.polytech.si3.qgl.ise.parsing;

import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;

public class Scan {
    private int cost;
    private ArrayList<String> creeks = null;
    private ArrayList<String> emergencySites = null;

    public Scan(String scanResult){
        JSONObject data = new JSONObject(scanResult);
        cost = data.getInt("cost");
        JSONObject extras = data.getJSONObject("extras");
        JSONArray creeksJson = extras.getJSONArray("creeks");
        for(int i=0;i<creeksJson.length();i++){
            creeks.add(creeksJson.getString(i));
        }
        JSONArray sitesJson = extras.getJSONArray("sites");
        for(int i=0;i<sitesJson.length();i++){
            emergencySites.add(sitesJson.getString(i));
        }
    }

    public ArrayList<String> getCreeks(){
        return creeks;
    }
    public ArrayList<String> getEmergencySites() {
        return emergencySites;
    }
}
