package fi.aalto.bdp.assignment2.batchingest.config;

import com.univocity.parsers.common.Context;
import com.univocity.parsers.common.DataProcessingException;
import com.univocity.parsers.common.RetryableErrorHandler;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvRoutines;
import com.univocity.parsers.csv.UnescapedQuoteHandling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Slf4j
@Configuration
public class ParserConfig {

    @Bean
    public CsvRoutines csvRoutines() {
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.getFormat().setLineSeparator(System.lineSeparator());
        parserSettings.setLineSeparatorDetectionEnabled(true);
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.setIgnoreTrailingWhitespaces(true);
        parserSettings.setIgnoreLeadingWhitespaces(true);
        parserSettings.setUnescapedQuoteHandling(UnescapedQuoteHandling.STOP_AT_CLOSING_QUOTE);

        parserSettings.setProcessorErrorHandler(new RetryableErrorHandler() {
            @Override
            public void handleError(DataProcessingException error, Object[] inputRow, Context context) {
                log.error("Error processing row: {}", Arrays.toString(inputRow));
                log.error("Error details: column '{}' (index {}) has value '{}'", error.getColumnName(), error.getColumnIndex(), inputRow[error.getColumnIndex()]);

                if(error.getColumnIndex() == 0){
                    setDefaultValue(null);
                } else {
                    keepRecord(); //prevents the parser from discarding the row.
                }
            }
        });
        return new CsvRoutines(parserSettings);
    }

}
