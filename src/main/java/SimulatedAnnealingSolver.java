import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class Solution {
    List<Integer> solution;
    double cost;

    public Solution(List<Integer> solution, double cost) {
        this.solution = solution;
        this.cost = cost;
    }

    public List<Integer> getSolution() {
        return solution;
    }
}

public class SimulatedAnnealingSolver {
    private static final double INITIAL_TEMPERATURE = 5000;
    private static final double COOLING_RATE = 0.99;
    private static final int MAX_ITERATIONS = 5000;
    private static final List<Double> fitnessLog = new ArrayList<>();

    public static Shelf solve(int shelfLength, List<Item> items) {
        fitnessLog.clear();
        Solution currentSolution = new Solution(generateInitialSolution(items.size()), 0);
        currentSolution.cost = calculateCost(currentSolution.getSolution(), shelfLength, items);
        Solution bestSolution = new Solution(new ArrayList<>(currentSolution.getSolution()), currentSolution.cost);
        double temperature = INITIAL_TEMPERATURE;

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            List<Integer> newSolutionList = getNeighbor(currentSolution.getSolution());
            Solution newSolution = new Solution(newSolutionList, calculateCost(newSolutionList, shelfLength, items));
            double delta = newSolution.cost - currentSolution.cost;
            fitnessLog.add(1 - currentSolution.cost);

            // Metropolis 准则
            if (delta > 0 || Math.exp(delta / temperature) > Math.random()) {
                currentSolution = newSolution;
            }
            if (currentSolution.cost > bestSolution.cost) {
                bestSolution = new Solution(new ArrayList<>(currentSolution.getSolution()), currentSolution.cost);
            }

            temperature *= COOLING_RATE;
        }

        return SolutionDecoder.decode(bestSolution.getSolution(), shelfLength, items);
    }

    private static List<Integer> generateInitialSolution(int itemCount) {
        List<Integer> solution = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            solution.add(i);
        }
        Collections.shuffle(solution, new Random());
        return solution;
    }

    private static double calculateCost(List<Integer> solution, int shelfLength, List<Item> items) {
        Shelf result = SolutionDecoder.decode(solution, shelfLength, items);
        return (1 - result.getUsage());
    }

    private static List<Integer> getNeighbor(List<Integer> solution) {
        List<Integer> neighbor = new ArrayList<>(solution);
        Random random = new Random();
        int index1 = random.nextInt(solution.size());
        int index2 = random.nextInt(solution.size());
        if (index1 == index2) {
            Collections.swap(neighbor, index1, solution.size() - (index1 + 1));
        } else {
            Collections.swap(neighbor, index1, index2);
        }
        return neighbor;
    }

    public static List<Double> getFitnessLog() {
        return fitnessLog;
    }
}