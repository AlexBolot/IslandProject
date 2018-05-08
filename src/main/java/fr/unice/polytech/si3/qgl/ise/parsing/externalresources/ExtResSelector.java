package fr.unice.polytech.si3.qgl.ise.parsing.externalresources;

import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Scanner;

import static fr.unice.polytech.si3.qgl.ise.parsing.externalresources.ExtResParser.ExtResBundle;
import static org.apache.logging.log4j.LogManager.getLogger;

public class ExtResSelector {

    private static final Logger logger = getLogger(ExtResSelector.class);
    private static final String DEFAULT_PATH = "external-resources.json";
    private static final String ENV_VAR_NAME = "ISE_EXT_RES";
    private static ExtResBundle globalResBundle = null;

    private static boolean raisedFlag = false;

    private ExtResSelector() { /*Empty private constructor to hide the implicit public one*/ }

    public static ExtResBundle bundle() {
        if (globalResBundle == null)
            selectBundle();
        return globalResBundle;
    }

    private static void setResBundle(ExtResBundle bundle) {
        globalResBundle = bundle;
    }

    public static void selectBundle() {
        raisedFlag = false;

        logger.info("Reading in External File");
        ExtResBundle envBundle = selectBundle(System.getenv(ENV_VAR_NAME), false);
        logger.info("Reading in Default File");
        ExtResBundle defaultBundle = selectBundle(DEFAULT_PATH, true);

        ExtResBundle selectedBunlde;

        if (raisedFlag || envBundle.compareDeep(defaultBundle) < 0) {
            logger.info("Selected Default File");
            selectedBunlde = defaultBundle;
        } else {
            logger.info("Selected External File");
            selectedBunlde = defaultBundle;
        }

        setResBundle(selectedBunlde);
    }

    private static ExtResBundle selectBundle(String pathToFile, boolean fromResources) {

        String fullPath;
        ExtResBundle bundle = new ExtResBundle();
        File file = new File("");

        try {
            if (fromResources) {
                URL path = ExtResSelector.class.getClassLoader().getResource(pathToFile);

                if (path == null) throw new FileNotFoundException("Path is null");

                fullPath = path.getFile();
            } else {
                fullPath = pathToFile;
            }

            file = new File(fullPath);

            if (file.isDirectory()) throw new IllegalStateException(fullPath + " is not a File");

        } catch (Exception ex) {
            raisedFlag = true;
            logger.info("Problem while loading resoure : " + ex.getMessage());
        }

        try (Scanner sc = new Scanner(file).useDelimiter("you'd never type that, would you \\?")) {

            ExtResParser extResParser = new ExtResParser();

            if (sc.hasNext()) bundle = extResParser.parse(sc.next());
            if (extResParser.raisedFlag()) raisedFlag = true;

        } catch (Exception ex) {
            raisedFlag = true;
            logger.info("Problem while loading resoure : " + ex.getMessage());
        }

        return bundle;
    }
}
