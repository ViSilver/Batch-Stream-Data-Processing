package fi.aalto.bdp.assignmenttwo.streamingest.client.rest;

import fi.aalto.bdp.assignmenttwo.streamingest.client.report.model.ReportMetric;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "report-metrics", url = "${stream-ingest-manager.url}")
public interface StreamIngestManagerClient {

    @RequestMapping(method = RequestMethod.POST, value = "/report-metrics", produces = "application/json")
    void postReport(ReportMetric reportMetric);

}
