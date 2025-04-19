import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class Solution {
    List<Integer> solution;
    double energy;

    public Solution(List<Integer> solution, double energy) {
        this.solution = solution;
        this.energy = energy;
    }

    public List<Integer> getSolution() {
        return solution;
    }
}

public class SimulatedAnnealingSolver {
    private static final double INITIAL_TEMPERATURE = 1000; // 初始温度
    private static final double COOLING_RATE = 0.99; // 降温率
    private static final int MAX_ITERATIONS = 1000; // 最大迭代次数
    private static final List<Double> fitnessLog = new ArrayList<>(); // 用于记录每代平均填充度

    public static Shelf solve(int shelfLength, List<Item> items) {
        fitnessLog.clear(); // 初始化迭代日志
        Solution currentSolution = new Solution(generateInitialSolution(items.size()), 0); // 创建初始解
        currentSolution.energy = calculateEnergy(currentSolution.getSolution(), shelfLength, items); // 计算初始解填充度
        Solution bestSolution = new Solution(new ArrayList<>(currentSolution.getSolution()), currentSolution.energy); // 创建最优解
        double temperature = INITIAL_TEMPERATURE; // 设定初始温度

        for (int i = 0; i < MAX_ITERATIONS; i++) { // 迭代过程
            List<Integer> newSolutionList = getNeighbor(currentSolution.getSolution()); // 创建邻域解
            Solution newSolution = new Solution(newSolutionList, calculateEnergy(newSolutionList, shelfLength, items)); // 计算邻域解能量
            double delta = newSolution.energy - currentSolution.energy; // 计算能量差值
            fitnessLog.add(1 - currentSolution.energy); // 添加日志

            // Metropolis 准则，高温时更积极接受坏解
            if (delta < 0 || Math.exp(-delta / temperature) > Math.random()) {
                currentSolution = newSolution;
            }

            if (currentSolution.energy < bestSolution.energy) { // 更新最优解
                bestSolution = new Solution(new ArrayList<>(currentSolution.getSolution()), currentSolution.energy);
            }

            temperature *= COOLING_RATE; // 降温并进入下一轮
        }
        return SolutionDecoder.decode(bestSolution.getSolution(), shelfLength, items);
    }

    private static List<Integer> generateInitialSolution(int itemCount) {
        List<Integer> solution = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            solution.add(i);
        }
        Collections.shuffle(solution, new Random()); // 初始解为打乱的物品索引列表
        return solution;
    }

    private static double calculateEnergy(List<Integer> solution, int shelfLength, List<Item> items) {
        Shelf result = SolutionDecoder.decode(solution, shelfLength, items);
        return (1 - result.getUsage()); // 由于模拟退火算法接受能量低的解，这里使用货架剩余比例表示能量
    }

    private static List<Integer> getNeighbor(List<Integer> solution) {
        List<Integer> neighbor = new ArrayList<>(solution);
        Random random = new Random();
        int index1 = random.nextInt(solution.size());
        int index2 = random.nextInt(solution.size());
        if (index1 == index2) {
            Collections.swap(neighbor, index1, solution.size() - 1 - index1); // 数值相同时交换对称位置
        } else {
            Collections.swap(neighbor, index1, index2); // 邻域解为交换两个索引位置
        }
        return neighbor;
    }

    public static List<Double> getFitnessLog() {
        return fitnessLog;
    }
}