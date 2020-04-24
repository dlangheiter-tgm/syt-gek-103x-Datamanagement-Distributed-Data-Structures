import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class AggregationWindow {
    int sumOfOperations;
    long beginningTimestamp;
    long endTimestamp;

    public AggregationWindow(int sumOfOperations, long beginningTimestamp, long endTimestamp) {
        this.sumOfOperations = sumOfOperations;
        this.beginningTimestamp = beginningTimestamp;
        this.endTimestamp = endTimestamp;
    }
}
