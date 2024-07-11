package datastructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {
    private int numInputs; // Number of input strings
    private List<Integer> pv; // The position vector
    private int l_v; // Partial solution length
    private int u_v; // Prefix length
    private Node parent; // Predecessor node
    private int priority;

    /**
     * @param numInputs Number of input strings
     * @param pv        The position vector
     * @param l_v       Partial solution length
     * @param u_v       Prefix length
     * @param parent    Predecessor node
     */
    public Node(int numInputs, List<Integer> pv, int l_v, int u_v, Node parent, int priority) {
        this.numInputs = numInputs;
        this.pv = pv;
        this.l_v = l_v;
        this.u_v = u_v;
        this.parent = parent;
        this.priority = priority;
    }

    

    /**
     * @param numInputs
     * @param pv
     * @param l_v
     * @param u_v
     * @param parent
     */
    public Node(int numInputs, List<Integer> pv, int l_v, int u_v, Node parent) {
        this.numInputs = numInputs;
        this.pv = pv;
        this.l_v = l_v;
        this.u_v = u_v;
        this.parent = parent;
    }



    /**
     * @param numInputs Number of input strings
     * @param pv        The position vector
     * @param l_v       Partial solution length
     * @param u_v       Prefix length
     */
    public Node(int numInputs, List<Integer> pv, int l_v, int u_v) {
        this(numInputs, pv, l_v, u_v, null, 0);
    }

    /**
     * @param numInputs Number of input strings
     */
    public Node(int numInputs) {
        this(numInputs, initNArray(numInputs, 1), 0, 0);
    }

    public Node(Node node, int priority) {
        this(node.getNumInputs(), node.getPv(), node.getL_v(), node.getU_v(), node.getParent(), priority);
    }

    /**
     * @param size Size of the array
     * @param n    The value to fill with
     * @return An array of integers
     */
    private static List<Integer> initNArray(int size, int n) {
        List<Integer> list = new ArrayList<>(Collections.nCopies(size, n));
        return list;
    }

    /**
     * @return the number of input strings
     */
    public int getNumInputs() {
        return numInputs;
    }

    /**
     * @return the position vector
     */
    public List<Integer> getPv() {
        return pv;
    }

    /**
     * @param pv the position vector to set
     */
    public void setPv(List<Integer> pv) {
        this.pv = pv;
    }

    // public List<Integer> getPositionVector() {
    // // List<Integer> res = new ArrayList<>();
    // // for (int i : pv) {
    // // res.add(i);
    // // }
    // // return res;

    // List<Integer> integerList = Arrays.stream(this.pv)
    // .boxed()
    // .collect(Collectors.toList());
    // return integerList;
    // }

    /**
     * @return the Partial solution length l_v
     */
    public int getL_v() {
        return l_v;
    }

    /**
     * @param l_v the Partial solution length l_v to set
     */
    public void setL_v(int l_v) {
        this.l_v = l_v;
    }

    /**
     * @return the Prefix length u_v
     */
    public int getU_v() {
        return u_v;
    }

    /**
     * @param u_v the Prefix length u_v to set
     */
    public void setU_v(int u_v) {
        this.u_v = u_v;
    }

    /**
     * @return the parent
     */
    public Node getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Integer[] getLU() {
        return new Integer[] {this.l_v, this.u_v};
    }

    @Override
    public String toString() {
        return "Node [pv=" + pv + ", l_v=" + l_v + ", u_v=" + u_v + ", priority=" + priority + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pv == null) ? 0 : pv.hashCode());
        result = prime * result + l_v;
        result = prime * result + u_v;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node other = (Node) obj;
        if (pv == null) {
            if (other.pv != null)
                return false;
        } else if (!pv.equals(other.pv))
            return false;
        if (l_v != other.l_v)
            return false;
        if (u_v != other.u_v)
            return false;
        return true;
    }

}