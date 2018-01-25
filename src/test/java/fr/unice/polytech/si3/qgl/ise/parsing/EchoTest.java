package fr.unice.polytech.si3.qgl.ise.parsing;

import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EchoTest {

    @Test
    public void parsingTest() {
        String dataIn = "{ \"cost\": 1, \"extras\": { \"range\": 2, \"found\": \"GROUND\" }, \"status\": \"OK\" }";
        Echo echo = new Echo(dataIn);
        assertEquals(1, echo.getCost());
        assertEquals(2, echo.getRange());
        assertEquals(DroneEnums.Obstacle.GROUND, echo.getObstacle());
    }

}
