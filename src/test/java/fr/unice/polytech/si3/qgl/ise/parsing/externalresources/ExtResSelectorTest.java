package fr.unice.polytech.si3.qgl.ise.parsing.externalresources;

import fr.unice.polytech.si3.qgl.ise.parsing.externalresources.ExtResParser.ExtResBundle;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

public class ExtResSelectorTest {

    @Test
    public void selectBundle() {
        try {

            ExtResSelector.selectBundle();

            ExtResBundle defaultBundle = loadDefaultBundle();
            ExtResBundle loadedBundle = ExtResSelector.bundle();

            assertTrue(loadedBundle.containsAllDeep(defaultBundle)); //Loaded bundle must at least do what default does

        } catch (Exception ex) {
            Assert.fail("Exception was raised : " + ex.getMessage());
        }
    }

    private ExtResBundle loadDefaultBundle() throws FileNotFoundException {
        String fullPath;
        ExtResBundle bundle = new ExtResBundle();
        File file;

        URL path = ExtResSelector.class.getClassLoader().getResource("external-resources.json");

        if (path == null) throw new FileNotFoundException("Path is null");

        fullPath = path.getFile();

        file = new File(fullPath);

        if (file.isDirectory()) throw new IllegalStateException(fullPath + " is not a File");

        Scanner sc = new Scanner(file).useDelimiter("you'd never type that, would you \\?");

        ExtResParser extResParser = new ExtResParser();

        if (sc.hasNext()) bundle = extResParser.parse(sc.next());
        if (extResParser.raisedFlag()) throw new IllegalStateException("Default bundle is invalid");

        return bundle;
    }

}