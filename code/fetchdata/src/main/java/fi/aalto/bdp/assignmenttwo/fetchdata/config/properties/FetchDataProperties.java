package fi.aalto.bdp.assignmenttwo.fetchdata.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "fetch-data")
public class FetchDataProperties {

    private String inputDirectory;
    private String outputDirectory;
    private int maxNrOfUserFiles;
    private int maxFileSize;
    private Quartz quartz;

    @Data
    public static class Quartz {
        private int jobInterval;
    }
}
