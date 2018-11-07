package pbouda.testdata.generator;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import org.springframework.data.cassandra.core.cql.ReactiveCqlTemplate;
import org.springframework.data.cassandra.core.cql.session.DefaultBridgedReactiveSession;
import reactor.core.publisher.Flux;

import java.util.UUID;

public class Application {

    private static final Lorem LOREM = LoremIpsum.getInstance();
    private static final String KAFKA_SERVER = "localhost:9092";
    private static final String KAFKA_TOPIC = "articles";

    private static final int START = 0;
    private static final int END = 200_000;

    public static void main(String[] args) throws InterruptedException {
        try (var cassandraConfiguration = new CassandraConfiguration();
             var cassandraSession = cassandraConfiguration.createSession();
             var kafkaPublisher = new KafkaEventPublisher(KAFKA_SERVER, KAFKA_TOPIC)) {

            ReactiveCqlTemplate cassandraTemplate = new ReactiveCqlTemplate(new DefaultBridgedReactiveSession(cassandraSession));

            PreparedStatement statement = cassandraSession.prepare(
                    "UPDATE article SET author_id = ?, author_email = ?, author_name = ?, title = ?, url = ?, city = ?, " +
                            "country = ?, content = ? WHERE article_id = ?");

            Flux.range(START, END)
                    .log()
                    .map(Application::createArticle)
                    .flatMap(article ->
                            cassandraTemplate.execute(bindStatement(article, statement))
                                    .map(result -> article))
                    .map(kafkaPublisher::publish)
                    .subscribe();

            Thread.currentThread().join();
        }
    }

    private static BoundStatement bindStatement(Article article, PreparedStatement statement) {
        return statement.bind(
                article.authorId,
                article.authorEmail,
                article.authorName,
                article.title,
                article.url,
                article.city,
                article.country,
                article.content,
                article.articleId);
    }

    private static Article createArticle(int id) {
        return new Article(
                id,
                UUID.randomUUID().toString(),
                LOREM.getEmail(),
                LOREM.getName(),
                LOREM.getTitle(3, 50),
                LOREM.getUrl(),
                LOREM.getCity(),
                LOREM.getCountry(),
                LOREM.getParagraphs(1, 20));
    }

    static class Article {
        final int articleId;
        final String authorId;
        final String authorEmail;
        final String authorName;
        final String title;
        final String url;
        final String city;
        final String country;
        final String content;

        private Article(
                int articleId,
                String authorId,
                String authorEmail,
                String authorName,
                String title,
                String url,
                String city,
                String country,
                String content) {

            this.articleId = articleId;
            this.authorId = authorId;
            this.authorEmail = authorEmail;
            this.authorName = authorName;
            this.title = title;
            this.url = url;
            this.city = city;
            this.country = country;
            this.content = content;
        }
    }
}
