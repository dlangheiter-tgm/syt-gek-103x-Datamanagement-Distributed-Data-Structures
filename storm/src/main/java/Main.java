import bolts.FilteringBolt;
import bolts.PrintingBolt;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import spouts.RandomIntSpout;

public class Main {
    public static void main(String[] args) throws Exception {
        runTopology();
    }

    public static void runTopology() throws Exception {
        TopologyBuilder builder = new TopologyBuilder();
        
        builder.setSpout("randomIntSpout", new RandomIntSpout());
        builder.setBolt("printBolt", new PrintingBolt()).shuffleGrouping("randomIntSpout");
        
        //builder.setSpout("randomNumberSpout", new RandomIntSpout());
        //builder.setBolt("filteringBolt", new FilteringBolt()).shuffleGrouping("randomNumberSpout");

        Config config = new Config();
        config.setDebug(false);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("Test", config, builder.createTopology());
    }
}
