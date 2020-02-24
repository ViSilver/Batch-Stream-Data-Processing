package com.example.spring.rabbitmq.bdd.config;

import com.example.spring.rabbitmq.bdd.container.CassandraContainer;
import com.example.spring.rabbitmq.bdd.container.CustomRabbitMqContainer;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.RabbitMQContainer;

@Configuration
@EnableAutoConfiguration
public class TestConfig {

//    private static final RabbitMQContainer rabbitMQContainer = CustomRabbitMqContainer.CONTAINER;
//    private static final GenericContainer cassandraContainer = CassandraContainer.CONTAINER;

    static {
//        rabbitMQContainer.start();
//        cassandraContainer.start();
    }

    @Bean
    public ConnectionFactory rabbitMqConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        connectionFactory.setAddresses("localhost:" + rabbitMQContainer.getMappedPort(CustomRabbitMqContainer.RABBIT_MQ_PORT));
        connectionFactory.setAddresses("localhost:" + 5672);
        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CONNECTION);
        return connectionFactory;
    }
}
