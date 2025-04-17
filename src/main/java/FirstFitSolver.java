import java.util.ArrayList;
import java.util.List;

public class FirstFitSolver {
    public static Shelf solve(int shelfLength, List<Item> items) {
        List<Item> fitItems = new ArrayList<>(items);
        Shelf result = new Shelf();
        List<Layer> shelf = result.getShelf();
        for (Item item : fitItems) {
            boolean placed = false;
            for (Layer layer : shelf) {
                if (layer.addItem(item)) {
                    result.usedLength += item.getLength();
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
