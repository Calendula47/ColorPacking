import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class OutputWriter {

    public static void printResult(Result result) {
        List<Layer> shelf = result.getShelf();
        int layerCount = shelf.size();

        // 输出各层使用情况
        for (int i = 0; i < layerCount; i++) {
            Layer currentLayer = shelf.get(i);
            double usageRatio = (1 - (double) currentLayer.remainLength) / result.shelfLength;
            System.out.printf("第%d层（使用率： %.2f%%）: ", i + 1, usageRatio * 100);

            List<Item> items = currentLayer.getLayer();
            for (Item item : items) {
                System.out.printf("（长度：%d，颜色：%d）", item.getLength(), item.getColor());
            }
            System.out.println();
        }

        // 输出总层数和总使用率
        double totalUsageRatio = result.shelf.getUsage();
        System.out.printf("\n使用层数: %d\n", layerCount);
        System.out.printf("总使用率: %.2f%%\n", totalUsageRatio * 100);
    }

    public static void writeResult(Result result) {
        String folderPath = "output";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                System.err.println("文件夹创建失败：" + folderPath);
                return;
            }
        }
        String filename = String.format("%s/Output_I%d_L%d_C%d_%d.csv", folderPath, result.itemSum, result.shelfLength, result.colorSum, System.currentTimeMillis());
        try {
            writeOutput(filename, result);
        } catch (IOException e) {
            System.err.println("文件写入失败：" + e.getMessage());
        }
    }

    private static void writeOutput(String fileName, Result result) throws IOException {
        FileWriter writer = new FileWriter(fileName);
        writer.write(result.algorithm + ": ");
        writer.write(result.itemSum + "\n");
        writer.write(result.colorSum + "\n");
        writer.close();
    }
}