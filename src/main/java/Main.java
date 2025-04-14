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

            // 贪心
            Shelf resultGreedy = GreedyAlgorithmSolver.solve(shelfLength, items);
            System.out.println("贪心算法：");
            ResultPrinter.printResult(resultGreedy, shelfLength);

            // 模拟退火
            Shelf resultSA = SimulatedAnnealingSolver.solve(shelfLength, items);
            System.out.println("模拟退火算法：");
            ResultPrinter.printResult(resultSA, shelfLength);

            // 粒子群
            Shelf resultPS = ParticleSwarmSolver.solve(shelfLength, items);
            System.out.println("粒子群算法：");
            ResultPrinter.printResult(resultPS, shelfLength);

        } catch (IOException e) {
            System.err.println("无法读取文件：" + e.getMessage());
        }
    }
}