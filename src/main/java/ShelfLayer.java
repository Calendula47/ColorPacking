import java.util.ArrayList;
import java.util.List;

// 定义货架层类
public class ShelfLayer {
    int remainingLength;
    List<Item> items;

    public ShelfLayer(int shelfLength) {
        this.remainingLength = shelfLength;
        this.items = new ArrayList<>();
    }

    // 尝试将货物放入当前层
    public boolean addItem(Item item) {
        if (remainingLength >= item.getLength() && (items.isEmpty() || items.get(items.size() - 1).getColor() != item.getColor())) {
            items.add(item);
            remainingLength -= item.getLength();
            return true;
        }
        return false;
    }

    // 处理最后一个货物
    public void removeLastItem(Item item) {
        if (!items.isEmpty() && items.get(items.size() - 1) == item) {
            items.remove(items.size() - 1);
            remainingLength += item.getLength();
        }
    }

    public List<Item> getItems() {
        return items;
    }
}    