import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// 贪心算法求解器
public class GreedyAlgorithmSolver {
    public static Shelf solve(int shelfLength, List<Item> items) {
        List<Item> sortedItems = new ArrayList<>(items);
        sortedItems.sort(Comparator.comparingInt(Item::getLength).reversed());

        Shelf result = new Shelf();
        List<Layer> shelf = result.getShelf();
        for (Item item : sortedItems) {
            boolean placed = false;
            for (Layer layer : shelf) {
                if (layer.addItem(item)) {
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                result.addLayer(shelfLength, new Layer(shelfLength));
                shelf.get(shelf.size() - 1).addItem(item);
            }
        }

        return result;

    }
}