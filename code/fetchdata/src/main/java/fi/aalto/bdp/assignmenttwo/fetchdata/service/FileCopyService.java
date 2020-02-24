package fi.aalto.bdp.assignmenttwo.fetchdata.service;

import fi.aalto.bdp.assignmenttwo.fetchdata.config.properties.FetchDataProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileCopyService {

    private final FetchDataProperties fetchDataProperties;

    public void getListOfInputFiles() {
        File[] listOfInputFiles = getFilesFrom(fetchDataProperties.getInputDirectory());
        log.info("Reading from {}", fetchDataProperties.getInputDirectory());

        File[] listOfOutputFiles = getFilesFrom(fetchDataProperties.getOutputDirectory());
        List<File> existingOutputFiles = Optional.ofNullable(listOfOutputFiles).map(Arrays::asList).orElse(new ArrayList<>());
//        log.info("List of existing files in output directory: {}", existingOutputFiles);

        Optional.ofNullable(listOfInputFiles)
            .ifPresent(files -> Arrays.stream(files)
                .filter(this::validatesConstraints)
                .limit(fetchDataProperties.getMaxNrOfUserFiles())
                .filter(inputFile -> !isItAlreadyCopied(inputFile, existingOutputFiles))
                .forEach(this::processFile));
    }

    private File[] getFilesFrom(final String directoryPath) {
        return new File(directoryPath).listFiles();
    }

    private boolean isItAlreadyCopied(final File inputFile, final List<File> existingOutputFiles) {
        return existingOutputFiles.stream()
            .map(File::getName)
            .anyMatch(outFileName -> inputFile.getName().equals(outFileName));
    }

    private boolean validatesConstraints(final File inputFile) {
        return ((double) inputFile.length() / (1024 * 1024)) < fetchDataProperties.getMaxFileSize();
    }

    private void processFile(final File inputFile) {
        if (inputFile.isFile()) {
            copyAndDeleteFile(inputFile);
        } else if (inputFile.isDirectory()) {
            log.warn("Found a directory {}. Directory processing is not supported!", inputFile.getName());
        }
    }

    private void copyAndDeleteFile(final File inputFile) {
        log.info("File to be copied {}, length = {} MB. Max file size = {}", inputFile.getName(), (double) inputFile.length() / (1024 * 1024), fetchDataProperties.getMaxFileSize());
        File outputFile = new File(fetchDataProperties.getOutputDirectory() + inputFile.getName());
        try {
            Files.copy(inputFile.toPath(), outputFile.toPath());
            Files.delete(inputFile.toPath());
            log.info("Copied file {}", outputFile.getName());
        } catch (IOException e) {
            log.error("An error occurred while copying file {}", inputFile.getName(), e);
        }
    }
}
