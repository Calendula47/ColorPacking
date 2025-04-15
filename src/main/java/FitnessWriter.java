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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
