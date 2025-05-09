import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// 主程序入口
public class Main {
    public static void main(String[] args) {
        List<Result> results = new ArrayList<>(); // 输出日志用于记录每个算例每个算法的最终结果
        Path dictionary = Paths.get("instances"); // 算例存放文件夹
        long mainStartTime = System.currentTimeMillis();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dictionary, "*.csv")) {
            for (Path path : stream) {
                if (Files.isRegularFile(path)) { // 遍历算例文件夹中所有算例
                    String filePath = String.valueOf(path.getFileName());
                    Input input = InputReader.read("instances/" + filePath); // 读取当前算例
                    System.out.println(filePath); // 调试用
                    List<Item> items = Objects.requireNonNull(input).items; // 读取算例中的物品

                    int shelfLength = input.shelfLength; // 用于书写输出日志的参数
                    int itemSum = input.itemSum;
                    int colorSum = input.colorSum;
                    long startTime;
                    long endTime;

                    // FirstFit
                    startTime = System.currentTimeMillis();
                    Shelf shelfFF = FirstFitSolver.solve(shelfLength, items);
                    endTime = System.currentTimeMillis();
                    Result currentResult = new Result("FF", shelfLength, itemSum, colorSum, startTime, endTime, shelfFF);
                    results.add(currentResult);

                    // 降序
                    startTime = System.currentTimeMillis();
                    Shelf shelfGreedy = DecreaseSolver.solve(shelfLength, items);
                    endTime = System.currentTimeMillis();
                    currentResult = new Result("FFD", shelfLength, itemSum, colorSum, startTime, endTime, shelfGreedy);
                    results.add(currentResult);

                    String instanceName = filePath.substring(0, filePath.length() - 4);

                    // 模拟退火
                    startTime = System.currentTimeMillis();
                    Shelf shelfSA = SimulatedAnnealingSolver.solve(shelfLength, items);
                    endTime = System.currentTimeMillis();
                    currentResult = new Result("SA", shelfLength, itemSum, colorSum, startTime, endTime, shelfSA);
                    results.add(currentResult);
                    FitnessWriter.write(SimulatedAnnealingSolver.getFitnessLog(), "log/" + instanceName + "_simulated_annealing.csv");

                    // 粒子群
                    startTime = System.currentTimeMillis();
                    Shelf shelfPS = ParticleSwarmSolver.solve(shelfLength, items);
                    endTime = System.currentTimeMillis();
                    currentResult = new Result("PS", shelfLength, itemSum, colorSum, startTime, endTime, shelfPS);
                    results.add(currentResult);
                    FitnessWriter.write(ParticleSwarmSolver.getFitnessLog(), "log/" + instanceName + "_particle_swarm.csv");

                    // 遗传
                    startTime = System.currentTimeMillis();
                    Shelf shelfGA = GeneticAlgorithmSolver.solve(shelfLength, items);
                    endTime = System.currentTimeMillis();
                    currentResult = new Result("GA", shelfLength, itemSum, colorSum, startTime, endTime, shelfGA);
                    results.add(currentResult);
                    FitnessWriter.write(GeneticAlgorithmSolver.getFitnessLog(), "log/" + instanceName + "_genetic_algorithm.csv");
                }
            }
        } catch (IOException e) {
            System.err.println("文件读取错误：" + e.getMessage());
        }
        OutputWriter.writeOutput(Objects.requireNonNull(results));
        System.out.println(System.currentTimeMillis() - mainStartTime);
    }
}