import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputReader {
    public static List<Item> readItemsFromFile(String filePath) throws IOException {
        List<Item> items = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 2) {
                    int length = Integer.parseInt(values[0]);
                    int color = Integer.parseInt(values[1]);
                    items.add(new Item(length, color));
                }
            }
        }
        return items;
    }
}