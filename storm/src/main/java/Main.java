import bolts.AggregationBolt;
import bolts.FileWritingBolt;
import bolts.FilteringBolt;
import bolts.PrintingBolt;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;
import spouts.RandomIntSpout;
import spouts.RandomNumberSpout;

public class Main {
    public static void main(String[] args) throws Exception {
        runTopology();
    }

    public static void runTopology() throws Exception {
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("randomIntSpout", new RandomIntSpout());
        builder.setBolt("printBolt", new PrintingBolt()).shuffleGrouping("randomIntSpout");

        builder.setSpout("randomNumberSpout", new RandomNumberSpout());
        builder.setBolt("filteringBolt", new FilteringBolt()).shuffleGrouping("randomNumberSpout");
        builder.setBolt("aggregationBolt", new AggregationBolt()
                .withTimestampField("timestamp")
                .withLag(BaseWindowedBolt.Duration.seconds(1))
                .withWindow(BaseWindowedBolt.Duration.seconds(5))
        ).shuffleGrouping("filteringBolt");

        String filePath = "operations.txt";
        builder.setBolt("fileBolt", new FileWritingBolt(filePath)).shuffleGrouping("aggregationBolt");

        Config config = new Config();
        config.setDebug(false);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("Test", config, builder.createTopology());
    }
}
