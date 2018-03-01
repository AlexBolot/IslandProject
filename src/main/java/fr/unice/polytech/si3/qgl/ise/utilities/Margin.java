package fr.unice.polytech.si3.qgl.ise.utilities;

import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD;
import scala.Tuple2;

import java.util.HashMap;

import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.Obstacle.BORDER;
import static fr.unice.polytech.si3.qgl.ise.enums.DroneEnums.ZQSD.*;

/**
 * This class' aim is to remember the range between the drone and the different obstacles/borders
 */
public class Margin {
    private HashMap<ZQSD, Tuple2<Obstacle, Integer>> localMargins;
    private HashMap<ZQSD, Tuple2<Obstacle, Integer>> globalMargins;

    public Margin() {
        this.localMargins = new HashMap<>();
        this.globalMargins = new HashMap<>();
        this.localMargins.put(BACK, new Tuple2<>(BORDER, 0));
        this.globalMargins.put(BACK, new Tuple2<>(BORDER, -1));
        this.globalMargins.put(FRONT, new Tuple2<>(BORDER, -1));
        this.globalMargins.put(LEFT, new Tuple2<>(BORDER, -1));
        this.globalMargins.put(RIGHT, new Tuple2<>(BORDER, -1));
    }

    public void setLocal(ZQSD direction, Obstacle obstacle, int range) {
        Tuple2<Obstacle, Integer> newTuple = new Tuple2<>(obstacle, range);
        this.localMargins.put(direction, newTuple);
    }

    public void setGlobal(ZQSD direction, Obstacle obstacle, int range) {
        Tuple2<Obstacle, Integer> newTuple = new Tuple2<>(obstacle, range);
        this.globalMargins.put(direction, newTuple);
    }

    public Tuple2<Obstacle, Integer> getGlobal(ZQSD direction) {
        return globalMargins.get(direction);
    }

    public Tuple2<Obstacle, Integer> getLocal(ZQSD direction) {
        return localMargins.get(direction);
    }
}
