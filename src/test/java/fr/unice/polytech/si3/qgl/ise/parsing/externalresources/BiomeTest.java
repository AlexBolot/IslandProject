package fr.unice.polytech.si3.qgl.ise.parsing.externalresources;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.*;

public class BiomeTest {

    @Test
    public void hasResource_Right() {
        for (int i = 0; i < 500; i++) {
            Biome biome = randomBiome();
            biome.getPossibleResources().stream().map(biome::hasResource).forEach(Assert::assertTrue);
        }
    }

    @Test
    public void hasResource_Fail() {
        Biome biome = randomBiome();
        String newName;

        do {
            newName = randomString();
        } while (biome.getPossibleResources().contains(new RawResource(newName)));

        assertFalse(biome.hasResource(new RawResource(newName)));
    }

    @Test
    public void sameName_Right() {
        for (int i = 0; i < 500; i++) {
            String name = randomString();
            Biome biome = new Biome(name, new ArrayList<>());
            assertTrue(biome.sameName(name));
        }
    }

    @Test
    public void sameName_Fail() {
        for (int i = 0; i < 500; i++) {
            String name = randomString();
            String otherName;

            do {
                otherName = randomString();
            } while (otherName.equalsIgnoreCase(name));

            Biome biome = new Biome(name, new ArrayList<>());
            assertTrue(biome.sameName(name));
        }
    }

    @Test
    public void compareTo() {
        Biome biome1 = new Biome(randomString(), rawResourcesList("WOOD", "FUR", "QUARTZ"));
        Biome biome2 = new Biome(randomString(), rawResourcesList("WOOD", "FUR", "QUARTZ"));
        Biome biome3 = new Biome(randomString(), rawResourcesList("WOOD", "FUR"));
        Biome biome4 = new Biome(randomString(), rawResourcesList("WOOD", "FLOWER", "QUARTZ"));

        assertEquals(0, biome1.compareTo(biome2)); //Since biome1 and biome2 have same resources
        assertEquals(1, biome1.compareTo(biome3)); //Since biome1 contains biome3
        assertEquals(-1, biome3.compareTo(biome1)); //Since biome3 is contained by biome1
        assertEquals(-2, biome1.compareTo(biome4)); //Since biome1 and biome4 are different -> not comparable
    }

    @Test
    public void toString_() {
        Biome biome = randomBiome();

        assertEquals(biome.toString(), biome.getName() + " " + biome.getPossibleResources().toString());
    }

    //region ------------ utils ---------------

    private ArrayList<RawResource> rawResourcesList(String... strings) {
        return Arrays.stream(strings).map(RawResource::new).collect(Collectors.toCollection(ArrayList::new));
    }

    private Biome randomBiome() {
        ArrayList<RawResource> rawResources = new ArrayList<>();
        IntStream.range(0, randomInt()).forEach(i -> rawResources.add(new RawResource(randomString())));
        return new Biome(randomString(), rawResources);
    }

    private String randomString() {
        return randomUUID().toString().replace("-", "");
    }

    private int randomInt() {
        return new Random().nextInt(10) + 5;
    }

    //endregion
}