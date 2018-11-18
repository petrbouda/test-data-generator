# Test-Data Generator

- Creates nodes for cassandra and kafka and populate them with data
- Kafka is intended to test and optimized batch operations / Cassandra for latency-wise experiments
- 50k events ~= 200MB of data

```
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
 
    ...
}
```

```
// Volumes ensure that we don't need to populate containers every time when we kill it
docker run -d -p 9042:9042 --name cassandra -v ~/volumes/cassandra:/var/lib/cassandra cassandra:3.11.3

docker run -d -p 2181:2181 --name zookeeper wurstmeister/zookeeper && \
docker run -d -p 9092:9092 --name kafka -h kafka --link zookeeper:zookeeper \
-e KAFKA_ADVERTISED_HOST_NAME=127.0.0.1 -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 wurstmeister/kafka


// Load Schema for Cassandra 
cqlsh -e "SOURCE 'article_schema.cql'"

// Querying Kafka
docker exec -it kafka kafka-topics.sh --list --zookeeper zookeeper:218
docker exec -it kafka kafka-console-consumer.sh --topic articles --from-beginning --bootstrap-server kafka:9092
```

