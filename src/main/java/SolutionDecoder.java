import java.util.List;

public class SolutionDecoder {
    public static Shelf decode(List<Integer> solution, int shelfLength, List<Item> items) {
        Shelf result = new Shelf();
        List<Layer> layers = result.layers;
        for (int index : solution) {
            Item item = items.get(index);
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
