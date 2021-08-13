package by.bsuir.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;


public class RandomFromFile {

    static private String[] names;

    static private String[] surNames;

    static private String[] nicks;

    final static private Random rnd = new Random();

    public RandomFromFile() {

        names = names();
        surNames = surNames();
        nicks = nicks();
    }

    public static String randomNextNick() {
        if (nicks.length < 1) {
            return null;
        }
        int lengthOfArray = nicks.length;
        return nicks[rnd.nextInt(lengthOfArray)];
    }

    public static String randomNextName() {

        if (names.length < 1) {
            return null;
        }
        int lengthOfArray = names.length;

        return names[rnd.nextInt(lengthOfArray)];
    }

    public static String randomNextSurname() {

        if (surNames.length < 1) {
            return null;
        }
        int lengthOfArray = surNames.length;

        return surNames[rnd.nextInt(lengthOfArray)];
    }

    public static String[] nicks() {
        String filePath =
                "d:" + File.separator + "repository" + File.separator +
                        "java_learn" + File.separator + "HomeTasks" +
                        File.separator + "pet_niks.txt";
        String data = readAllBytes(filePath);

        return data.split("\\s*(\\s|,|!|\\?|;|:|\\(|" +
                "(\\)\\.*\\s*)|\\.+|\\*|(\\s*\"\\.*\\s*))\\s*");
    }

    public static String[] surNames() {
        String filePath = "h:" + File.separator + "disk d"
                + File.separator + "repository" + File.separator + "work_schedule"
                + File.separator + "src" + File.separator + "main"
                + File.separator + "resources"
                + File.separator + "russian_surnames.txt";
        String data = readAllBytes(filePath);

        return data.split("\\s*(\\s|,|!|\\?|;|:|\\(|" +
                "(\\)\\.*\\s*)|\\.+|\\*|(\\s*\"\\.*\\s*))\\s*");
    }

    public static String[] names() {
        String filePath = "h:" + File.separator + "disk d"
                + File.separator + "repository" + File.separator + "work_schedule"
                + File.separator + "src" + File.separator + "main"
                + File.separator + "resources" + File.separator + "names.txt";
        String data = readAllBytes(filePath);

        return data.split("\\s*(\\s|,|!|\\?|;|:|\\(|" +
                "(\\)\\.*\\s*)|\\.+|\\*|(\\s*\"\\.*\\s*))\\s*");
    }

    public static String readAllBytes(String filePath) {
        String data = "";
        try {
            data = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.out.println("Error " + e.getMessage());
        }
        return data;
    }
}
