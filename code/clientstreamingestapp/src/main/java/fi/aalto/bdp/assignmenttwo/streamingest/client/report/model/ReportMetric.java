package fi.aalto.bdp.assignmenttwo.streamingest.client.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@NoArgsConstructor
@AllArgsConstructor
public class ReportMetric {

    private String customerId;
    private long numberOfMessages;
    private double processingRate;
    private double averageIngestionTime;
    private double totalIngestionDataSize;

}
