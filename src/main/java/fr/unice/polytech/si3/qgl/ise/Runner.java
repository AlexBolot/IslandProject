package fr.unice.polytech.si3.qgl.ise;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static eu.ace_design.island.runner.Runner.run;

public class Runner {

    /**
     * Run with IDE : args list : ressources/map_test_week03_2017.json
     * Run with maven : mvn -q exec:java -Dexec.args="path/to/map_test_week03_2017.json"
     */

    public static void main(String[] args) throws Exception {

        int argc = args.length;
        Path mapLocation;
        int x = 1;
        int y = 1;
        String heading = "SOUTH";
        int points = 30000;
        int crew = 15;
        List<String> resources = new ArrayList<>();
        resources.add("WOOD");
        resources.add("RUM");
        resources.add("FUR");
        resources.add("QUARTZ");
        List<Integer> amounts = new ArrayList<>();
        amounts.add(5000);
        amounts.add(10);
        amounts.add(200);
        amounts.add(100);

        switch (argc) {
            case 6:
                crew = Integer.valueOf(args[5]);
            case 5:
                points = Integer.valueOf(args[4]);
            case 4:
                x = Integer.valueOf(args[1]);
                y = Integer.valueOf(args[2]);
                heading = args[3];
            case 1:
                mapLocation = Paths.get(args[0]);
                break;
            default:
                if (args.length > 6 && args.length % 2 == 0) {
                    mapLocation = Paths.get(args[0]);
                    x = Integer.valueOf(args[1]);
                    y = Integer.valueOf(args[2]);
                    heading = args[3];
                    points = Integer.valueOf(args[4]);
                    crew = Integer.valueOf(args[5]);
                    resources.clear();
                    amounts.clear();
                    for (int i = 6; i < args.length; i += 2) {
                        resources.add(args[i]);
                        amounts.add(Integer.valueOf(args[i + 1]));
                    }
                } else {
                    throw new IllegalArgumentException("Invalid arguments");
                }
        }

        if (!mapLocation.toFile().exists()) {
            System.out.println(System.getProperty("user.dir"));
            throw new IllegalArgumentException("Your map does not exist, aborting");
        }

        run(Explorer.class)
                .exploring(mapLocation.toFile())
                .withName("ise")
                .withSeed(0x161D552A4A22E2A1L)
                .startingAt(x, y, heading)
                .backBefore(points)
                .withCrew(crew)
                .collecting(amounts.get(0 % amounts.size()), resources.get(0 % amounts.size()))
                .collecting(amounts.get(1 % amounts.size()), resources.get(1 % amounts.size()))
                .collecting(amounts.get(2 % amounts.size()), resources.get(2 % amounts.size()))
                .collecting(amounts.get(3 % amounts.size()), resources.get(3 % amounts.size()))
                .storingInto(".")
                .fire();
    }
}