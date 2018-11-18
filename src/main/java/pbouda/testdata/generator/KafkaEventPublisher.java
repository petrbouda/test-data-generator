package pbouda.testdata.generator;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pbouda.testdata.articles.Articles;

import java.util.Properties;

import static java.lang.String.format;

public class KafkaEventPublisher implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaEventPublisher.class);

    private final String topicName;
    private final KafkaProducer<Integer, byte[]> kafkaProducer;

    KafkaEventPublisher(String bootstrapServers, String topicName) {
        this.kafkaProducer = new KafkaProducer<>(createProducerProperties(bootstrapServers));
        this.topicName = topicName;
    }

    public boolean publish(Article article) {
        Articles.Article articleProto = Articles.Article.newBuilder()
                .setArticleId(article.articleId)
                .setAuthorId(article.authorId)
                .setAuthorEmail(article.authorEmail)
                .setAuthorName(article.authorName)
                .setTitle(article.title)
                .setContent(article.content)
                .setUrl(article.url)
                .setCountry(article.country)
                .setCity(article.city)
                .build();

        ProducerRecord<Integer, byte[]> record = new ProducerRecord<>(topicName, article.articleId, articleProto.toByteArray());

        kafkaProducer.send(record, (metadata, exception) -> {
            if (metadata == null) {
                LOG.error("Error while sending event to Kafka", exception);
            }
        });
        return true;
    }

    private static Properties createProducerProperties(String bootstrapServers) {
        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        String allocId = System.getenv("NOMAD_ALLOC_ID");
        if (allocId != null) {
            long threadId = Thread.currentThread().getId();
            props.put(ProducerConfig.CLIENT_ID_CONFIG, format("%s-%d", allocId, threadId));
        }
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        return props;
    }


    @Override
    public void close() {
        kafkaProducer.flush();
        kafkaProducer.close();
    }
}
