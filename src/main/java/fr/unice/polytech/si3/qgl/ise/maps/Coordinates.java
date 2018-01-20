package fr.unice.polytech.si3.qgl.ise.maps;

/**
 * A class that allows the identification the location of the tiles on the map
 */
public class Coordinates {
    private int x;
    private int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Coordinates && ((Coordinates) obj).x == x && ((Coordinates) obj).y == y);
    }

    @Override
    public int hashCode() {
        return (String.valueOf(x) + String.valueOf(y) + getClass()).hashCode();
    }
}
