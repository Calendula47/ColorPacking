import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InstanceGenerator {

    public static void main(String[] args) {
        int numInstances = 10; // 每个参数组合生成的实例数
        int[] numItems = {300, 300, 300, 500, 500, 500, 300, 300, 300, 500, 500, 500};
        int[] binCapacities = {500, 500, 500, 500, 500, 500, 750, 750, 750, 750, 750, 750};
        int[] numColors = {2, 7, 15, 2, 7, 15, 2, 7, 15, 2, 7, 15};

        try {
            for (int i = 0; i < numItems.length; i++) {
                for (int j = 0; j < numInstances; j++) {
                    generateInstance(numItems[i], binCapacities[i], numColors[i], i * numInstances + j);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateInstance(int numItems, int binCapacity, int numColors, int instanceId) throws IOException {
        Random random = new Random(instanceId); // 使用 instanceId 作为种子，以确保可重复性
        List<Item> items = new ArrayList<>();

        for (int i = 0; i < numItems; i++) {
            int length = (int) ((0.1 + 0.7 * random.nextDouble()) * binCapacity); // 长度在 [0.1L, 0.8L] 范围内
            int color = drawZipf(numColors, 2, random); // 使用 Zipf 分布生成颜色
            items.add(new Item(length, color));
        }

        // 将实例写入文件
        String filename = String.format("CBPP_Uniform_n%d_L%d_c%d_id%d.txt", numItems, binCapacity, numColors, instanceId);
        writeFile(filename, binCapacity, items);
    }

    private static int drawZipf(int numColors, double alpha, Random random) {
        double z = 0;
        for (int i = 1; i <= numColors; i++) {
            z += 1.0 / Math.pow(i, alpha);
        }

        double u = random.nextDouble();
        double sum = 0;
        for (int i = 1; i <= numColors; i++) {
            sum += 1.0 / Math.pow(i, alpha) / z;
            if (u <= sum) {
                return i;
            }
        }
        return numColors; // 默认情况
    }

    private static void writeFile(String filename, int binCapacity, List<Item> items) throws IOException {
        FileWriter writer = new FileWriter(filename);
        writer.write(binCapacity + "\n");
        writer.write(items.size() + "\n");
        for (Item item : items) {
            writer.write(item.length + " " + item.color + "\n");
        }
        writer.close();
    }
}

