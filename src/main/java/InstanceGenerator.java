import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InstanceGenerator {

    public static void main(String[] args) {
        int instanceSum = 10; // 每个参数组合生成的实例数
        int[] itemSums = {300, 300, 300, 500, 500, 500, 300, 300, 300, 500, 500, 500, 300, 300, 300, 500, 500, 500};
        int[] shelfLengths = {500, 500, 500, 500, 500, 500, 750, 750, 750, 750, 750, 750, 1000, 1000, 1000, 1000, 1000, 1000};
        int[] colorSums = {2, 7, 15, 2, 7, 15, 2, 7, 15, 2, 7, 15, 2, 7, 15, 2, 7, 15};

        try {
            for (int i = 0; i < itemSums.length; i++) {
                for (int j = 0; j < instanceSum; j++) {
                    generateInstance(itemSums[i], shelfLengths[i], colorSums[i], i * instanceSum + j);
                }
            }
        } catch (IOException e) {
            System.err.println("文件写入错误：" + e.getMessage());
        }
    }

    private static void generateInstance(int itemSum, int shelfLength, int colorSum, int instanceSeed) throws IOException {
        Random random = new Random(instanceSeed); // 使用相同种子以确保可重复性
        List<Item> items = new ArrayList<>();

        for (int i = 0; i < itemSum; i++) {
            int length = (int) ((0.1 + 0.7 * random.nextDouble()) * shelfLength); // 长度在 [0.1L, 0.8L] 范围内
            int color = drawZipf(colorSum, 2, random); // 使用 Zipf 分布生成颜色
//            int color = random.nextInt(1, colorSum); // 使用纯随机生成颜色
            items.add(new Item(length, color));
        }

        String folderPath = "instances";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                System.err.println("文件夹创建失败：" + folderPath);
                return;
            }
        }
        String filename = String.format("%s/Random_Zipf_I%d_L%d_C%d_%d.csv", folderPath, itemSum, shelfLength, colorSum, instanceSeed + 1);
        try {
            writeInstance(filename, shelfLength, items, colorSum);
        } catch (IOException e) {
            System.err.println("文件写入失败：" + e.getMessage());
        }
    }

    private static int drawZipf(int colorSum, double alpha, Random random) {
        double z = 0;
        for (int i = 1; i <= colorSum; i++) {
            z += 1.0 / Math.pow(i, alpha);
        }
        double u = random.nextDouble();
        double sum = 0;
        for (int i = 1; i <= colorSum; i++) {
            sum += 1.0 / Math.pow(i, alpha) / z;
            if (u <= sum) {
                return i;
            }
        }
        return colorSum;
    }

    private static void writeInstance(String filename, int shelfLength, List<Item> items, int colorSum) throws IOException {
        FileWriter writer = new FileWriter(filename);
        writer.write(shelfLength + "\n");
        writer.write(items.size() + "\n");
        writer.write(colorSum + "\n");
        for (Item item : items) {
            writer.write(item.length + "," + item.color + "\n");
        }
        writer.close();
    }
}

