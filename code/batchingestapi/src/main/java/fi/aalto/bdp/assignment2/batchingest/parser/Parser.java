package fi.aalto.bdp.assignment2.batchingest.parser;

import com.univocity.parsers.csv.CsvRoutines;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.cassandra.core.CassandraTemplate;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
public class Parser<T> {

    private final Class<T> type;

    private final CsvRoutines csvRoutines;

    private final CassandraTemplate cassandraTemplate;

    public void parse(final File csvFile) {
//        File csvFile = new File(fileName);
//        if (!csvFile.exists()) {
//            csvFile = new ClassPathResource(csvFile.getName()).getFile();
//            log.debug("Opened file from classpath: {}", csvFile.getName());
//        }

        Iterable<T> iterable = csvRoutines.iterate(type, csvFile);
        Stream<T> stream = StreamSupport.stream(iterable.spliterator(), true);
        log.info("Beginning reading the file");
        long begin = System.currentTimeMillis();

        stream.forEach(cassandraTemplate::insert);

        long end = System.currentTimeMillis();
        log.info("Processing took {} seconds", (end - begin)/1_000L);
//        log.info("Processed file size: {}");
    }
}
