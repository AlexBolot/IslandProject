package fr.unice.polytech.si3.qgl.ise.actions.simple;

import fr.unice.polytech.si3.qgl.ise.entities.Drone;
import fr.unice.polytech.si3.qgl.ise.enums.DroneEnums;
import fr.unice.polytech.si3.qgl.ise.map.Coordinates;
import fr.unice.polytech.si3.qgl.ise.map.IslandMap;
import fr.unice.polytech.si3.qgl.ise.parsing.Scan;
import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.Biome;
import org.junit.Test;

import static fr.unice.polytech.si3.qgl.ise.parsing.externalresources.ExtResSelector.bundle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ScanActionTest {

    @Test
    public void correct() {
        Drone drone = new Drone(new IslandMap(), DroneEnums.NSEW.NORTH);
        ScanAction scanAction = new ScanAction(drone);
        String result = scanAction.apply();
        assertEquals("{\"action\":\"scan\"}", result);
    }

    @Test
    public void testProbability() {

        Biome glacier = bundle().getBiome("GLACIER");
        Biome alpine = bundle().getBiome("ALPINE");

        IslandMap map = new IslandMap();
        Drone drone = new Drone(map, DroneEnums.NSEW.NORTH);
        drone.acknowledgeScan(new Scan("{\"cost\": 2, \"extras\": { \"biomes\": [\"GLACIER\", \"ALPINE\"], " +
                "\"creeks\": [], \"sites\": []}, \"status\": \"OK\"}"));
        assertEquals(IslandMap.getPercentageOfLayerForUpdate()[0],
                map.getTile(new Coordinates(0, 0)).getBiomesPercentage().get(glacier), 0.01);

        assertEquals(IslandMap.getPercentageOfLayerForUpdate()[1],
                map.getTile(new Coordinates(2, 0)).getBiomesPercentage().get(glacier), 0.01);

        assertEquals(IslandMap.getPercentageOfLayerForUpdate()[2],
                map.getTile(new Coordinates(3, 0)).getBiomesPercentage().get(glacier), 0.01);

        assertEquals(IslandMap.getPercentageOfLayerForUpdate()[3],
                map.getTile(new Coordinates(4, 0)).getBiomesPercentage().get(glacier), 0.01);

        assertEquals(IslandMap.getPercentageOfLayerForUpdate()[4],
                map.getTile(new Coordinates(3, 3)).getBiomesPercentage().get(glacier), 0.01);

        assertEquals(IslandMap.getPercentageOfLayerForUpdate()[5],
                map.getTile(new Coordinates(3, 4)).getBiomesPercentage().get(glacier), 0.01);

        assertEquals(IslandMap.getPercentageOfLayerForUpdate()[6],
                map.getTile(new Coordinates(4, 4)).getBiomesPercentage().get(glacier), 0.01);

        //With only an alpine scan, Glacier prob must not change
        drone.acknowledgeScan(new Scan("{\"cost\": 2, \"extras\": { \"biomes\": [\"ALPINE\"], " +
                "\"creeks\": [], \"sites\": []}, \"status\": \"OK\"}"));

        //region Test that nothing changed on Glacier
        assertEquals(IslandMap.getPercentageOfLayerForUpdate()[0],
                map.getTile(new Coordinates(0, 0)).getBiomesPercentage().get(glacier), 0.01);

        assertEquals(IslandMap.getPercentageOfLayerForUpdate()[1],
                map.getTile(new Coordinates(2, 0)).getBiomesPercentage().get(glacier), 0.01);

        assertEquals(IslandMap.getPercentageOfLayerForUpdate()[2],
                map.getTile(new Coordinates(3, 0)).getBiomesPercentage().get(glacier), 0.01);

        assertEquals(IslandMap.getPercentageOfLayerForUpdate()[3],
                map.getTile(new Coordinates(4, 0)).getBiomesPercentage().get(glacier), 0.01);

        assertEquals(IslandMap.getPercentageOfLayerForUpdate()[4],
                map.getTile(new Coordinates(3, 3)).getBiomesPercentage().get(glacier), 0.01);

        assertEquals(IslandMap.getPercentageOfLayerForUpdate()[5],
                map.getTile(new Coordinates(3, 4)).getBiomesPercentage().get(glacier), 0.01);

        assertEquals(IslandMap.getPercentageOfLayerForUpdate()[6],
                map.getTile(new Coordinates(4, 4)).getBiomesPercentage().get(glacier), 0.01);

        //endregion

        assertEquals(100, map.getTile(new Coordinates(0, 0)).getBiomesPercentage().get(alpine), 0.01);

        double percentage = IslandMap.getPercentageOfLayerForUpdate()[1];
        assertEquals(percentage + (((100d - percentage) * percentage) / 100),
                map.getTile(new Coordinates(2, 0)).getBiomesPercentage().get(alpine), 0.01);

        percentage = IslandMap.getPercentageOfLayerForUpdate()[2];
        assertEquals(percentage + (((100d - percentage) * percentage) / 100),
                map.getTile(new Coordinates(3, 0)).getBiomesPercentage().get(alpine), 0.01);

        percentage = IslandMap.getPercentageOfLayerForUpdate()[3];
        assertEquals(percentage + (((100d - percentage) * percentage) / 100),
                map.getTile(new Coordinates(4, 0)).getBiomesPercentage().get(alpine), 0.01);

        percentage = IslandMap.getPercentageOfLayerForUpdate()[4];
        assertEquals(percentage + (((100d - percentage) * percentage) / 100),
                map.getTile(new Coordinates(3, 3)).getBiomesPercentage().get(alpine), 0.01);

        percentage = IslandMap.getPercentageOfLayerForUpdate()[5];
        assertEquals(percentage + (((100d - percentage) * percentage) / 100),
                map.getTile(new Coordinates(3, 4)).getBiomesPercentage().get(alpine), 0.01);

        percentage = IslandMap.getPercentageOfLayerForUpdate()[6];
        assertEquals(percentage + (((100d - percentage) * percentage) / 100),
                map.getTile(new Coordinates(4, 4)).getBiomesPercentage().get(alpine), 0.01);


        //Test that we don't exceed 100%
        for (int i = 0; i < 20; ++i)
            drone.acknowledgeScan(new Scan("{\"cost\": 2, \"extras\": { \"biomes\": [\"GLACIER\", \"ALPINE\"], " +
                    "\"creeks\": [], \"sites\": []}, \"status\": \"OK\"}"));
        assertTrue(map.getTile(new Coordinates(2, 0)).getBiomesPercentage().get(glacier) < 100);

    }
}
