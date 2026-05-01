package de.ostfale.va.application.domain.service.ranking;

import de.ostfale.va.application.domain.model.playerrankings.HistoryTimestamp;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.PlayerId;
import de.ostfale.va.application.port.in.ranking.ForBatchProcessingRankingFiles;
import de.ostfale.va.application.port.in.ranking.ForLoadingRankings;
import de.ostfale.va.application.port.out.ranking.ForLoadingPlayers;
import de.ostfale.va.application.port.out.ranking.ForParsingRankingFile;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
public class RankingFilesBatchProcessorUC implements ForBatchProcessingRankingFiles, UseFileSystemHandling, UseLogging {

    private static final String RANKING_HISTORY_SUBDIR = Path.of("ranking", "history").toString();

    private final ForParsingRankingFile parser;
    private final ForLoadingPlayers loadedPlayers;
    private final ForLoadingRankings loadingRankings;

    public RankingFilesBatchProcessorUC(ForParsingRankingFile parser, ForLoadingPlayers loadedPlayers, ForLoadingRankings loadingRankings) {
        this.parser = parser;
        this.loadedPlayers = loadedPlayers;
        this.loadingRankings = loadingRankings;
    }

    @Async
    @Override
    public void processRankingFiles() {
        log().info("RankingFilesBatchProcessorUC :: Starting async batch import of historical data...");

        Path historyDir = Path.of(getApplicationSubDir(RANKING_HISTORY_SUBDIR));
        try (Stream<Path> paths = Files.list(historyDir)) {

            Map<PlayerId, Player> playersMap = new HashMap<>();   // for batch starts always with empty map

            List<Path> excelFiles = paths
                    .filter(p -> p.toString().endsWith(".xlsx"))
                    .sorted(Comparator.comparing(p -> new HistoryTimestamp(p.getFileName().toString())))
                    .toList();

            for (Path file : excelFiles) {
                String fileName = file.getFileName().toString();
                log().info("RankingFilesBatchProcessorUC :: Processing file: {}", fileName);

                List<Player> weeklyRanking = parser.parseRankingFile(file);
                weeklyRanking.forEach(player -> processPlayer(player, fileName, playersMap));

                log().debug("BatchProcessor :: {} Player from {} processed", weeklyRanking.size(), fileName);
            }

            loadedPlayers.save(playersMap.values().stream().toList());
            loadingRankings.invalidateCache();
        } catch (Exception e) {
            log().error("RankingFilesBatchProcessorUC :: Critical error during batch processing", e);
        }
    }

    private void processPlayer(Player player, String fileName, Map<PlayerId, Player> playersMap) {
        var dbPlayer = playersMap.get(player.getPlayerId());
        if (dbPlayer == null) {
            playersMap.put(player.getPlayerId(), player);
            return;
        }

        String cwYear = new HistoryTimestamp(fileName).cwyear();
        applyFieldChange(cwYear, "age class", player.getAgeClassGeneral(), dbPlayer.getAgeClassGeneral(), dbPlayer::setAgeClassGeneral, dbPlayer);
        applyFieldChange(cwYear, "club name", player.getClubName(), dbPlayer.getClubName(), dbPlayer::setClubName, dbPlayer);
        applyFieldChange(cwYear, "state name", player.getStateName(), dbPlayer.getStateName(), dbPlayer::setStateName, dbPlayer);
        applyFieldChange(cwYear, "district name", player.getDistrictName(), dbPlayer.getDistrictName(), dbPlayer::setDistrictName, dbPlayer);

        player.getHistory().values().stream().findFirst().ifPresent(historyStatistics -> {
            dbPlayer.addHistoryEntry(fileName, historyStatistics);
        });
    }

    private void applyFieldChange(String cwYear, String fieldName, String newValue, String oldValue,
                                  Consumer<String> setter, Player dbPlayer) {
        if (newValue == null || newValue.equals(oldValue)) {
            return;
        }
        log().trace("RankingFilesBatchProcessorUC :: found change in {} for player {}: {} -> {}", fieldName, dbPlayer.getPlayerId().playerId(), oldValue, newValue);
        dbPlayer.addHistoryChange(cwYear, oldValue, newValue);
        setter.accept(newValue);
    }
}
