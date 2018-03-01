package fr.unice.polytech.si3.qgl.ise.actions;

import fr.unice.polytech.si3.qgl.ise.factories.JsonFactory;

public abstract class StopAction extends Action {
    public static String get() {
        return new JsonFactory().createJsonString("stop");
    }
}
