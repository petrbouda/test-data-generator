package pbouda.testdata.generator;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;

import java.net.InetSocketAddress;

public class CassandraConfiguration implements AutoCloseable {

    private Session session;

    Session createSession() {
        Cluster.Builder builder = Cluster.builder()
                .withLoadBalancingPolicy(DCAwareRoundRobinPolicy.builder().withLocalDc("datacenter1").build())
                .addContactPointsWithPorts(new InetSocketAddress("localhost", 9042));

        session = builder.build().connect("articles");
        return session;
    }

    @Override
    public void close() {
        session.getCluster().close();
    }
}