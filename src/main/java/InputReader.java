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

    public static Input read(String filePath) throws IOException {
        List<Item> items = new ArrayList<>();
        int readShelfLength;
        int readItemSum;
        int readColorSum;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            readShelfLength = readInt(reader.readLine(), "货架长度");
            readItemSum = readInt(reader.readLine(), "物品总数");
            readColorSum = readInt(reader.readLine(), "颜色总数");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 2) {
                    int length = readInt(values[0],"物品长度");
                    int color = readInt(values[1],"物品颜色");
                    items.add(new Item(length, color));
                }
            }
        } catch (IOException e) {
            System.err.println("无法读取文件：" + e.getMessage());
            return null;
        }
        return new Input(readShelfLength, readItemSum, readColorSum, items);
    }

    private static int readInt(String value, String readingName) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.println("无法读取" + readingName + "：" + e.getMessage());
            throw e;
        }
    }
}