package fr.unice.polytech.si3.qgl.ise;

import java.nio.file.Path;
import java.nio.file.Paths;

import static eu.ace_design.island.runner.Runner.run;

public class Runner {

    /**
     * Run with IDE : args list : ressources/map_test_week03_2017.json
     * Run with maven : mvn -q exec:java -Dexec.args="path/to/map_test_week03_2017.json"
     */
    public static void main(String[] args) throws Exception {

        if (args.length != 1) {
            throw new IllegalArgumentException("No map available, aborting");
        }

        Path mapLocation = Paths.get(args[0]);
        if (!mapLocation.toFile().exists()) {
            System.out.println(System.getProperty("user.dir"));
            throw new IllegalArgumentException("Your map does not exist, aborting");
        }

        run(Explorer.class)
                .exploring(mapLocation.toFile())
                .withName("ise")
                .withSeed(0x161D552A4A22E2A1L)
                .startingAt(1, 1, "SOUTH")
                .backBefore(30000)
                .withCrew(15)
                .collecting(2000, "WOOD")
                .collecting(100, "FUR")
                .collecting(400, "QUARTZ")
                .collecting(2, "RUM")
                .storingInto(".")
                .fire();
    }
}