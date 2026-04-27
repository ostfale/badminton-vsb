package de.ostfale.va.application.domain.service.ranking;

import de.ostfale.va.application.domain.model.playerrankings.GenderType;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.RankingDashboardStatistics;
import de.ostfale.va.application.port.in.ranking.ForLoadingRankings;
import de.ostfale.va.application.port.out.ranking.ForLoadingPlayers;
import de.ostfale.va.application.port.out.ranking.ForParsingRankingFile;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.out.filesystem.ApplicationDirectoryConfiguration;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
public class ImportRankingsService implements ForLoadingRankings, UseLogging {

    private static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";

    private final ForParsingRankingFile parser;
    private final ForLoadingPlayers forLoadingPlayers;
    private LocalDateTime lastRankingFileUpdate;
    private volatile List<Player> cachedPlayers;

    public ImportRankingsService(ForParsingRankingFile parser, ForLoadingPlayers forLoadingPlayers) {
        this.parser = parser;
        this.forLoadingPlayers = forLoadingPlayers;
    }

    @Override
    public List<Player> getAllPlayers() {
        if (cachedPlayers != null) {
            return cachedPlayers;
        }

        synchronized (this) {
            if (cachedPlayers != null) {
                return cachedPlayers;
            }

            // ONLY load from EclipseStore (database) now, no automatic fallback to the file
            List<Player> playersFromStore = forLoadingPlayers.findAllPlayers();
            cachedPlayers = List.copyOf(playersFromStore);
            log().info("ImportRankingsService :: Loaded {} players from store", cachedPlayers.size());
            return cachedPlayers;
        }
    }

    @Override
    public void importRankingsFromFile() {
        log().info("ImportRankingsService :: Importing players from ranking file to store");
        List<Player> localPlayers = loadFromSource();
        List<Player> savedPlayers = forLoadingPlayers.save(localPlayers);
        synchronized (this) {
            cachedPlayers = List.copyOf(savedPlayers);
        }
        log().info("ImportRankingsService :: Successfully imported and cached {} players", savedPlayers.size());
    }

    @Override
    public RankingDashboardStatistics calculateStatistics() {
        var players = getAllPlayers();
        var lastDownloadDate = "";
        
        // The count is purely based on the players stored/cached in the EclipseStore
        var nofPlayers = players.size();
        var nofMalePlayers = players.stream().filter(player -> GenderType.MALE.equals(player.getGender())).count();
        var nofFemalePlayers = players.stream().filter(player -> GenderType.FEMALE.equals(player.getGender())).count();

        // The timestamp is still determined based on the file in the directory as requested
        if (lastRankingFileUpdate == null) {
            var rankingDir = getApplicationSubDir(ApplicationDirectoryConfiguration.RANKING_DIR_NAME);
            List<File> rankingFiles = readAllFiles(rankingDir);
            if (!rankingFiles.isEmpty()) {
                lastRankingFileUpdate = getFirstFileTimestamp(rankingFiles.getFirst().toPath());
            }
        }

        if (lastRankingFileUpdate != null) {
            lastDownloadDate = lastRankingFileUpdate.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
        }

        return new RankingDashboardStatistics(
                lastDownloadDate,
                nofPlayers,
                nofFemalePlayers,
                nofMalePlayers
        );
    }

    private List<Player> loadFromSource() {
        var rankingDir = getApplicationSubDir(ApplicationDirectoryConfiguration.RANKING_DIR_NAME);
        List<File> rankingFiles = readAllFiles(rankingDir);

        if (rankingFiles.isEmpty()) {
            log().warn("ImportRankingsService ::No ranking files found in {}", rankingDir);
            return List.of();
        }

        if (rankingFiles.size() > 1) {
            log().warn("ImportRankingsService :: Found more than one ranking file in {}. Remove unused file(s)", rankingDir);
            return List.of();
        }

        Path rankingFilePath = rankingFiles.getFirst().toPath();
        List<Player> players = parser.parseRankingFile(rankingFilePath);
        log().info("ImportRankingsService :: Imported {} players from ranking file {}", players.size(), rankingFilePath);

        lastRankingFileUpdate = getFirstFileTimestamp(rankingFilePath);

        return players;
    }

    @Override
    public List<Player> findPlayers(String filter, int offset, int limit) {
        if (filter == null || filter.isBlank()) return Collections.emptyList();

        String[] tokens = filter.toLowerCase().split("\\s+");
        return getAllPlayers().stream()
                .filter(player -> matchPlayer(player, tokens))
                .skip(offset)
                .limit(limit)
                .toList();
    }

    @Override
    public int countPlayers(String filter) {
        var players = getAllPlayers();
        if (filter == null || filter.isBlank()) return 0;

        String[] tokens = filter.toLowerCase().split("\\s+");
        return (int) players.stream()
                .filter(player -> matchPlayer(player, tokens))
                .count();
    }

    private boolean matchPlayer(Player player, String[] tokens) {
        String firstName = (player.getFirstName() != null) ? player.getFirstName().toLowerCase() : "";
        String lastName = (player.getLastName() != null) ? player.getLastName().toLowerCase() : "";

        for (String token : tokens) {
            if (token.isEmpty()) continue;
            // The player must contain EVERY token somewhere (first name or last name)
            if (!firstName.contains(token) && !lastName.contains(token)) {
                return false;
            }
        }
        return true;
    }
}
