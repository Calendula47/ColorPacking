import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Input {
    int shelfLength;
    int itemSum;
    int colorSum;
    List<Item> items;

    public Input(int shelfLength, int itemSum, int colorSum, List<Item> items) {
        this.shelfLength = shelfLength;
        this.itemSum = itemSum;
        this.colorSum = colorSum;
        this.items = items;
    }
}

public class InputReader {
    static Input read(String filePath) {
        List<Item> items = new ArrayList<>();
        int readShelfLength;
        int readItemSum;
        int readColorSum;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            readShelfLength = Integer.parseInt(reader.readLine());
            readItemSum = Integer.parseInt(reader.readLine());
            readColorSum = Integer.parseInt(reader.readLine()); // 前三行为货架长度、物品数量和颜色数量

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(","); // 长度和颜色由逗号分隔，物品之间由换行符分隔
                if (values.length >= 2) {
                    int length = Integer.parseInt(values[0]);
                    int color = Integer.parseInt(values[1]);
                    items.add(new Item(length, color));
                }
            }
        } catch (IOException e) {
            System.err.println("无法读取文件：" + e.getMessage());
            return null;
        }
        return new Input(readShelfLength, readItemSum, readColorSum, items);
    }
}