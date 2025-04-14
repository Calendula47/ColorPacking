import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// 贪心算法求解器
public class GreedyAlgorithmSolver {
    public static List<Layer> solve(int shelfLength, List<Item> items) {
        List<Item> sortedItems = new ArrayList<>(items);
        sortedItems.sort(Comparator.comparingInt(Item::getLength).reversed());

        List<Layer> shelf = new ArrayList<>();
        for (Item item : sortedItems) {
            boolean placed = false;
            for (Layer layer : shelf) {
                if (layer.addItem(item)) {
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                Layer newLayer = new Layer(shelfLength);
                newLayer.addItem(item);
                shelf.add(newLayer);
            }
        }

        return shelf;

    }
}    