import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import datastructures.CLCSInstance;

public class RunFolder {
    private static final String LOG_FILE = "length_mismatch_log.txt";
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java RunFolder \"testcase/artificial\"");
            return;
        }
        String dir = args[0];
        System.out.println(dir);
        runFolder(dir);
    }

    public static void runFolder(String path) throws IOException {
        File dir = new File(path);
        File[] files = dir.listFiles();
        int i = 0;
        System.out.println(dir);
        if (files == null) {
            System.out.println("Directory not found or cannot be read: " + path);
            return;
        }
        for (File file : files) {
            if (file.isFile()) {
                System.out.println("(" + i++ + ") Running " + file.getPath());
                // Extract strings from the current file
                List<String> lines = extractStrings(file.getPath());
                String raw_p_con = lines.get(0);
                String raw_s0 = lines.get(1);
                String raw_s1 = lines.get(2);
                // Run solveCLCS for the extracted strings
                CLCSInstance clcsInstance = StringsSolver.createInstanceFromStrings(raw_p_con, raw_s0, raw_s1);
                String deoResult = StringsSolver.DEO_Solver(clcsInstance);
                String astarResult = StringsSolver.AStar_Solver(clcsInstance);

                int deoLength = deoResult.length();
                int astarLength = astarResult.length();

                System.out.println("[DP_DEO](" + deoLength + ") " + deoResult);
                System.out.println("[A_STAR](" + astarLength + ") " + astarResult);
                System.out.println("Match str: " + deoResult.equals(astarResult));
                System.out.println("Match len: " + (deoLength == astarLength));

                if (deoLength != astarLength) {
                    logLengthMismatch(file.getPath(), raw_p_con, raw_s0, raw_s1, deoResult, astarResult);
                }

                System.out.println(clcsInstance.getIndexToChar());

                System.out.println();
            }
        }
    }

    private static List<String> extractStrings(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        List<String> strings = new ArrayList<>();
        // Skip the first line as it contains integers
        reader.readLine();
        String line;
        while ((line = reader.readLine()) != null) {
            // Split each line into an integer and a string, and add the string to the list
            String[] parts = line.split("\\s+");
            if (parts.length > 1) { // Check if there is a string part in the line
                strings.add(parts[1]);
            }
        }
        reader.close();
        return strings;
    }

    private static void logLengthMismatch(String filePath, String p_con, String s0, String s1, String deoResult, String astarResult) {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println("File: " + filePath);
            out.println("p_con: " + p_con);
            out.println("s0: " + s0);
            out.println("s1: " + s1);
            out.println("DEO Result (" + deoResult.length() + "): " + deoResult);
            out.println("A* Result (" + astarResult.length() + "): " + astarResult);
            out.println("--------------------");
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
}
