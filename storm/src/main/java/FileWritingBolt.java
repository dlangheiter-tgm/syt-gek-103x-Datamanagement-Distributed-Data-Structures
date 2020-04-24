import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class FileWritingBolt extends BaseRichBolt {
    public static Logger logger = LoggerFactory.getLogger(FileWritingBolt.class);
    private BufferedWriter writer;
    private String filePath;
    private ObjectMapper objectMapper;

    @Override
    public void cleanup() {
        try {
            writer.close();
        } catch (IOException e) {
            logger.error("Failed to close writer!");
        }
    }

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        try {
            writer = new BufferedWriter(new FileWriter(filePath));
        } catch (IOException e) {
            logger.error("Failed to open file for writing!", e);
        }
    }

    @Override
    public void execute(Tuple input) {
        int sumOfOperations = input.getIntegerByField("sumOfOperations");
        long beginningTimestamp = input.getLongByField("beginningTimestamp");
        long endTimestamp = input.getLongByField("endTimestamp");

        if (sumOfOperations > 2000) {
            AggregationWindow aggregationWindow = new AggregationWindow(sumOfOperations, beginningTimestamp, endTimestamp);
            try {
                writer.write(objectMapper.writeValueAsString(aggregationWindow));
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                logger.error("Failed to write data to file.", e);
            }
        }
    }
}
