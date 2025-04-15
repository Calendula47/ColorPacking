import java.io.IOException;
import java.util.List;

// 主程序入口
public class Main {
    public static void main(String[] args) {
        int shelfLength = 10;
        List<Item> items;
        try {
            // 从文件中读取货物信息
            items = InputReader.readItemsFromFile("input.csv");

            // FirstFit
            Shelf resultFF = FirstFitSolver.solve(shelfLength, items);
            System.out.println("FirstFit算法：");
            ResultPrinter.printResult(resultFF, shelfLength);

            // 贪心
            Shelf resultGreedy = GreedySolver.solve(shelfLength, items);
            System.out.println("贪心算法：");
            ResultPrinter.printResult(resultGreedy, shelfLength);

            // 模拟退火
            Shelf resultSA = SimulatedAnnealingSolver.solve(shelfLength, items);
            System.out.println("模拟退火算法：");
            ResultPrinter.printResult(resultSA, shelfLength);
            FitnessWriter.write(SimulatedAnnealingSolver.getFitnessLog(), "simulated_annealing_log.csv");

            // 粒子群
            Shelf resultPS = ParticleSwarmSolver.solve(shelfLength, items);
            System.out.println("粒子群算法：");
            ResultPrinter.printResult(resultPS, shelfLength);
            FitnessWriter.write(ParticleSwarmSolver.getFitnessLog(), "particle_swarm_log.csv");

            // 遗传
            Shelf resultGA = GeneticAlgorithmSolver.solve(shelfLength, items);
            System.out.println("遗传算法：");
            ResultPrinter.printResult(resultGA, shelfLength);
            FitnessWriter.write(GeneticAlgorithmSolver.getFitnessLog(), "genetic_algorithm_log");

        } catch (IOException e) {
            System.err.println("无法读取文件：" + e.getMessage());
        }
    }
}