import java.io.IOException;
import java.util.List;

// 主程序入口
public class Main {
    public static void main(String[] args) {
        int shelfLength = 10;
        List<Item> items;
        try {
            // 从文件中读取货物信息
            items = FileInputReader.readItemsFromFile("input.csv");

            // 使用贪心算法求解
            List<List<Item>> resultGreedy = GreedyAlgorithmSolver.solve(shelfLength, items);
            System.out.println("Greedy Algorithm Result:");
            ResultPrinter.printResult(resultGreedy, shelfLength);

            // 使用遗传算法求解
            List<List<Item>> resultGA = GeneticAlgorithmSolver.solve(shelfLength, items);
            System.out.println("Genetic Algorithm Result:");
            ResultPrinter.printResult(resultGA, shelfLength);

            // 使用粒子群算法求解
            List<List<Item>> resultPSO = ParticleSwarmSolver.solve(shelfLength, items);
            System.out.println("Particle Swarm Algorithm Result:");
            ResultPrinter.printResult(resultPSO, shelfLength);

            // 使用模拟退火算法求解
            List<List<Item>> resultSA = SimulatedAnnealingSolver.solve(shelfLength, items);
            System.out.println("Simulated Annealing Algorithm Result:");
            ResultPrinter.printResult(resultSA, shelfLength);

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}