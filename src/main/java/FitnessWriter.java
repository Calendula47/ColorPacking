import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FitnessWriter {
    public static void write(List<Double> fitnessLog, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            int generation = 1;
            for (double fitness : fitnessLog) {
                writer.write(String.valueOf(generation));
                writer.write(",");
                writer.write(String.valueOf(fitness));
                writer.write("\n");
                generation++;
            }
        } catch (IOException e) {
            System.err.println("适应度历史写入错误：" + e.getMessage());
        }
    }
}
