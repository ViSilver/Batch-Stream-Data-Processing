package fi.aalto.bdp.assignmenttwo.streamingest.client.config;

import fi.aalto.bdp.assignmenttwo.streamingest.client.report.ReportService;
import fi.aalto.bdp.assignmenttwo.streamingest.client.report.model.ReportMetric;
import fi.aalto.bdp.assignmenttwo.streamingest.client.rest.StreamIngestManagerClient;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SampleJob implements Job {

    /**
     * It cannot be user constructor injection because of the @code{AutoWiringSpringBeanJobFactory.createJobInstance()}
     */
    @Autowired
    private ReportService reportService;

    @Autowired
    private StreamIngestManagerClient managerClient;

    @Value("${client-stream-ingest.quartz.jobInterval}")
    private int jobInterval;

    @Value("${data.broker.queue-name}")
    private String queue_name;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        long numberOfMessages = reportService.getProcessedMessages();
        reportService.reset();

        double rate = numberOfMessages / (double) jobInterval;
        double averageIngestionTime = 1 / rate;

        ReportMetric reportMetric = new ReportMetric(queue_name, numberOfMessages, rate, averageIngestionTime, 0.142 * numberOfMessages);
        if (reportMetric.getNumberOfMessages() != 0) {
            managerClient.postReport(reportMetric);
            log.info("Posted the report metric: {}", reportMetric);
        }
    }
}
