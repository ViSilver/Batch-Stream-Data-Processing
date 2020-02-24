package fi.aalto.bdp.assignmenttwo.streamingest.client.config;

import fi.aalto.bdp.assignmenttwo.streamingest.client.model.Apartment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfigImpl extends UserConfig<Apartment> {

    @Value("${spring.data.cassandra.keyspace-name}")
    private String keyspace;

    @Override
    public Class<Apartment> entityModelClass() {
        return Apartment.class;
    }

    @Override
    public String keyspace() {
        return keyspace;
    }

    @Override
    public String[] getEntityBasePackages() {
        return new String[] {Apartment.class.getPackage().getName()};
    }
}
