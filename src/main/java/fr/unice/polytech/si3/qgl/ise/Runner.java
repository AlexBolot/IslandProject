package fr.unice.polytech.si3.qgl.ise;

import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static eu.ace_design.island.runner.Runner.run;

public class Runner {

    /**
     * Run with IDE : args list : maps/map_weekXX.json
     * Run with maven : mvn -q exec:java -Dexec.args="path/to/map_test_week03_2017.json"
     */

    public static void main(String[] args) throws Exception {

        int argc = args.length;
        Path mapLocation;
        int x = 159;
        int y = 159;
        String heading = "NORTH";
        int points = 15000;
        int crew = 7;
        long seed = 0xB8743F260B1D24EFL;
        List<String> resources = new ArrayList<>();
        resources.add("WOOD");
        resources.add("SUGAR_CANE");
        resources.add("QUARTZ");
        resources.add("RUM");
        resources.add("ORE");
        List<Integer> amounts = new ArrayList<>();
        amounts.add(7000);
        amounts.add(400);
        amounts.add(20);
        amounts.add(5);
        amounts.add(4);

        switch (argc) {
            case 7:
                seed = new BigInteger(args[6], 16).longValue();
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
                if (args.length > 7 && args.length % 2 != 0) {
                    mapLocation = Paths.get(args[0]);
                    x = Integer.valueOf(args[1]);
                    y = Integer.valueOf(args[2]);
                    heading = args[3];
                    points = Integer.valueOf(args[4]);
                    crew = Integer.valueOf(args[5]);
                    seed = new BigInteger(args[6], 16).longValue();
                    resources.clear();
                    amounts.clear();
                    for (int i = 7; i < args.length; i += 2) {
                        resources.add(args[i]);
                        amounts.add(Integer.valueOf(args[i + 1]));
                    }
                } else {
                    throw new IllegalArgumentException("Invalid arguments");
                }
        }

        if (!mapLocation.toFile().exists()) {
            throw new IllegalArgumentException("Your map does not exist, aborting");
        }

        run(Explorer.class)
                .exploring(mapLocation.toFile())
                .withName("ise")
                .withSeed(seed)
                .startingAt(x, y, heading)
                .backBefore(points)
                .withCrew(crew)
                .collecting(amounts.get(0 % amounts.size()), resources.get(0 % amounts.size()))
                .collecting(amounts.get(1 % amounts.size()), resources.get(1 % amounts.size()))
                .collecting(amounts.get(2 % amounts.size()), resources.get(2 % amounts.size()))
                .collecting(amounts.get(3 % amounts.size()), resources.get(3 % amounts.size()))
                .collecting(amounts.get(4 % amounts.size()), resources.get(4 % amounts.size()))
                .storingInto(".")
                .fire();
    }
}