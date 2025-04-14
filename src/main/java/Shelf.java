import java.util.ArrayList;
import java.util.List;

public class Shelf {
    int totalLength;
    int usedLength;
    List<Layer> shelf;

    public Shelf() {
        this.usedLength = 0;
        this.totalLength = 0;
        this.shelf = new ArrayList<>();
    }

    public void addLayer(int shelfLength, Layer layer) {
        totalLength += shelfLength;
        usedLength += (shelfLength - layer.remainLength);
        shelf.add(layer);
    }

    public double getUsage() {
        return ((double) usedLength / totalLength);
    }

    public List<Layer> getShelf() {
        return shelf;
    }
}
