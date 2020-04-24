import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.util.Map;
import java.util.Random;

public class RandomIntSpout extends BaseRichSpout {
    
    private Random random;
    private SpoutOutputCollector outputCollector;

    @Override
    public void open(Map<String, Object> conf, TopologyContext context, SpoutOutputCollector collector) {
        random = new Random();
        outputCollector = collector;
    }

    @Override
    public void nextTuple() {
        Utils.sleep(1000);
        outputCollector.emit(new Values(random.nextInt()), System.currentTimeMillis());
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("randomInt", "timestamp"));
    }
}
