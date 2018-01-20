package fr.unice.polytech.si3.qgl.ise.maps;

import fr.unice.polytech.si3.qgl.ise.enums.Biome;

import java.util.ArrayList;
import java.util.List;

/**
 * A 10*10 pixels tile as seen by the drone, only containing the information provided by the "SCAN" action.
 */
public class DroneTile extends Tile {
    private String possibleCreek;
    private String possibleSite;
    private List<Biome> possibleBiomes;

    public DroneTile() {
        super();
        possibleCreek = "";
        possibleSite = "";
        possibleBiomes = new ArrayList<>();
    }

    public DroneTile(String possibleCreek, String possibleSite, List<Biome> possibleBiomes) {
        this.possibleCreek = possibleCreek;
        this.possibleSite = possibleSite;
        this.possibleBiomes = new ArrayList<>(possibleBiomes);
    }

    @Override
    public Tile makeCopy() {
        return new DroneTile(possibleCreek, possibleSite, possibleBiomes);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof DroneTile && ((DroneTile) obj).possibleSite.equals(possibleSite) && ((DroneTile) obj).possibleCreek.equals(possibleCreek) && ((DroneTile) obj).possibleBiomes.equals(possibleBiomes));
    }

    @Override
    public int hashCode() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(possibleCreek).append(possibleSite);
        possibleBiomes.forEach(stringBuilder::append);
        stringBuilder.append(getClass());

        return stringBuilder.toString().hashCode();
    }

    public List<Biome> getPossibleBiomes() {
        return new ArrayList<>(possibleBiomes);
    }

    public String getPossibleCreek() {
        return possibleCreek;
    }

    public String getPossibleSite() {
        return possibleSite;
    }

    public void setPossibleCreek(String possibleCreek) {
        this.possibleCreek = possibleCreek;
    }

    public void setPossibleSite(String possibleSite) {
        this.possibleSite = possibleSite;
    }

    public void setPossibleBiomes(List<Biome> possibleBiomes) {
        this.possibleBiomes = new ArrayList<>(possibleBiomes);
    }
}
