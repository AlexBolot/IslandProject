package fr.unice.polytech.si3.qgl.ise.actions.crew;

import fr.unice.polytech.si3.qgl.ise.actions.CrewAction;
import fr.unice.polytech.si3.qgl.ise.entities.Crew;
import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;

public class Land extends CrewAction {


    private final String idCreek;

    public Land(Crew crewToUpdate, String idCreek) {
        super(crewToUpdate);
        this.idCreek = idCreek;
    }

    @Override
    public String apply() {
        this.finish();
        return new JsonFactory().createJsonString("land", "creek", idCreek, "people", 1 + "");
    }

    @Override
    public String acknowledgeResults(String result) {
        return "";
    }
}
