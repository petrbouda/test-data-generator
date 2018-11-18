package pbouda.testdata.generator;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import org.springframework.data.cassandra.core.cql.ReactiveCqlTemplate;
import org.springframework.data.cassandra.core.cql.session.DefaultBridgedReactiveSession;
import reactor.core.publisher.Flux;

public class CassandraGenerator {

    private static final int EVENT_COUNT = 200_000;

    public static void main(String[] args) throws InterruptedException {
        try (var cassandraConfiguration = new CassandraConfiguration();
             var cassandraSession = cassandraConfiguration.createSession()) {

            ReactiveCqlTemplate cassandraTemplate = new ReactiveCqlTemplate(new DefaultBridgedReactiveSession(cassandraSession));

            PreparedStatement statement = cassandraSession.prepare(
                    "UPDATE article SET author_id = ?, author_email = ?, author_name = ?, title = ?, url = ?, city = ?, " +
                            "country = ?, content = ? WHERE article_id = ?");

            Flux.range(0, EVENT_COUNT)
                    .log()
                    .map(Article::create)
                    .flatMap(article ->
                            cassandraTemplate.execute(bindStatement(article, statement))
                                    .map(result -> article))
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
}
