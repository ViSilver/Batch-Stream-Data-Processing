package fi.aalto.bdp.assignmenttwo.streamingest.client.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

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
}
