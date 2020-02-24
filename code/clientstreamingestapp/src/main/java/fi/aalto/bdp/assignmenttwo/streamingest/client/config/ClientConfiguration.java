package fi.aalto.bdp.assignmenttwo.streamingest.client.config;

import feign.codec.ErrorDecoder;
import fi.aalto.bdp.assignmenttwo.streamingest.client.error.CustomErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients
public class ClientConfiguration {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
}
