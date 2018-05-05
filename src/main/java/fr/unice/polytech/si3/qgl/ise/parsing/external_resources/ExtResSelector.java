package fr.unice.polytech.si3.qgl.ise.parsing.external_resources;

import fr.unice.polytech.si3.qgl.ise.AppDraft;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Scanner;

public class ExtResSelector {

    private static final String DEFAULT_PATH = "external-resources.json";
    private static final String ENV_VAR_NAME = "ISE_EXT_RES"; // Island Explorer External Resources

    private boolean raisedFlag = false;

    public void selectBundle() {
        raisedFlag = false;

        selectBundle(System.getenv(ENV_VAR_NAME), false);

        if (raisedFlag) selectBundle(DEFAULT_PATH, true);

        System.out.println("Chose to read in : " + (raisedFlag ? "Default File" : "Env Var File"));
        System.out.println("All went good :D");
    }

    private void selectBundle(String pathToFile, boolean fromResources) {

        String fullPath;
        Scanner sc = null;

        try {
            if (fromResources) {
                URL path = AppDraft.class.getClassLoader().getResource(pathToFile);

                if (path == null) throw new FileNotFoundException("Path is null");

                fullPath = path.getFile();
            } else {
                fullPath = pathToFile;
            }

            File file = new File(fullPath);

            if (file.isDirectory()) throw new IllegalStateException(fullPath + " is not a File");

            sc = new Scanner(file).useDelimiter("you'd never type that, would you \\?");
            ExtResParser extResParser = new ExtResParser();

            if (sc.hasNext()) extResParser.parse(sc.next());
            if (extResParser.raisedFlag()) raisedFlag = true;

        } catch (Exception ex) {
            raisedFlag = true;
            if (sc != null) sc.close();
        }
    }

}
