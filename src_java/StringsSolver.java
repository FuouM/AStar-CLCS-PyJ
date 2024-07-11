
// import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import datastructures.AStarSolution;
import datastructures.CLCSInstance;
import parsingUtils.GenericsParsing;
import parsingUtils.StringsParsing;
import src_DEO.CLCS_DP_DEO;
import src_DEO.DeoInstance;

public class StringsSolver {

    public static CLCSInstance createInstanceFromStrings(String constraint, String inputI, String inputJ) {
        List<Character> charConstraint = StringsParsing.charsFromString(constraint);
        List<Character> charInputI = StringsParsing.charsFromString(inputI);
        List<Character> charInputJ = StringsParsing.charsFromString(inputJ);

        List<Character> alphabetSigma = GenericsParsing.getSigma(charInputI, charInputJ);

        int sigmaLength = alphabetSigma.size();
        // Obtain the Sigma and Inverse Sigma mapping
        Map<Character, Integer> charToIndex = GenericsParsing.getItemToIndexMap(alphabetSigma);
        Map<Integer, Character> indexToChar = GenericsParsing.getIndexToItemMap(alphabetSigma);

        // Apply the Sigma mapping to obtain the sequences of integers as inputs for the
        // algorithm
        List<Integer> intConstraint = GenericsParsing.mapItems(charConstraint, charToIndex);
        List<Integer> intInputI = GenericsParsing.mapItems(charInputI, charToIndex);
        List<Integer> intInputJ = GenericsParsing.mapItems(charInputJ, charToIndex);

        List<List<Integer>> inputs = new ArrayList<>();
        inputs.add(intInputI);
        inputs.add(intInputJ);

        long startTime = System.nanoTime();
        CLCSInstance inst = new CLCSInstance(sigmaLength, intConstraint, inputs);
        long endTime = System.nanoTime();
        long initTimeNS = endTime - startTime;
        long initTimeMS = TimeUnit.MILLISECONDS.convert(initTimeNS, TimeUnit.NANOSECONDS);
        System.out.println("Init took: " + initTimeMS + " ms");

        inst.setCharToIndex(charToIndex);
        inst.setIndexToChar(indexToChar);

        return inst;

        // // Create a new list to hold the items in sequence that are also in alphabet
        // List<Character> validConstraint = new ArrayList<>(charConstraint);

        // // Remove items from validSequence that are not in alphabet
        // validConstraint.removeIf(item -> !alphabetSigma.contains(item));

    }

    public static String DEO_Solver(CLCSInstance clcsInst) {
        long startTime = System.nanoTime();

        CLCS_DP_DEO solver = new CLCS_DP_DEO();
        DeoInstance deoInst = CLCS2Deo(clcsInst);
        solver.inst = deoInst;
        List<Integer> result = solver.DP_Deo();

        long endTime = System.nanoTime();
        long initTimeNS = endTime - startTime;
        long initTimeMS = TimeUnit.MILLISECONDS.convert(initTimeNS, TimeUnit.NANOSECONDS);
        System.out.println("DEO_DP took: " + initTimeMS + " ms");
        return StringsParsing.stringFromInt(result, clcsInst.getIndexToChar());
    }

    public static String AStar_Solver(CLCSInstance clcsInst) {
        long startTime = System.nanoTime();

        AStarSolution astarSolution = AStar.AStarRun(clcsInst);
        List<Integer> result = astarSolution.solutions.get(0);

        long endTime = System.nanoTime();
        long initTimeNS = endTime - startTime;
        long initTimeMS = TimeUnit.MILLISECONDS.convert(initTimeNS, TimeUnit.NANOSECONDS);
        System.out.println("A_STAR took: " + initTimeMS + " ms");
        return StringsParsing.stringFromInt(result, clcsInst.getIndexToChar());
    }

    private static DeoInstance CLCS2Deo(CLCSInstance inst) {
        DeoInstance deoInst = new DeoInstance();
        int[] sigmaInts = new int[inst.getSigmaLength()];
        for (int j = 0; j < inst.getSigmaLength(); j++) {
            sigmaInts[j] = j;
        }
        deoInst.sigma = sigmaInts;
        deoInst.S = new int[][] { listToIntArray(inst.getInputs().get(0)), listToIntArray(inst.getInputs().get(1)) };
        deoInst.p = inst.getConstraintLength();
        deoInst.P = listToIntArray(inst.getConstraint());

        return deoInst;
    }

    public static int[] listToIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        int i = 0;
        for (Integer e : list)
            ret[i++] = e;
        return ret;
    }

}
