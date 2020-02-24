package com.example.spring.rabbitmq.bdd.stepdef;

import com.example.spring.rabbitmq.Application;
import com.example.spring.rabbitmq.bdd.config.TestConfig;
import com.example.spring.rabbitmq.model.Apartment;
import com.example.spring.rabbitmq.sender.RabbitSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.awaitility.Awaitility.await;

@Slf4j
@RequiredArgsConstructor
@SpringBootTest(classes = {Application.class, TestConfig.class})
//@ContextConfiguration(classes = {Application.class, TestConfig.class}, loader = SpringBootContextLoader.class)
public class BaseSteps {

    private static final String RABBIT_MESSAGE = "Hello World from BDD test!";

    private final RabbitSender rabbitSender;

    private final ScenarioContext scenarioContext;

    @Before
    public void beforeScenario() {
        scenarioContext.getReceivedRabbitMessages().clear();
    }

    @When("User sends (.*) message")
    public void userSendsAMessage(long messages) {
        log.info("Sending message");
        LongStream.rangeClosed(0, messages).forEach(id -> {
            Apartment apartment = new Apartment(id, "name", 1L, "hostName",
                    "neighbourhoodGroup", "neighbourhood", "lat",
                    "long", "roomType", 12L, 3L, 1L, "lastReview", "revPerMonth", "calc", 12L);
                rabbitSender.send(apartment);
        });
    }

    @Then("A message is received")
    public void aMessageIsReceived() {
        await().alias("Desired rabbit message was not received")
               .atMost(3L, TimeUnit.SECONDS)
               .pollInterval(1L, TimeUnit.SECONDS)
               .until(() -> scenarioContext.getReceivedRabbitMessages().stream().anyMatch(RABBIT_MESSAGE::equals));
    }
}
