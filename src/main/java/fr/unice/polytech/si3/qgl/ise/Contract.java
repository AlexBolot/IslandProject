package fr.unice.polytech.si3.qgl.ise;

import fr.unice.polytech.si3.qgl.ise.enums.RawResource;

import java.util.Map;

public interface Contract {
    Map<RawResource, Double> getTotalRessourcesToCollect();
}
