import java.util.ArrayList;
import java.util.List;

public class Layer {
    int remainLength;
    List<Item> layer;

    public Layer(int shelfLength) {
        this.remainLength = shelfLength;
        this.layer = new ArrayList<>();
    }

    // 检查货物是否能放入当前层
    public boolean addItem(Item item) {
        if (remainLength >= item.getLength() && (layer.isEmpty() || layer.get(layer.size() - 1).getColor() != item.getColor())) {
            layer.add(item);
            remainLength -= item.getLength();
            return true; // 长度和颜色合适时更新本层数据
        }
        return false;
    }

    public List<Item> getLayer() {
        return layer;
    }
}