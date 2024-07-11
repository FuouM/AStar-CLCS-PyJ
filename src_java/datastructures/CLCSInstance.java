package datastructures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CLCSInstance {
    private int sigmaLength;
    private List<Integer> constraint = new ArrayList<>();
    private List<List<Integer>> inputs = new ArrayList<>();
    private int numInputs;
    private int[] inputLengths;
    private int maxLength;
    private int minLength;
    private int constraintLength;
    private int[][][] successorTables;
    private int[][][] occurrenceTables;
    private int[][] embedTables;
    private int[][][] mScores;
    private Map<Character, Integer> charToIndex;
    private Map<Integer, Character> indexToChar;

    public CLCSInstance(int sigmaLength, List<Integer> constraint, List<List<Integer>> inputs) {
        this.sigmaLength = sigmaLength;
        this.constraint = constraint;
        this.inputs = inputs;
        this.numInputs = inputs.size();
        this.constraintLength = constraint.size();

        initInputLengths();
        initTables();
        populateTables();

        this.mScores = buildMScores(this.numInputs, this.inputLengths, this.inputs);
    }

    private void initTables() {
        this.successorTables = new int[this.numInputs][][];
        this.embedTables = new int[this.numInputs][];
        this.occurrenceTables = new int[this.numInputs][][];
    }

    private void initInputLengths() {
        int maxLength = -1;
        int minLength = Integer.MAX_VALUE;
        this.inputLengths = new int[this.numInputs];
        for (int i = 0; i < this.numInputs; i++) {
            int currLength = this.inputs.get(i).size();
            this.inputLengths[i] = currLength;

            maxLength = Math.max(maxLength, currLength);
            minLength = Math.min(minLength, currLength);
        }
        this.maxLength = maxLength;
        this.minLength = minLength;
    }

    private void populateTables() {
        for (int i = 0; i < this.numInputs; i++) {
            this.successorTables[i] = buildSuccessor(this.sigmaLength, this.maxLength, this.inputs.get(i));
            this.embedTables[i] = buildEmbed(this.constraintLength, this.constraint, this.inputs.get(i));
            this.occurrenceTables[i] = buildOccurrence(this.sigmaLength, this.maxLength, this.inputs.get(i));
        }
    }

    public static int[][] buildSuccessor(int sigmaLength, int maxLength, List<Integer> input) {
        int[][] successorTable = new int[sigmaLength][maxLength];
        for (int label = 0; label < sigmaLength; label++) {
            // Initialize the successor indices to +inf,
            // also indicates the label does not appear again
            int number = Integer.MAX_VALUE;
            Arrays.fill(successorTable[label], Integer.MAX_VALUE);

            // Iterate over each element in the input sequence in reverse
            for (int i = input.size() - 1; i >= 0; i--) {
                // If the current element equals the current label
                if (input.get(i).equals(label)) {
                    // Update the successor index to the current index
                    number = i;
                }
                // Set the successor index of the label at position i
                successorTable[label][i] = number;
            }
        }
        return successorTable;
    }

    public static int[] buildEmbed(int constraintLength, List<Integer> constraint, List<Integer> input) {
        int[] embed = new int[constraintLength];
        Arrays.fill(embed, Integer.MAX_VALUE);

        // Index for the constraint sequence
        int pIdx = constraintLength - 1;

        // Iterate over each element in the input sequence in reverse
        // finds its last occurrence in the input sequence.
        for (int i = input.size() - 1; i >= 0 && pIdx >= 0; i--) {
            // If the current element equals the current element in the constraint sequence
            if (input.get(i).equals(constraint.get(pIdx))) {
                // Store the position of the match in the Embed table
                embed[pIdx] = i;

                // Move to the previous item of the constraint sequence
                pIdx--;
            }
        }

        return embed;
    }

    public static int[][] buildOccurrence(int sigmaLength, int maxLength, List<Integer> input) {
        int[][] occurrenceTable = new int[sigmaLength][maxLength];
        int[] count = new int[sigmaLength];

        // Iterate over each element in the input sequence in reverse order
        for (int i = input.size() - 1; i >= 0; i--) {
            // Increment the count for the current element
            count[input.get(i)] += 1;

            // Update the Occurrence table for each label with
            // the number of occurrences of each label from this position to end.
            for (int label = 0; label < sigmaLength; label++) {
                occurrenceTable[label][i] = count[label];
            }
        }

        return occurrenceTable;
    }

    public static int[][] LCS_MScore_IJ(int lenI, int lenJ, List<Integer> inputI, List<Integer> inputJ) {
        int[][] m_IJ = new int[lenI + 1][lenJ + 1];

        for (int x = lenI - 1; x >= 0; x--) {
            for (int y = lenJ - 1; y >= 0; y--) {
                // If the current elements in both sequences are equal
                // s_1[i] = s_2[i]
                if (inputI.get(x).equals(inputJ.get(y))) {
                    // Increment the M-score
                    // M[i-1,j-1] + 1
                    m_IJ[x][y] = m_IJ[x + 1][y + 1] + 1;
                } else { // s_1[i] != s_2[i]
                    // Set the M-score to the maximum M-score of the previous elements in the
                    // sequences
                    // max(M[i,j-1], M[i-1, j])
                    m_IJ[x][y] = Math.max(m_IJ[x][y + 1], m_IJ[x + 1][y]);
                }
            }
        }

        return m_IJ;
    }

    public static int[][][] buildMScores(int numInputs, int[] inputLengths, List<List<Integer>> inputs) {
        int[][][] mScores = new int[numInputs - 1][][];
        for (int i = 0; i < numInputs - 1; i++) {
            mScores[i] = LCS_MScore_IJ(inputLengths[i], inputLengths[i + 1], inputs.get(i), inputs.get(i + 1));
        }
        return mScores;
    }

    /**
     * @return the length of the 'alphabet' (sigma)
     */
    public int getSigmaLength() {
        return sigmaLength;
    }

    /**
     * @return the constraint sequence
     */
    public List<Integer> getConstraint() {
        return constraint;
    }

    /**
     * @return the input sequences
     */
    public List<List<Integer>> getInputs() {
        return inputs;
    }

    /**
     * @return the number of input sequences
     */
    public int getNumInputs() {
        return numInputs;
    }

    /**
     * @return lengths of input sequences
     */
    public int[] getInputLengths() {
        return inputLengths;
    }

    /**
     * @return the length of the longest input sequence
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * @return the constraintLength
     */
    public int getConstraintLength() {
        return constraintLength;
    }

    /**
     * A 3D array to store the successor tables.
     * <p>
     * {@code [numInputs x sigmaLength x maxLength]}.
     * <p>
     * The successor tables are used to store the indices of successors.
     * <p>
     * For each item in the alphabet at each position in the input sequences.
     * 
     * @return the successorTables
     */
    public int[][][] getSuccessorTables() {
        return successorTables;
    }

    /**
     * A 3D array to store the occurrence tables.
     * <p>
     * {@code [numInputs x sigmaLength x maxLength]}.
     * <p>
     * For each input, for each character, for each position in input.
     * 
     * @return the occurrenceTables
     */
    public int[][][] getOccurrenceTables() {
        return occurrenceTables;
    }

    /**
     * A 2D array to store the embed tables.
     * <p>
     * {@code [numInputs x constraint.size()]}.
     * <p>
     * For each input, for each character in constraint.
     * 
     * @return the embedTables
     */
    public int[][] getEmbedTables() {
        return embedTables;
    }

    /**
     * A 3D array to store the mScores.
     * <p>
     * {@code [numInputs-1 x [i-th input.size() + 1] x [i+1 -th input.size + 1]]}.
     * <p>
     * For each pair of inputs (i, i+1), for each pos in i, for each pos in i+1.
     * 
     * @return the mScores
     */
    public int[][][] getmScores() {
        return mScores;
    }

    /**
     * @return the minLength
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * @param sigmaLength the sigmaLength to set
     */
    public void setSigmaLength(int sigmaLength) {
        this.sigmaLength = sigmaLength;
    }

    /**
     * @param constraint the constraint to set
     */
    public void setConstraint(List<Integer> constraint) {
        this.constraint = constraint;
    }

    /**
     * @param inputs the inputs to set
     */
    public void setInputs(List<List<Integer>> inputs) {
        this.inputs = inputs;
    }

    /**
     * @param numInputs the numInputs to set
     */
    public void setNumInputs(int numInputs) {
        this.numInputs = numInputs;
    }

    /**
     * @param inputLengths the inputLengths to set
     */
    public void setInputLengths(int[] inputLengths) {
        this.inputLengths = inputLengths;
    }

    /**
     * @param maxLength the maxLength to set
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * @param minLength the minLength to set
     */
    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    /**
     * @param constraintLength the constraintLength to set
     */
    public void setConstraintLength(int constraintLength) {
        this.constraintLength = constraintLength;
    }

    /**
     * @param successorTables the successorTables to set
     */
    public void setSuccessorTables(int[][][] successorTables) {
        this.successorTables = successorTables;
    }

    /**
     * @param occurrenceTables the occurrenceTables to set
     */
    public void setOccurrenceTables(int[][][] occurrenceTables) {
        this.occurrenceTables = occurrenceTables;
    }

    /**
     * @param embedTables the embedTables to set
     */
    public void setEmbedTables(int[][] embedTables) {
        this.embedTables = embedTables;
    }

    /**
     * @param mScores the mScores to set
     */
    public void setmScores(int[][][] mScores) {
        this.mScores = mScores;
    }

    /**
     * @return the charToIndex
     */
    public Map<Character, Integer> getCharToIndex() {
        return charToIndex;
    }

    /**
     * @param charToIndex the charToIndex to set
     */
    public void setCharToIndex(Map<Character, Integer> charToIndex) {
        this.charToIndex = charToIndex;
    }

    /**
     * @return the indexToChar
     */
    public Map<Integer, Character> getIndexToChar() {
        return indexToChar;
    }

    /**
     * @param indexToChar the indexToChar to set
     */
    public void setIndexToChar(Map<Integer, Character> indexToChar) {
        this.indexToChar = indexToChar;
    }

    

}
