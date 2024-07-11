package datastructures;
import java.util.ArrayList;
import java.util.List;

public class AStarSolution {
    public List<List<Integer>> solutions;
    public List<Integer> expandeds;

    public AStarSolution(List<List<Integer>> solutions, List<Integer> expandeds) {
        this.solutions = solutions;
        this.expandeds = expandeds;
    }

    public AStarSolution() {
        this.solutions = new ArrayList<>();
        this.expandeds = new ArrayList<>();
    }

    /**
     * @return the solutions
     */
    public List<List<Integer>> getSolutions() {
        return solutions;
    }

    /**
     * @param solutions the solutions to set
     */
    public void setSolutions(List<List<Integer>> solutions) {
        this.solutions = solutions;
    }

    /**
     * @return the expandeds
     */
    public List<Integer> getExpandeds() {
        return expandeds;
    }

    /**
     * @param expandeds the expandeds to set
     */
    public void setExpandeds(List<Integer> expandeds) {
        this.expandeds = expandeds;
    }

    
}
