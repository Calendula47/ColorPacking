import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// 用于存放解及其成本的类
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

    public double getCost() {
        return cost;
    }

    public void setSolution(List<Integer> solution) {
        this.solution = solution;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}

// 模拟退火算法求解器
public class SimulatedAnnealingSolver {
    private static final double INITIAL_TEMPERATURE = 1000;
    private static final double COOLING_RATE = 0.99;
    private static final int MAX_ITERATIONS = 1000;

    public static List<List<Item>> solve(int shelfLength, List<Item> items) {
        Solution currentSolution = new Solution(generateInitialSolution(items.size()), 0);
        currentSolution.setCost(calculateCost(currentSolution.getSolution(), shelfLength, items));
        Solution bestSolution = new Solution(new ArrayList<>(currentSolution.getSolution()), currentSolution.getCost());
        double temperature = INITIAL_TEMPERATURE;

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            List<Integer> newSolutionList = getNeighbor(currentSolution.getSolution());
            Solution newSolution = new Solution(newSolutionList, calculateCost(newSolutionList, shelfLength, items));
            double delta = newSolution.getCost() - currentSolution.getCost();

            if (delta < 0 || Math.exp(-delta / temperature) > Math.random()) {
                currentSolution = newSolution;
            }

            if (currentSolution.getCost() < bestSolution.getCost()) {
                bestSolution = new Solution(new ArrayList<>(currentSolution.getSolution()), currentSolution.getCost());
            }

            temperature *= COOLING_RATE;
        }

        return decodeSolution(bestSolution.getSolution(), shelfLength, items);
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
        List<List<Item>> result = decodeSolution(solution, shelfLength, items);
        return result.size();
    }

    private static List<Integer> getNeighbor(List<Integer> solution) {
        List<Integer> neighbor = new ArrayList<>(solution);
        Random random = new Random();
        int index1 = random.nextInt(solution.size());
        int index2 = random.nextInt(solution.size());
        Collections.swap(neighbor, index1, index2);
        return neighbor;
    }

    private static List<List<Item>> decodeSolution(List<Integer> solution, int shelfLength, List<Item> items) {
        List<List<Item>> result = new ArrayList<>();
        for (int index : solution) {
            Item item = items.get(index);
            boolean placed = false;
            for (List<Item> layer : result) {
                Shelf currentLayer = new Shelf(shelfLength);
                currentLayer.getLayer().addAll(layer);
                currentLayer.remainLength = shelfLength - layer.stream().mapToInt(Item::getLength).sum();
                if (currentLayer.addItem(item)) {
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                Shelf newLayer = new Shelf(shelfLength);
                newLayer.addItem(item);
                result.add(newLayer.getLayer());
            }
        }
        return result;
    }
}