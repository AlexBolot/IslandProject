package fr.unice.polytech.si3.qgl.ise.parsing.externalresources;

import org.junit.Test;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertTrue;

public class ResourceTest {

    @Test
    public void isSame_Right() {
        for (int i = 0; i < 500; i++) {
            String name = randomString();
            Resource res = new RawResource(name);
            assertTrue(res.sameName(name));
        }
    }

    @Test
    public void isSame_Fail() {
        for (int i = 0; i < 500; i++) {
            String name = randomString();
            String otherName;

            do {
                otherName = randomString();
            } while (otherName.equalsIgnoreCase(name));

            Resource res = new RawResource(name);
            assertTrue(res.sameName(name));
        }
    }

    private String randomString() {
        return randomUUID().toString().replace("-", "");
    }
}