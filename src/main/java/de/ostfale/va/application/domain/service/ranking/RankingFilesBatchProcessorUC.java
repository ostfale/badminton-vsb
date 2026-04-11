package de.ostfale.va.application.domain.service.ranking;

import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.PlayerId;
import de.ostfale.va.application.port.in.ranking.ForBatchProcessingRankingFiles;
import de.ostfale.va.application.port.out.ranking.ForLoadingPlayers;
import de.ostfale.va.application.port.out.ranking.ForParsingRankingFile;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RankingFilesBatchProcessorUC implements ForBatchProcessingRankingFiles, UseFileSystemHandling, UseLogging {

    private static final String RANKING_HISTORY_SUBDIR = Path.of("ranking", "history").toString();

    private final ForParsingRankingFile parser;
    private final ForLoadingPlayers loadedPlayers;

    public RankingFilesBatchProcessorUC(ForParsingRankingFile parser, ForLoadingPlayers loadedPlayers) {
        this.parser = parser;
        this.loadedPlayers = loadedPlayers;
    }

    @Async
    @Override
    public void processRankingFiles() {
        log().info("RankingFilesBatchProcessorUC :: Starting async batch import of historical data...");

        var historyDirectory = getApplicationSubDir(RANKING_HISTORY_SUBDIR);
        Path historyDir = Paths.get(historyDirectory);

        try (Stream<Path> paths = Files.list(historyDir)) {
            Map<PlayerId, Player> playersById = loadedPlayers.findAllPlayers().stream()
                    .collect(Collectors.toMap(Player::getPlayerId, Function.identity()));
            Set<PlayerId> changedPlayerIds = new HashSet<>();

            List<Path> excelFiles = paths
                    .filter(p -> p.toString().endsWith(".xlsx"))
                    .sorted()
                    .toList();

            for (Path file : excelFiles) {
                String fileName = file.getFileName().toString();

                log().info("RankingFilesBatchProcessorUC :: Processing file: {}", fileName);
                List<Player> weeklySnapshots = parser.parseRankingFile(file);

                for (Player player : weeklySnapshots) {
                    var dbPlayer = playersById.get(player.getPlayerId());
                    if (dbPlayer != null) {
                        var history = player.getHistory().values().stream().findFirst();
                        history.ifPresent(historyStatistics -> {
                            dbPlayer.addHistoryEntry(fileName, historyStatistics);
                            changedPlayerIds.add(player.getPlayerId());
                        });
                    }
                }
                log().debug("BatchProcessor :: {} Player from {} processed", weeklySnapshots.size(), fileName);
            }

            if (!changedPlayerIds.isEmpty()) {
                List<Player> changedPlayers = new ArrayList<>(changedPlayerIds.size());
                for (var playerId : changedPlayerIds) {
                    changedPlayers.add(playersById.get(playerId));
                }
                loadedPlayers.save(changedPlayers);
                log().info("RankingFilesBatchProcessorUC :: Persisted {} players with updated history", changedPlayers.size());
            } else {
                log().info("RankingFilesBatchProcessorUC :: No player history changes detected");
            }
        } catch (Exception e) {
            log().error("RankingFilesBatchProcessorUC :: Critical error during batch processing", e);
        }
    }
}
