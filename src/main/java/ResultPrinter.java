import java.util.List;

// 结果输出方法
public class ResultPrinter {
    public static void printResult(List<List<Item>> result, int shelfLength) {
        for (int i = 0; i < result.size(); i++) {
            List<Item> layer = result.get(i);
            int usedLength = 0;
            for (Item item : layer) {
                usedLength += item.getLength();
            }
            double usageRatio = (double) usedLength / shelfLength;
            System.out.printf("Layer %d (Usage Ratio: %.2f%%): ", i + 1, usageRatio * 100);
            for (Item item : layer) {
                System.out.print("(Length: " + item.getLength() + ", Color: " + item.getColor() + ") ");
            }
            System.out.println();
        }
    }
}    