import java.util.List;

public class ResultPrinter {
    public static void printResult(Shelf shelf, int shelfLength) {
        List<Layer> result = shelf.getShelf();
        int layerCount = result.size();

        // 输出各层使用情况
        for (int i = 0; i < layerCount; i++) {
            Layer currentLayer = result.get(i);
            double usageRatio = (double) currentLayer.remainLength / shelfLength;
            System.out.printf("第%d层（使用率： %.2f%%）: ", i + 1, usageRatio * 100);

            List<Item> items = currentLayer.getLayer();
            for (Item item : items) {
                System.out.printf("（长度：%d，颜色：%d）", item.getLength(), item.getColor());
            }
            System.out.println();
        }

        // 输出总层数和总使用率
        double totalUsageRatio = shelf.getUsage();
        System.out.printf("\n使用层数: %d\n", layerCount);
        System.out.printf("总使用率: %.2f%%\n", totalUsageRatio * 100);
    }
}