package fr.unice.polytech.si3.qgl.ise.contracts;

import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.RawResource;

import java.util.Map;

public interface Contract {
    Map<RawResource, Double> getTotalResourcesToCollect();
}