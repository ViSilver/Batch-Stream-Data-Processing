package fi.aalto.bdp.assignmenttwo.streamingest.client.report;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReportService {

    private final AtomicLong processedMessages = new AtomicLong();

    public void registerOneMoreRequest() {
        processedMessages.incrementAndGet();
    }

    public synchronized void reset() {
        processedMessages.set(0L);
    }

    public long getProcessedMessages() {
        return processedMessages.get();
    }
}
