CREATE KEYSPACE IF NOT EXISTS articles WITH replication = {'class': 'NetworkTopologyStrategy', 'datacenter1': '1'}  AND durable_writes = true;

CREATE TABLE IF NOT EXISTS articles.article (
    article_id int,
    author_id text,
    author_email text,
    author_name text,
    title text,
    url text,
    city text,
    country text,
    content text,
    PRIMARY KEY (article_id)
) WITH bloom_filter_fp_chance = 0.01
       AND caching = {'keys':'ALL', 'rows_per_partition':'NONE'}
       AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy'}
    AND compression = {'sstable_compression': 'org.apache.cassandra.io.compress.LZ4Compressor'};