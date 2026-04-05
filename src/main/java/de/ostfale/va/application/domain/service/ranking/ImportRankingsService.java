package de.ostfale.va.application.domain.service.ranking;

import de.ostfale.va.application.domain.model.playerrankings.*;
import de.ostfale.va.application.port.in.ranking.ForLoadingRankings;
import de.ostfale.va.application.port.out.ranking.ForParsingRankingFile;
import de.ostfale.va.application.port.out.ranking.PlayerRepository;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.out.filesystem.ApplicationDirectoryConfiguration;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ImportRankingsService implements ForLoadingRankings, UseFileSystemHandling, UseLogging {

    private static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";

    private final ForParsingRankingFile parser;
    private final PlayerRepository playerRepository;

    public ImportRankingsService(PlayerRepository playerRepository, ForParsingRankingFile parser) {
        this.playerRepository = playerRepository;
        this.parser = parser;
    }

    @Override
    public List<Player> getAllPlayers() {
        // Data is always fetched from the persistent graph via repository
        return playerRepository.findAll();
    }

    @Override
    public List<Player> findPlayers(String filter, int offset, int limit) {
        if (filter == null || filter.isBlank()) {
            return List.of();
        }

        String[] tokens = filter.toLowerCase().split("\\s+");
        return getAllPlayers().stream()
                .filter(player -> matchPlayer(player, tokens))
                .skip(offset)
                .limit(limit)
                .toList();
    }

    @Override
    public RankingDashboardStatistics calculateStatistics() {
        List<Player> allPlayers = playerRepository.findAll();

        long nofPlayers = allPlayers.size();
        long nofMale = allPlayers.stream().filter(p -> GenderType.MALE.equals(p.getGender())).count();
        long nofFemale = allPlayers.stream().filter(p -> GenderType.FEMALE.equals(p.getGender())).count();

        // Placeholder for last download date - this could be retrieved from a metadata store
        String lastUpdateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));

        return new RankingDashboardStatistics(lastUpdateStr, "", nofPlayers, nofFemale, nofMale);
    }

    @Override
    public int countPlayers(String filter) {
        if (filter == null || filter.isBlank()) {
            return (int) playerRepository.count();
        }

        String[] tokens = filter.toLowerCase().split("\\s+");
        return (int) playerRepository.findAll().stream()
                .filter(player -> matchPlayer(player, tokens))
                .count();
    }

    @Override
    public void updateRankingsFromSource() {
        var rankingDir = getApplicationSubDir(ApplicationDirectoryConfiguration.RANKING_DIR_NAME);
        List<File> rankingFiles = readAllFiles(rankingDir);

        if (rankingFiles.isEmpty()) {
            log().info("ImportRankingsService :: No ranking files found for update.");
            return;
        }

        Path rankingFilePath = rankingFiles.getFirst().toPath();
        LocalDateTime fileTime = getFirstFileTimestamp(rankingFilePath);

        // Use the new custom repository methods
        LocalDateTime dbTime = playerRepository.getLastUpdate();
        if (dbTime == null || fileTime.isAfter(dbTime)) {
            log().info("ImportRankingsService :: New file detected. Starting import...");

            List<Player> snapshots = parser.parseRankingFile(rankingFilePath);
            for (Player snapshot : snapshots) {
                Player persistentPlayer = playerRepository.findById(snapshot.getPlayerId().playerId())
                        .orElseGet(() -> playerRepository.save(snapshot));

                updatePlayerHistory(persistentPlayer, snapshot, fileTime.toLocalDate());
                playerRepository.save(persistentPlayer);
            }

            // Update metadata after successful import
            playerRepository.setLastUpdate(fileTime);
            log().info("ImportRankingsService :: Update completed for timestamp {}", fileTime);
        } else {
            log().info("ImportRankingsService :: Data is up to date (DB: {}, File: {})", dbTime, fileTime);
        }
    }

    /**
     * Helper to transfer data from the parsed snapshot to the persistent player's history.
     */
    private void updatePlayerHistory(Player target, Player source, LocalDate date) {
        // Add single ranking snapshot if available
        if (source.getSinglePoints() > 0 || source.getSingleRanking() > 0) {
            target.addHistoryEntry(date, DisciplineType.SINGLE, new RankingSnapshot(
                    source.getSinglePoints(), source.getSingleRanking(),
                    source.getSingleAgeRanking(), source.getSingleTournaments()));
        }

        // Add double ranking snapshot if available
        if (source.getDoublePoints() > 0 || source.getDoubleRanking() > 0) {
            target.addHistoryEntry(date, DisciplineType.DOUBLE, new RankingSnapshot(
                    source.getDoublePoints(), source.getDoubleRanking(),
                    source.getDoubleAgeRanking(), source.getDoubleTournaments()));
        }

        // Add mixed ranking snapshot if available
        if (source.getMixedPoints() > 0 || source.getMixedRanking() > 0) {
            target.addHistoryEntry(date, DisciplineType.MIXED, new RankingSnapshot(
                    source.getMixedPoints(), source.getMixedRanking(),
                    source.getMixedAgeRanking(), source.getMixedTournaments()));
        }
    }

    // Internal matching logic for tokenized search strings
    private boolean matchPlayer(Player player, String[] tokens) {
        String firstName = (player.getFirstName() != null) ? player.getFirstName().toLowerCase() : "";
        String lastName = (player.getLastName() != null) ? player.getLastName().toLowerCase() : "";

        for (String token : tokens) {
            if (token.isEmpty()) continue;
            if (!firstName.contains(token) && !lastName.contains(token)) {
                return false;
            }
        }
        return true;
    }
}
