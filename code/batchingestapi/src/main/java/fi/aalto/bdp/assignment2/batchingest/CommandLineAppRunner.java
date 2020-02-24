package fi.aalto.bdp.assignment2.batchingest;

import com.datastax.driver.core.Session;
import fi.aalto.bdp.assignment2.batchingest.parser.Parser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandLineAppRunner implements CommandLineRunner {

    @Value("${csv.file.path}")
    private String csvFilePath;

    @Value("${csv.file.directory}")
    private String csvFileDirectory;

    private final Session session;

    private final Parser parser;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting user application");

        File[] listOfOutputFiles = getFilesFrom(csvFileDirectory);
        List<File> existingOutputFiles = Optional.ofNullable(listOfOutputFiles).map(Arrays::asList).orElse(new ArrayList<>());

        existingOutputFiles.forEach(parser::parse);

//        parser.parse(csvFilePath);
        session.close();
        System.exit(1);
    }


    private File[] getFilesFrom(final String directoryPath) {
        return new File(directoryPath).listFiles();
    }
}
