package de.ostfale.va.application.domain.service.ranking;

import de.ostfale.va.application.domain.model.playerrankings.HistoryTimestamp;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.port.in.ranking.ForBatchProcessingRankingFiles;
import de.ostfale.va.application.port.out.ranking.ForParsingRankingFile;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class RankingFilesBatchProcessorUC implements ForBatchProcessingRankingFiles, UseFileSystemHandling, UseLogging {

    private static final String RANKING_HISTORY_SUBDIR = Path.of("ranking", "history").toString();

    private final ForParsingRankingFile parser;
    private final InMemoryPlayerService inMemoryPlayerService;

    public RankingFilesBatchProcessorUC(ForParsingRankingFile parser, InMemoryPlayerService inMemoryPlayerService) {
        this.parser = parser;
        this.inMemoryPlayerService = inMemoryPlayerService;
    }

    @Async
    @Override
    public void processRankingFiles() {
        log().info("RankingFilesBatchProcessorUC :: Starting async batch import of historical data into memory...");

        // 1. Clear any existing in-memory data to ensure a clean import
        inMemoryPlayerService.clear();

        Path historyDir = Path.of(getApplicationSubDir(RANKING_HISTORY_SUBDIR));
        try (Stream<Path> paths = Files.list(historyDir)) {

            // 2. Find all Excel files and sort them chronologically
            List<Path> excelFiles = paths
                    .filter(p -> p.toString().endsWith(".xlsx"))
                    .sorted(Comparator.comparing(p -> new HistoryTimestamp(p.getFileName().toString())))
                    .toList();

            log().info("RankingFilesBatchProcessorUC :: Found {} historical files to process.", excelFiles.size());

            // 3. Process each file sequentially to build up the player state
            for (Path file : excelFiles) {
                log().debug("RankingFilesBatchProcessorUC :: Processing file: {}", file.getFileName().toString());
                HistoryTimestamp timestamp = new HistoryTimestamp(file.getFileName().toString());
                List<Player> parsedPlayers = parser.parseRankingFile(file);
                inMemoryPlayerService.mergePlayers(parsedPlayers, timestamp);
            }

            log().info("RankingFilesBatchProcessorUC :: Successfully finished batch import. Total players in memory: {}", inMemoryPlayerService.getAllPlayers().size());

        } catch (Exception e) {
            log().error("RankingFilesBatchProcessorUC :: Critical error during batch processing", e);
        }
    }
}
