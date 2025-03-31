import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// 贪心算法求解器
public class GreedyAlgorithmSolver {
    public static List<List<Item>> solve(int shelfLength, List<Item> items) {
        List<Item> sortedItems = new ArrayList<>(items);
        sortedItems.sort(Comparator.comparingInt(Item::getLength).reversed());

        List<List<Item>> result = new ArrayList<>();
        for (Item item : sortedItems) {
            boolean placed = false;
            for (List<Item> layer : result) {
                ShelfLayer currentLayer = new ShelfLayer(shelfLength);
                currentLayer.getItems().addAll(layer);
                currentLayer.remainingLength = shelfLength - layer.stream().mapToInt(Item::getLength).sum();
                if (currentLayer.addItem(item)) {
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                ShelfLayer newLayer = new ShelfLayer(shelfLength);
                newLayer.addItem(item);
                result.add(newLayer.getItems());
            }
        }
        return result;
    }
}    