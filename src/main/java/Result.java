public class Result {
    String algorithm;
    int shelfLength;
    int itemSum;
    int colorSum;
    long startTime;
    long endTime;
    Shelf shelf;

    public Result(String algorithm, int shelfLength, int itemSum, int colorSum, long startTime, long endTime, Shelf shelf) {
        this.algorithm = algorithm; // 用于存取输出日志中需要的参数
        this.shelfLength = shelfLength;
        this.itemSum = itemSum;
        this.colorSum = colorSum;
        this.startTime = startTime;
        this.endTime = endTime;
        this.shelf = shelf;
    }
}
