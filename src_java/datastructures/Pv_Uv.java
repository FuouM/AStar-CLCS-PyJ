package datastructures;

import java.util.List;

public class Pv_Uv {
    private List<Integer> pv;
    private int u_v;
    /**
     * @param pv
     * @param u_v
     */
    public Pv_Uv(List<Integer> pv, int u_v) {
        this.pv = pv;
        this.u_v = u_v;
    }
    /**
     * @return the pv
     */
    public List<Integer> getPv() {
        return pv;
    }
    /**
     * @return the u_v
     */
    public int getU_v() {
        return u_v;
    }
    @Override
    public String toString() {
        return "Pv_Uv [pv=" + pv + ", u_v=" + u_v + "]";
    }

    
    
}
