import java.util.List;

import datastructures.CLCSInstance;
import datastructures.Node;

public class Upperbound {

    public static int ubMinOccurrence(int sigmaLength, int numInputs, List<Integer> positionVector, int[] inputLengths,
            int[][][] occurrenceTables) {
        int ub = 0;

        boolean[] isOvershoot = getIsOvershoot(numInputs, positionVector, inputLengths);

        for (int label = 0; label < sigmaLength; label++) {
            int labelMin = Integer.MAX_VALUE;

            for (int i = 0; i < numInputs; i++) {
                int inputMin = isOvershoot[i] ? 0 : occurrenceTables[i][label][positionVector.get(i) - 1];
                labelMin = Math.min(labelMin, inputMin);
            }

            ub += labelMin;
        }

        return ub;
    }

    private static boolean[] getIsOvershoot(int numInputs, List<Integer> positionVector, int[] inputLengths) {
        boolean[] isOvershoot = new boolean[numInputs];

        for (int i = 0; i < numInputs; i++) {
            isOvershoot[i] = positionVector.get(i) > inputLengths[i];
        }
        return isOvershoot;
    }

    public static int ubMScore(int minLength, int numInputs, List<Integer> positionVector, int[][][] mScores) {
        int ub = minLength;
        for (int i = 0; i < numInputs - 1; i++) {
            ub = Math.min(ub, mScores[i][positionVector.get(i) - 1][positionVector.get(i + 1) - 1]);
        }
        return ub;
    }

    public static int ubBoth(CLCSInstance inst, Node v) {
        int ubMinOcc = ubMinOccurrence(inst.getSigmaLength(), inst.getNumInputs(), v.getPv(), inst.getInputLengths(),
                inst.getOccurrenceTables());

        int ubMscore = ubMScore(inst.getMinLength(), inst.getNumInputs(), v.getPv(), inst.getmScores());

        int ub = Math.min(ubMinOcc, ubMscore);
        return v.getL_v() + ub;
    }
}
