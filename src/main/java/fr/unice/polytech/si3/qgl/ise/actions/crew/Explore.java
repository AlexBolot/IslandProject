package fr.unice.polytech.si3.qgl.ise.actions.crew;

import fr.unice.polytech.si3.qgl.ise.actions.CrewAction;
import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;
import fr.unice.polytech.si3.qgl.ise.parsing.ExploreParsing;

public class Explore extends CrewAction {

    public Explore(Crew crewToUpdate) {
        super(crewToUpdate);
    }

    @Override
    public String apply() {
        return new JsonFactory().createJsonString("explore");
    }

    @Override
    public String acknowledgeResults(Crew crewToUpdate, String result) {
        ExploreParsing exploreParsing = new ExploreParsing(result);
        crewToUpdate.getMap().getTile(crewToUpdate.getCoords()).setResourcesStats(exploreParsing.getResources());
        return "";
    }
}
