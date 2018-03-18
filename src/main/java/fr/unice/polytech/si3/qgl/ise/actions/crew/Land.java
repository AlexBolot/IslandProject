package fr.unice.polytech.si3.qgl.ise.actions.crew;

import fr.unice.polytech.si3.qgl.ise.actions.CrewAction;
import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;

public class Land extends CrewAction {


    private String idCreek;
    private Integer crewSize;

    public Land(Crew crewToUpdate, String idCreek, Integer crewSize) {
        super(crewToUpdate);
        this.idCreek = idCreek;
        this.crewSize = crewSize;
    }

    @Override
    public String apply() {
        return new JsonFactory().createJsonString("land", "creek", idCreek,
                "people", crewSize.toString());
    }

    @Override
    public String acknowledgeResults(Crew crewToUpdate, String result) {
        return "";
    }
}
