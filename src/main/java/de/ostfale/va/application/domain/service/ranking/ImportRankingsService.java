package de.ostfale.va.application.domain.service.ranking;

import de.ostfale.va.application.domain.model.playerrankings.GenderType;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.RankingDashboardStatistics;
import de.ostfale.va.application.port.in.ranking.ForLoadingRankings;
import de.ostfale.va.application.port.out.ranking.ForParsingRankingFile;
import de.ostfale.va.framework.out.filesystem.ApplicationDirectoryConfiguration;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ImportRankingsService implements ForLoadingRankings {

    private static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";

    // cached players
    private final List<Player> players = new ArrayList<>();

    private final ForParsingRankingFile parser;
    private LocalDateTime lastRankingFileUpdate;

    public ImportRankingsService(ForParsingRankingFile parser) {
        this.parser = parser;
    }

    @Override
    public List<Player> getAllPlayers() {
        if (players.isEmpty()) {
            players.addAll(loadFromSource());
        }
        log().trace("ImportRankingsService :: Loaded {} players from ranking file", players.size());
        return players;
    }

    @Override
    public RankingDashboardStatistics calculateStatistics() {
        players.clear();
        players.addAll(loadFromSource());
        var lastDownloadDate = "";
        var nofPlayers = players.size();
        var nofMalePlayers = players.stream().filter(player -> GenderType.MALE.equals(player.getGender())).count();
        var nofFemalePlayers = players.stream().filter(player -> GenderType.FEMALE.equals(player.getGender())).count();

        if (lastRankingFileUpdate != null) {
            lastDownloadDate = lastRankingFileUpdate.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
        }

        return new RankingDashboardStatistics(
                lastDownloadDate,
                "",
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
        if (players.isEmpty()) getAllPlayers();
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
            // Der Spieler muss JEDES Token irgendwo (Vorname oder Nachname) enthalten
            if (!firstName.contains(token) && !lastName.contains(token)) {
                return false;
            }
        }
        return true;
    }
}
