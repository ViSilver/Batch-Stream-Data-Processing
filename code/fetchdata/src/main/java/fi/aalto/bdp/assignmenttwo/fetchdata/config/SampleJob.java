package fi.aalto.bdp.assignmenttwo.fetchdata.config;

import fi.aalto.bdp.assignmenttwo.fetchdata.service.FileCopyService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SampleJob implements Job {

    /**
     * It cannot be user constructor injection because of the @code{AutoWiringSpringBeanJobFactory.createJobInstance()}
     */
    @Autowired
    private FileCopyService fileCopyService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        log.info("Starting job");
        fileCopyService.getListOfInputFiles();
//        log.info("Job ended");
    }
}
