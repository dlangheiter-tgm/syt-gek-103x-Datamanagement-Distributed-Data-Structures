package bolts;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class AggregationBolt extends BaseWindowedBolt {
    private OutputCollector collector;
    
    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("sumOfOperations", "beginningTimestamp", "endTimestamp"));
    }

    @Override
    public void execute(TupleWindow inputWindow) {
        System.out.println("AGGREGATION " + inputWindow.toString());
        List<Tuple> tuples = inputWindow.get();
        tuples.sort(Comparator.comparing(this::getTimestamp));
        
        int sumOfOperations = tuples.stream()
                .mapToInt(tuple -> tuple.getIntegerByField("operation"))
                .sum();
        Long beginningTimestamp = getTimestamp(tuples.get(0));
        Long endTimestamp = getTimestamp(tuples.get(tuples.size() - 1));

        Values values = new Values(sumOfOperations, beginningTimestamp, endTimestamp);
        collector.emit(values);
    }

    private Long getTimestamp(Tuple tuple) {
        return tuple.getLongByField("timestamp");
    }
}
