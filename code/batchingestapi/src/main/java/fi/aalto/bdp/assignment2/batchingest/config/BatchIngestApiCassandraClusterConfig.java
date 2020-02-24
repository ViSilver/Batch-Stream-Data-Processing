package fi.aalto.bdp.assignment2.batchingest.config;

import com.datastax.driver.core.policies.LoadBalancingPolicy;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import com.univocity.parsers.csv.CsvRoutines;
import fi.aalto.bdp.assignment2.batchingest.parser.Parser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableCassandraRepositories
public class BatchIngestApiCassandraClusterConfig extends AbstractCassandraConfiguration {

    @Value("${spring.data.cassandra.keyspace-name}")
    private String keyspace;

    @Value("${spring.data.cassandra.contact-points}")
    private String hosts;

    private final UserConfig userConfig;

    @Override
    protected boolean getMetricsEnabled() {
        return false;
    }

    @Override
    protected LoadBalancingPolicy getLoadBalancingPolicy() {
        // It can be used: DCAwareRoundRobinPolicy.builder().withLocalDc("DC1").build();
        return new RoundRobinPolicy();
    }

    @Override
    protected String getContactPoints() {
        return hosts;
    }

    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        List<CreateKeyspaceSpecification> keyspaceCreations = new ArrayList<>();
        CreateKeyspaceSpecification keyspaceSpecification = CreateKeyspaceSpecification.createKeyspace(userConfig.keyspace())
                .ifNotExists()
                .withSimpleReplication(2)
                .with(KeyspaceOption.DURABLE_WRITES); // TODO: move to user config
        keyspaceCreations.add(keyspaceSpecification);
        return keyspaceCreations;
    }

    @Override
    protected String getKeyspaceName() {
        return userConfig.keyspace();
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    public String[] getEntityBasePackages() {
        return userConfig.getEntityBasePackages();
    }
}
