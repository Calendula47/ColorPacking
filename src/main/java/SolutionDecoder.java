import java.util.List;

public class SolutionDecoder {
    public static Shelf decode(List<Integer> solution, int shelfLength, List<Item> items) {
        Shelf result = new Shelf();
        List<Layer> shelf = result.getShelf();
        for (int index : solution) {
            Item item = items.get(index);
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
