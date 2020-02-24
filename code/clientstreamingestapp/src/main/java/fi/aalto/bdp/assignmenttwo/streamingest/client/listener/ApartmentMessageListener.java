package fi.aalto.bdp.assignmenttwo.streamingest.client.listener;

import fi.aalto.bdp.assignmenttwo.streamingest.client.model.Apartment;
import fi.aalto.bdp.assignmenttwo.streamingest.client.report.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ApartmentMessageListener {

    private final CassandraTemplate cassandraTemplate;

    private final ReportService reportService;

    @RabbitListener(containerFactory = "myRabbitListenerContainerFactory", queues = "${data.broker.queue-name}")
    public void receiveApartment(@Payload Apartment apartment) {
        cassandraTemplate.insert(apartment);
        reportService.registerOneMoreRequest();
    }
}
