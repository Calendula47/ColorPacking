import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DecreaseSolver {
    public static Shelf solve(int shelfLength, List<Item> items) {
        List<Item> sortedItems = new ArrayList<>(items);
        sortedItems.sort(Comparator.comparingInt(Item::getLength).reversed()); // 贪心算法为从大到小排列
        Shelf result = new Shelf();
        List<Layer> layers = result.layers;
        for (Item item : sortedItems) {
            boolean placed = false;
            for (Layer layer : layers) {
                if (layer.addItem(item)) {
                    result.usedLength += item.length;
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                result.addLayer(shelfLength, new Layer(shelfLength));
                layers.getLast().addItem(item);
                result.usedLength += item.length;
            }
        }
        return result;
    }
}