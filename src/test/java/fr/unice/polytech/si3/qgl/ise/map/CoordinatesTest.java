package fr.unice.polytech.si3.qgl.ise.map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CoordinatesTest {

    @Test
    public void equalsTest() {
        assertEquals(new Coordinates(0, 0), new Coordinates(0, 0));
        assertEquals(new Coordinates(48478, -464), new Coordinates(48478, -464));
        assertNotEquals(new Coordinates(0, 0), new Coordinates(48478, -464));
        assertNotEquals(new Coordinates(48478, -464), new Coordinates(0, 0));
        assertNotEquals(new Coordinates(48478, -464), new Object());
    }

    @Test
    public void hashCodeTest() {
        assertEquals(new Coordinates(0, 0).hashCode(), new Coordinates(0, 0).hashCode());
        assertEquals(new Coordinates(48478, -464).hashCode(), new Coordinates(48478, -464).hashCode());
        assertNotEquals(new Coordinates(0, 0).hashCode(), new Coordinates(48478, -464).hashCode());
        assertNotEquals(new Coordinates(48478, -464).hashCode(), new Coordinates(0, 0).hashCode());
        assertNotEquals(new Coordinates(48478, -464).hashCode(), new Object().hashCode());
    }
}
