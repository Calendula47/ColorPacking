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

            // 使用贪心算法求解
            List<Layer> resultGreedy = GreedyAlgorithmSolver.solve(shelfLength, items);
            System.out.println("贪心算法：");
            ResultPrinter.printResult(resultGreedy, shelfLength);

        } catch (IOException e) {
            System.err.println("无法读取文件：" + e.getMessage());
        }
    }
}