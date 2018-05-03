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
     * Run with maven : mvn -q exec:java -Dexec.args="path/to/map_weekXX.json"
     */

    public static void main(String[] args) throws Exception {

        int argc = args.length;
        Path mapLocation;
        int x = 1;
        int y = 1;
        String heading = "EAST";
        int points = 10000;
        int crew = 4;
        long seed = 0xFB9DDB405E0071F8L;
        List<String> resources = new ArrayList<>();
        resources.add("FUR");
        resources.add("SUGAR_CANE");
        resources.add("GLASS");
        resources.add("WOOD");
        List<Integer> amounts = new ArrayList<>();
        amounts.add(500);
        amounts.add(400);
        amounts.add(15);
        amounts.add(500);

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

        AfterRunBench afterRunBench = new AfterRunBench();
        afterRunBench.fill();
        afterRunBench.compute();
        afterRunBench.display();
    }
}