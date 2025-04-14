import java.util.List;

public class ResultPrinter {
    public static void printResult(List<Layer> result, int shelfLength) {
        int totalUsedLength = 0;
        int layerCount = result.size();

        // 输出各层使用情况
        for (int i = 0; i < layerCount; i++) {
            List<Item> layer = result.get(i).getLayer();
            int usedLength = 0;
            for (Item item : layer) {
                usedLength += item.getLength();
            }
            totalUsedLength += usedLength;

            double usageRatio = (double) usedLength / shelfLength;
            System.out.printf("第%d层（使用率： %.2f%%）: ", i + 1, usageRatio * 100);
            for (Item item : layer) {
                System.out.print("（长度：" + item.getLength() + "，颜色：" + item.getColor() + "）");
            }
            System.out.println();
        }

        // 输出总层数和总使用率
        double totalUsageRatio = (double) totalUsedLength / (shelfLength * layerCount);
        System.out.printf("\n使用层数: %d\n", layerCount);
        System.out.printf("总使用率: %.2f%%\n", totalUsageRatio * 100);
    }
}