package datastructures;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class NodePriorityQueue {
    PriorityQueue<Node> queue;

    public NodePriorityQueue() {
        this.queue = new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node n1, Node n2) {
                // The node with higher priority comes first
                return Integer.compare(n2.getPriority(), n1.getPriority());
            }
        });
    }


    // /**
    //  * Inserts the node into this priority queue.
    //  * @param node
    //  * @param priority
    //  */
    // public void add(Node node, int priority) {
    //     queue.add(new Node(node, priority));
    // }

    /**
     * Inserts the node into this priority queue.
     * @param node
     */
    public void add(Node node) {
        queue.add(node);
    }


    /**
     * Retrieves and removes the head of this queue, or returns null if this queue is empty.
     * @return the head Node
     */
    public Node poll() {
        return queue.poll();
    }

    /**
     * Retrieves, but does not remove, the head of this queue, or returns null if this queue is empty.
     * @return
     */
    public Node peek() {
        return queue.peek();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }


    /**
     * @param pv
     * @param l_v
     * @param u_v
     * @return
     */
    public boolean remove(List<Integer> pv, int l_v, int u_v) {
        return queue.removeIf(node -> node.getPv().equals(pv) && node.getL_v() == l_v && node.getU_v() == u_v);
    }

}
