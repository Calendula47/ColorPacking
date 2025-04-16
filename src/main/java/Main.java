import java.io.IOException;
import java.util.List;

// 主程序入口
public class Main {
    public static void main(String[] args) {
        List<Item> items;
        try {
            // 从文件中读取货物信息
            Input input = InputReader.read("input.csv");
            items = input.items;
            int shelfLength = input.shelfLength;
            int itemSum = input.itemSum;
            int colorSum = input.colorSum;
            long startTime;
            long endTime;

            // FirstFit
            startTime=System.currentTimeMillis();
            Shelf shelfFF = FirstFitSolver.solve(shelfLength, items);
            endTime=System.currentTimeMillis();
            Result resultFF = new Result("FirstFit算法：", shelfLength, itemSum, colorSum, startTime, endTime, shelfFF);
            OutputWriter.printResult(resultFF);

            // 贪心
            startTime=System.currentTimeMillis();
            Shelf shelfGreedy = GreedySolver.solve(shelfLength, items);
            endTime=System.currentTimeMillis();
            Result resultGreedy = new Result("贪心算法：", shelfLength, itemSum, colorSum, startTime, endTime, shelfGreedy);
            OutputWriter.printResult(resultGreedy);

            // 模拟退火
            startTime=System.currentTimeMillis();
            Shelf shelfSA = SimulatedAnnealingSolver.solve(shelfLength, items);
            endTime=System.currentTimeMillis();
            Result resultSA = new Result("模拟退火算法：", shelfLength, itemSum, colorSum, startTime, endTime, shelfSA);
            OutputWriter.printResult(resultSA);
            FitnessWriter.write(SimulatedAnnealingSolver.getFitnessLog(), "simulated_annealing_log.csv");

            // 粒子群
            startTime=System.currentTimeMillis();
            Shelf shelfPS = ParticleSwarmSolver.solve(shelfLength, items);
            endTime=System.currentTimeMillis();
            Result resultPS = new Result("粒子群算法：", shelfLength, itemSum, colorSum, startTime, endTime, shelfPS);
            OutputWriter.printResult(resultPS);
            FitnessWriter.write(ParticleSwarmSolver.getFitnessLog(), "particle_swarm_log.csv");

            // 遗传
            startTime=System.currentTimeMillis();
            Shelf shelfGA = GeneticAlgorithmSolver.solve(shelfLength, items);
            endTime=System.currentTimeMillis();
            Result resultGA = new Result("遗传算法：", shelfLength, itemSum, colorSum, startTime, endTime, shelfGA);
            OutputWriter.printResult(resultGA);
            FitnessWriter.write(GeneticAlgorithmSolver.getFitnessLog(), "genetic_algorithm_log");

        } catch (IOException e) {
            System.err.println("无法读取文件：" + e.getMessage());
        }
    }
}