import java.util.ArrayList;
import java.util.List;

public class Layer {
    int remainLength;
    List<Item> items;

    public Layer(int shelfLength) {
        this.remainLength = shelfLength;
        this.items = new ArrayList<>();
    }

    // 检查货物是否能放入当前层
    public boolean addItem(Item item) {
        if (remainLength >= item.length && (items.isEmpty() || items.getLast().color != item.color)) {
            items.add(item);
            remainLength -= item.length;
            return true; // 长度足够放置或该层为空且和上一个物品颜色不同放置物品
        }
        return false;
    }
}