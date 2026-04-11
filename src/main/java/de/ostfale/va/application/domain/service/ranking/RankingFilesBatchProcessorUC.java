package de.ostfale.va.application.domain.service.ranking;

import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.port.in.ranking.ForBatchProcessingRankingFiles;
import de.ostfale.va.application.port.out.ranking.ForParsingRankingFile;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@Service
public class RankingFilesBatchProcessorUC implements ForBatchProcessingRankingFiles, UseFileSystemHandling, UseLogging {

    private static final String RANKING_HISTORY_SUBDIR = Path.of("ranking", "history").toString();

    private final ForParsingRankingFile parser;

    public RankingFilesBatchProcessorUC(ForParsingRankingFile parser) {
        this.parser = parser;
    }

    @Async
    @Override
    public void processRankingFiles() {
        log().info("RankingFilesBatchProcessorUC :: Starting async batch import of historical data...");

        var historyDirectory = getApplicationSubDir(RANKING_HISTORY_SUBDIR);
        Path historyDir = Paths.get(historyDirectory);

        try (Stream<Path> paths = Files.list(historyDir)) {

            List<Path> excelFiles = paths
                    .filter(p -> p.toString().endsWith(".xlsx"))
                    .sorted()
                    .toList();

            for (Path file : excelFiles) {
                String fileName = file.getFileName().toString();
                LocalDate refDate = parseDateFromFilename(fileName);

                log().info("RankingFilesBatchProcessorUC :: Processing file: {}", fileName);
                List<Player> weeklySnapshots = parser.parseRankingFile(file);

                for (Player player : weeklySnapshots) {

                }
                log().debug("BatchProcessor :: {} Player from {} processed", weeklySnapshots.size(), fileName);
            }
        } catch (Exception e) {
            log().error("RankingFilesBatchProcessorUC :: Critical error during batch processing", e);
        }
    }

    // expects format Ranking_2026_KW14.xlsx
    private LocalDate parseDateFromFilename(String fileName) {
        try {
            String[] parts = fileName.replace(".xlsx", "").split("_");
            int year = Integer.parseInt(parts[1]);
            int week = Integer.parseInt(parts[2].replace("KW", ""));

            return LocalDate.now()
                    .withYear(year)
                    .with(WeekFields.of(Locale.GERMANY).weekOfYear(), week)
                    .with(WeekFields.of(Locale.GERMANY).dayOfWeek(), 1); // monday of week
        } catch (Exception e) {
            log().warn("RankingFilesBatchProcessorUC :: Could not read date from file {} !", fileName);
            return null;
        }
    }
}
