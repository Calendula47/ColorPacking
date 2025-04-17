import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class OutputWriter {
    public static void writeOutput(List<Result> results) {
        try (FileWriter writer = new FileWriter("Output.csv")) {
            for (Result result : results) {
                if (Objects.equals(result.algorithm, "FirstFit")) {
                    // 行首标明算例参数
                    writer.write(result.shelfLength + ",");
                    writer.write(result.itemSum + ",");
                    writer.write(String.valueOf(result.colorSum));
                } else writer.write(",");
                // 写入运行时长和最终填充度
                writer.write(result.endTime - result.startTime + ",");
                writer.write(String.format("% 2f", result.shelf.getUsage() * 100));
                // 遗传算法为行尾
                if (Objects.equals(result.algorithm, "GA")) writer.write("\n");
            }
        } catch (IOException e) {
            System.err.println("文件写入失败：" + e.getMessage());
        }
    }
}