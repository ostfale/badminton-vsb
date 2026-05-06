package de.ostfale.va.application.domain.service.ranking;

import de.ostfale.va.application.domain.model.playerrankings.GenderType;
import de.ostfale.va.application.domain.model.playerrankings.HistoryTimestamp;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.RankingDashboardStatistics;
import de.ostfale.va.application.port.in.ranking.ForLoadingRankings;
import de.ostfale.va.application.port.out.ranking.ForParsingRankingFile;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.out.filesystem.ApplicationDirectoryConfiguration;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ImportRankingsService implements ForLoadingRankings, UseLogging {

    private static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";

    private final ForParsingRankingFile parser;
    private final InMemoryPlayerService inMemoryPlayerService;
    private LocalDateTime lastRankingFileUpdate;

    public ImportRankingsService(ForParsingRankingFile parser, InMemoryPlayerService inMemoryPlayerService) {
        this.parser = parser;
        this.inMemoryPlayerService = inMemoryPlayerService;
    }

    @Override
    public List<Player> getAllPlayers() {
        return inMemoryPlayerService.getAllPlayers();
    }

    @Override
    public void importRankingsFromFile() {
        log().info("ImportRankingsService :: Importing players from ranking file to memory");
        
        var rankingDir = getApplicationSubDir(ApplicationDirectoryConfiguration.RANKING_DIR_NAME);
        List<File> rankingFiles = readAllFiles(rankingDir);

        if (rankingFiles.isEmpty()) {
            log().warn("ImportRankingsService ::No ranking files found in {}", rankingDir);
            return;
        }

        if (rankingFiles.size() > 1) {
            log().warn("ImportRankingsService :: Found more than one ranking file in {}. Remove unused file(s)", rankingDir);
            return;
        }

        Path rankingFilePath = rankingFiles.getFirst().toPath();
        List<Player> parsedPlayers = parser.parseRankingFile(rankingFilePath);
        log().info("ImportRankingsService :: Imported {} players from ranking file {}", parsedPlayers.size(), rankingFilePath);

        lastRankingFileUpdate = getFirstFileTimestamp(rankingFilePath);
        HistoryTimestamp timestamp = new HistoryTimestamp(rankingFilePath.getFileName().toString());

        if (!parsedPlayers.isEmpty()) {
            inMemoryPlayerService.mergePlayers(parsedPlayers, timestamp);
        }
    }

    @Override
    public RankingDashboardStatistics calculateStatistics() {
        var players = inMemoryPlayerService.getAllPlayers();
        var lastDownloadDate = "";
        
        var nofPlayers = players.size();
        var nofMalePlayers = players.stream().filter(player -> GenderType.MALE.equals(player.getGender())).count();
        var nofFemalePlayers = players.stream().filter(player -> GenderType.FEMALE.equals(player.getGender())).count();

        // The timestamp is determined based on the file in the directory
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

    @Override
    public List<Player> findPlayers(String filter, int offset, int limit) {
        return inMemoryPlayerService.findPlayers(filter, offset, limit);
    }

    @Override
    public int countPlayers(String filter) {
        return inMemoryPlayerService.countPlayers(filter);
    }
}
