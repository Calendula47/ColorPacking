import java.util.ArrayList;
import java.util.List;

public class Shelf {
    int totalLength;
    int usedLength;
    List<Layer> layers;

    public Shelf() {
        this.usedLength = 0;
        this.totalLength = 0;
        this.layers = new ArrayList<>();
    }

    public void addLayer(int shelfLength, Layer layer) {
        totalLength += shelfLength; // 增加货架层时增加总长度
        layers.add(layer);
    }

    public double getUsage() {
        return ((double) usedLength / totalLength);
    }
}
