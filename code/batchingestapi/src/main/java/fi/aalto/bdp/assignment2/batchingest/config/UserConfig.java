package fi.aalto.bdp.assignment2.batchingest.config;

import com.univocity.parsers.csv.CsvRoutines;
import fi.aalto.bdp.assignment2.batchingest.parser.Parser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.CassandraTemplate;

@Slf4j
@Configuration
public abstract class UserConfig<T> {

    /**
     * Define the class type of your entity model.
     * For the ease of usage, the Cassandra entity model and the parser model are defined in the same model class.
     *
     * @return class of type Class<T>.
     *     Ex: @code{return MyEntityModel.class;}
     */
    public abstract Class<T> entityModelClass();

    /**
     * Name of the keyspace.
     *
     * @return keyspace of type String.
     */
    public abstract String keyspace();

    /**
     * Define the name of the base package of the model.
     *
     * @return array of base package names.
     *      Ex: @code{return new String[] {MyEntityModel.class.getPackage().getName()};}
     */
    public abstract String[] getEntityBasePackages();

    @Bean
    public Parser<T> apartmentParser(final CsvRoutines csvRoutines, final CassandraTemplate cassandraTemplate) {
        log.info("Creating user parser for {}", entityModelClass());
        return new Parser<T>(entityModelClass(), csvRoutines, cassandraTemplate);
    }
}
