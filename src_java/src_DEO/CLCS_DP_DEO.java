package src_DEO;

import java.util.ArrayList;
import java.util.List;

public class CLCS_DP_DEO {
    public int[][] T;
    public DeoItem[][][] F;
    public int[] n_pos;
    public int[][] pos;
    public List<Integer> result;
    public DeoInstance inst;

    public List<Integer> DP_Deo() {
        // references to create simple alias of input strings
        int[] s0 = inst.S[0];
        int[] s1 = inst.S[1];

        // T and F matrix structure initialization
        T = new int[s1.length][s0.length];
        F = new DeoItem[inst.p + 1][s1.length][s0.length];

        // compute positions of letters in the first sequence
        n_pos = new int[inst.sigma.length];
        pos = new int[inst.sigma.length][s0.length];
        for (int i = 0; i < s0.length; i++) {
            pos[s0[i]][n_pos[s0[i]]] = i;
            n_pos[s0[i]] = n_pos[s0[i]] + 1;
        }

        // main loop to traverse the matrices in a level-wise matter
        for (int k = 0; k <= inst.p; k++) {
            List<DeoItem> L0 = new ArrayList<>();
            L0.add(new DeoItem());
            if (k == 0) {
                L0.get(0).len = 0;
            }
            int N0 = 1;
            int N1 = 0;
            for (int j = 0; j < s1.length; j++) {
                List<DeoItem> L1 = new ArrayList<>();
                L1.add(L0.get(0));
                N1++;
                int p = 1;
                for (int s = 0; s < n_pos[s1[j]]; s++) {
                    int i = pos[s1[j]][s];
                    while (p < N0) {
                        if (L0.get(p).i >= i)
                            break;
                        else if (L1.get(N1 - 1).len < L0.get(p).len && L0.get(p).len > 0) {
                            L1.add(L0.get(p));
                            N1++;
                        }
                        p++;
                    }
                    int v;
                    if (k > 0 && s1[j] == inst.P[k - 1]) {
                        v = T[j][i];
                        T[j][i] = L0.get(p - 1).len + 1;
                    } else {
                        v = L0.get(p - 1).len + 1;
                        T[j][i] = v;
                    }
                    F[k][j][i] = new DeoItem(L0.get(p - 1).i, L0.get(p - 1).j, v);
                    if (L1.get(N1 - 1).len < v) {
                        L1.add(new DeoItem(i, j, v));
                        N1++;
                    }
                }
                while (p < N0 && L1.get(N1 - 1).len >= L0.get(p).len) {
                    p++;
                }
                while (p < N0) {
                    L1.add(L0.get(p));
                    N1++;
                    p++;
                }
                L0 = L1;
                N0 = N1;
                N1 = 0;
            }

            if (k == inst.p) {
                // determine CLCS solution
                result = new ArrayList<>();
                DeoItem item = L0.get(N0 - 1);
                int k_sol = inst.p;
                while (item.len > 1) {
                    result.add(0, s1[item.j]);
                    if (k_sol > 0 && s1[item.j] == inst.P[k_sol - 1]) {
                        k_sol--;
                    }
                    item = F[k_sol][item.j][item.i];
                }
            }
        }
        return result;
    }

}
