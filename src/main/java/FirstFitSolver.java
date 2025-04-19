import java.util.ArrayList;
import java.util.List;

public class FirstFitSolver {
    public static Shelf solve(int shelfLength, List<Item> items) {
        List<Item> fitItems = new ArrayList<>(items);
        Shelf result = new Shelf();
        List<Layer> layers = result.layers;
        for (Item item : fitItems) {
            boolean placed = false;
            for (Layer layer : layers) {
                if (layer.addItem(item)) { // 调用放置函数尝试放置
                    result.usedLength += item.length; // 更新货架总使用
                    placed = true;
                    break;
                }
            }
            if (!placed) { // 无法放置时增加新的层再放置物品
                result.addLayer(shelfLength, new Layer(shelfLength));
                layers.getLast().addItem(item);
                result.usedLength += item.length;
            }
        }
        return result;
    }
}
