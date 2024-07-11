import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import datastructures.AStarSolution;
import datastructures.CLCSInstance;
import datastructures.Node;
import datastructures.NodePriorityQueue;
import datastructures.Pv_Uv;

public class AStar {

    public static AStarSolution AStarRun(CLCSInstance inst) {
        AStarSolution solution = new AStarSolution();
        NodePriorityQueue Q_p = new NodePriorityQueue();

        Map<List<Integer>, List<Integer[]>> N_v = new HashMap<>();

        Node root = new Node(inst.getNumInputs());
        root.setPriority(Upperbound.ubBoth(inst, root));

        int expanded = 0;

        Q_p.add(root);
        insertNewNodeToNv(N_v, root);

        while (!Q_p.isEmpty()) {
            Node v = Q_p.poll();
            List<Integer> v_pv = v.getPv();

            // Get visited nodes for this position
            List<Integer[]> N_vRelative = N_v.get(v_pv);

            // Skip node if the position is visited and the node is outdated
            if (N_vRelative != null && isOutdated(v, N_v.get(v_pv)))
                continue;

            // Node has not been visited. Insert node to the N hash map
            insertNewNodeToNv(N_v, v);

            List<Pv_Uv> v_nd = getFeasibleNonDominatedExtensions(inst, v);
            expanded++;

            if (v_nd.isEmpty()) {
                List<Integer> sol = deriveSolution(v, inst.getInputs().get(0));
                solution.getSolutions().add(sol);
                solution.getExpandeds().add(expanded);
                break;
            }

            for (Pv_Uv pv_Uv : v_nd) {
                int vExtendedL = v.getL_v() + 1;
                int vExtendedU = pv_Uv.getU_v();
                List<Integer> vExtendedPv = pv_Uv.getPv();

                boolean doInsert = true;

                N_vRelative = N_v.get(vExtendedPv);
                boolean isVExtVisited = N_vRelative != null;

                if (isVExtVisited) {
                    List<Integer[]> toRemove = new ArrayList<>();
                    doInsert = isExtNotDominated(N_vRelative, vExtendedL, vExtendedU, toRemove);
                    for (Integer[] vRelative : toRemove) {
                        Q_p.remove(pv_Uv.getPv(), vRelative[0], vRelative[1]);
                        N_vRelative.remove(vRelative);
                    }
                }

                if (doInsert) {
                    Integer[] vExtendedLU = { vExtendedL, vExtendedU };
                    Node vExtended = new Node(inst.getNumInputs(), vExtendedPv, vExtendedL, vExtendedU, v);
                    vExtended.setPriority(Upperbound.ubBoth(inst, vExtended));
                    if (isVExtVisited) {
                        N_vRelative.add(vExtendedLU);
                    } else {
                        insertNewNodeToNv(N_v, vExtended);
                    }
                    Q_p.add(vExtended);
                }
            }
        }

        return solution;
    }

    private static boolean isExtNotDominated(List<Integer[]> N_vRelative, int vExtendedL, int vExtendedU, List<Integer[]> toRemove) {
        boolean doInsert = true;
        for (Integer[] vRelative : N_vRelative) {
            int vRelativeL = vRelative[0];
            int vRelativeU = vRelative[1];

            // A node v1 dominates v2 if and only if
            // lv1 >= lv2 && uv1 >= uv2

            boolean L_relativeEQ = vRelativeL == vExtendedL;
            boolean U_relativeEQ = vRelativeU == vExtendedU;

            // Condition 1: Can be eased out to find equally good sols
            boolean L_relativeChosen = (vRelativeL > vExtendedL) || L_relativeEQ;
            boolean U_relativeChosen = (vRelativeU > vExtendedU) || U_relativeEQ;

            // Condition 2: Without, can lead to very long run time
            boolean L_relativeReject = (vRelativeL < vExtendedL) || L_relativeEQ;
            boolean U_relativeReject = (vRelativeU < vExtendedU) || U_relativeEQ;

            // If v_ext is dominated by a known node v_rel,
            // we stop the comparison and skip v_ext.
            if (L_relativeChosen && U_relativeChosen) {
                doInsert = false;
                break;
            }

            if (L_relativeReject && U_relativeReject) {
                toRemove.add(vRelative);
            }
        }
        return doInsert;
    }

    private static boolean isOutdated(Node v, List<Integer[]> N_vRelative) {
        boolean outdated = false;
        // Iterates over each pair of partial solution length and
        // constraint prefix length in the list of pairs for the node's
        // position vector in the hash map.
        for (Integer[] v_rel : N_vRelative) {
            outdated = isOutdatedSingle(v, v_rel);
            if (outdated)
                break;
        }
        return outdated;
    }

    private static boolean isOutdatedSingle(Node v, Integer[] v_rel) {
        int l_rel = v_rel[0];
        int u_rel = v_rel[1];
        // If the partial solution length and constraint prefix length of the node
        // are smaller than those of a pair in the list, the node is outdated.
        boolean cond1 = l_rel > v.getL_v() && u_rel >= v.getU_v();
        boolean cond2 = l_rel == v.getL_v() && u_rel > v.getU_v();
        if (cond1 || cond2)
            return true;
        return false;
    }

    private static void insertNewNodeToNv(Map<List<Integer>, List<Integer[]>> N_v, Node v) {
        List<Integer[]> tmp_v_childs = new ArrayList<>();
        tmp_v_childs.add(v.getLU());
        N_v.put(v.getPv(), tmp_v_childs);
    }

    private static List<Pv_Uv> getFeasibleNonDominatedExtensions(CLCSInstance inst, Node v) {
        List<Pv_Uv> feasibles = new ArrayList<>();

        // Return empty if reached the end of any sequence (Cannot satisfy constraint if
        // go further)
        if (isOvershoot(inst.getNumInputs(), v.getPv(), inst.getInputLengths())) {
            return feasibles;
        }

        // Get non-dominated potential extensions
        List<Integer> sigmaND = getSigmaND(inst.getNumInputs(), inst.getSigmaLength(), v.getPv(),
                inst.getSuccessorTables());

        // If the partial solution already satisfy the constraint P,
        // then we don't need to check for feasibility

        if (v.getU_v() == inst.getConstraintLength()) {
            for (Integer label : sigmaND) {
                feasibles.add(
                        PvUvFromLabel(label, inst.getNumInputs(), v.getU_v(), v.getPv(), inst.getSuccessorTables()));
            }
            return feasibles;
        } 

        for (Integer label : sigmaND) {
            boolean isNextConstraint = (label == inst.getConstraint().get(v.getU_v()));

            // If the label is not the next missing P element 
            // and adding it to the partial solution would lead to a solution 
            // where the constraint P cannot be met 
            if(!isNextConstraint && !isFeasible(label, v.getU_v(), inst.getNumInputs(), v.getPv(), inst.getSuccessorTables(), inst.getEmbedTables())) {
                continue;
            } 
            // If the label matches the next missing element of P, increment u_v
            int newU_v = isNextConstraint ? v.getU_v() + 1 : v.getU_v();
            feasibles.add(
                        PvUvFromLabel(label, inst.getNumInputs(), newU_v, v.getPv(), inst.getSuccessorTables()));
        }

        return feasibles;
    }

    private static boolean isOvershoot(int numInputs, List<Integer> positionVector, int[] inputLengths) {
        // Iterates over each input sequence
        // If any sequence is overshot
        for (int i = 0; i < numInputs; i++) {
            if (positionVector.get(i) > inputLengths[i])
                return true;
        }
        return false;
    }

    private static List<Integer> getSigmaND(int numInputs, int sigmaLength, List<Integer> positionVector,
            int[][][] successorTables) {
        List<Integer> sigmaND = new ArrayList<>();

        // Iterates over each label in the alphabet
        for (int label = 0; label < sigmaLength; label++) {
            // If the label is not dominated, add it to the set of non-dominated labels
            if (!isDominated(label, numInputs, sigmaLength, positionVector, successorTables)) {
                sigmaND.add(label);
            }
        }
        return sigmaND;
    }

    private static boolean isDominated(int label, int numInputs, int sigmaLength, List<Integer> positionVector,
            int[][][] successorTables) {
        // If the label does not appear in the remainder of the input sequences,
        // it is dominated
        if (isLabelAbsent(label, numInputs, positionVector, successorTables)) {
            return true;
        }

        for (int labelOther = 0; labelOther < sigmaLength; labelOther++) {
            // Skip the potential extension itself
            if (label == labelOther)
                continue;

            // If the label appears later than another label in both strings, it is
            // dominated
            if (isDominatedByOther(label, labelOther, numInputs, positionVector, successorTables)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isLabelAbsent(int label, int numInputs, List<Integer> positionVector,
            int[][][] successorTables) {
        for (int i = 0; i < numInputs; i++) {
            int currPos = positionVector.get(i) - 1;
            int labelSuccessorIDX = successorTables[i][label][currPos];
            if (labelSuccessorIDX == Integer.MAX_VALUE)
                return true;
        }
        return false;
    }

    private static boolean isDominatedByOther(int label, int labelOther, int numInputs, List<Integer> positionVector,
            int[][][] successorTables) {
        for (int i = 0; i < numInputs; i++) {
            int currPos = positionVector.get(i) - 1;
            int labelSuccessorIDX = successorTables[i][label][currPos];
            int otherSuccessorIDX = successorTables[i][labelOther][currPos];
            if (labelSuccessorIDX < otherSuccessorIDX)
                return false;
        }
        return true;
    }

    private static Pv_Uv PvUvFromLabel(int label, int numInputs, int u_v, List<Integer> positionVector,
            int[][][] successorTables) {
        List<Integer> newPV = new ArrayList<>(numInputs);

        for (int i = 0; i < numInputs; i++) {
            int currPos = positionVector.get(i) - 1;
            int labelSuccessorIDX = successorTables[i][label][currPos];
            // Calculates the new position vector.
            // +1 to go to the next character
            // +1 to get the 1-based index
            newPV.add(i, labelSuccessorIDX + 2);
        }

        return new Pv_Uv(newPV, u_v);
    }

    public static boolean isFeasible(int label, int u_v, int numInputs, List<Integer> positionVector, int[][][] successorTables, int[][] embedTables) {
        // Iterates over each input sequence
        for(int i = 0; i < numInputs; i++) {
            int currPos = positionVector.get(i) - 1;
            int labelSuccessorIDX = successorTables[i][label][currPos];
            // If the index exceeds the constraint limit, it is not a feasible extension
            if(labelSuccessorIDX > embedTables[i][u_v]) return false;
        }
        return true;
    }

    private static List<Integer> deriveSolution(Node v, List<Integer> firstInput) {
        List<Integer> solution = new ArrayList<>();
        while (v.getParent() != null) {
            solution.add(0, firstInput.get(v.getPv().get(0) - 2));
            v = v.getParent();
        }
        return solution;
    }



}
