package pbouda.testdata.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaGenerator.class);

    private static final String KAFKA_SERVER = "localhost:9092";
    private static final String KAFKA_TOPIC = "articles";

    private static final int EVENT_COUNT = 200_000;

    public static void main(String[] args) {
        try (var kafkaPublisher = new KafkaEventPublisher(KAFKA_SERVER, KAFKA_TOPIC)) {
            for (int i = 0; i < EVENT_COUNT; i++) {
                LOG.info("Event: " + i);
                Article article = Article.create(i);
                kafkaPublisher.publish(article);
            }
        }
    }
}
