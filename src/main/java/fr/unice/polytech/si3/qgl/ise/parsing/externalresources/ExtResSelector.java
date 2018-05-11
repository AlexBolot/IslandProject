package fr.unice.polytech.si3.qgl.ise.parsing.externalresources;

import org.apache.logging.log4j.Logger;

import java.io.*;
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

    public static void selectBundle() {
        raisedFlag = false;

        logger.info("Reading in External File");
        ExtResBundle envBundle = readFromEnvironment(System.getenv(ENV_VAR_NAME));
        logger.info("Reading in Default File");
        ExtResBundle defaultBundle = readFromResource(DEFAULT_PATH);

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


    private static ExtResBundle readFromResource(String pathToFile) {
        String str;
        StringBuilder builder = new StringBuilder();

        try (InputStream is = ExtResSelector.class.getClassLoader().getResourceAsStream(pathToFile)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while ((str = reader.readLine()) != null) {
                builder.append(str).append("\n");
            }
        } catch (IOException e) {
            logger.error("Problem in readFromResource : ", e);
        }

        ExtResParser extResParser = new ExtResParser();

        ExtResBundle bundle = extResParser.parse(builder.toString());
        if (extResParser.raisedFlag()) raisedFlag = true;

        return bundle;
    }

    private static ExtResBundle readFromEnvironment(String pathToFile) {

        ExtResBundle bundle = new ExtResBundle();
        File file = new File("");

        try {
            file = new File(pathToFile);
            if (file.isDirectory()) throw new IllegalStateException(pathToFile + " is not a File");
        } catch (Exception e) {
            raisedFlag = true;
            logger.error("Problem while loading resource : ", e);
        }

        try (Scanner sc = new Scanner(file).useDelimiter("you'd never type that, would you \\?")) {

            ExtResParser extResParser = new ExtResParser();

            if (sc.hasNext()) bundle = extResParser.parse(sc.next());
            if (extResParser.raisedFlag()) raisedFlag = true;

        } catch (Exception e) {
            raisedFlag = true;
            logger.error("Problem while loading resource : ", e);
        }

        return bundle;
    }

    private static void setResBundle(ExtResBundle bundle) {
        globalResBundle = bundle;
    }
}
