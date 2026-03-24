package de.ostfale.va.application.domain.service.plannedtournaments;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentsDashboardStatistics;
import de.ostfale.va.application.port.in.ForProvidingPlannedTournamentStreams;
import de.ostfale.va.application.port.in.plannedtournaments.ForLoadingPlannedTournaments;
import de.ostfale.va.application.port.out.plannedtournaments.ForParsingPlannedTournaments;
import de.ostfale.va.framework.out.filesystem.ApplicationDirectoryConfiguration;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportPlannedTournamentsService implements ForLoadingPlannedTournaments {

    private static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";

    // cached tournaments
    private final List<PlannedTournament> tournaments = new ArrayList<>();

    private final ForParsingPlannedTournaments parser;
    private final ForProvidingPlannedTournamentStreams streamsProvider;

    private LocalDateTime lastRankingFileUpdate;

    public ImportPlannedTournamentsService(
            ForParsingPlannedTournaments parser,
            ForProvidingPlannedTournamentStreams streamsProvider) {
        this.parser = parser;
        this.streamsProvider = streamsProvider;
    }

    @Override
    public List<PlannedTournament> getAllPlannedTournaments() {
        if (tournaments.isEmpty()) {
            tournaments.addAll(loadFromSource());
        }
        log().debug("ImportPlannedTournamentsService :: Loaded {} tournaments from file", tournaments.size());
        return tournaments;
    }

    @Override
    public PlannedTournamentsDashboardStatistics calculateStatistics() {
        getAllPlannedTournaments();
        var lastDownloadDate = "";

        if (lastRankingFileUpdate != null) {
            lastDownloadDate = lastRankingFileUpdate.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
        }

        var nofTournamentsThisYear = tournaments.stream().filter(PlannedTournament::isFromCurrentYear).count();
        var nofTournamentsOpen = tournaments.stream().filter(PlannedTournament::isOpenTournament).count();
        var nofTournamentsNextYear = tournaments.stream().filter(PlannedTournament::isFromNextYear).count();

        return new PlannedTournamentsDashboardStatistics(
                lastDownloadDate,
                nofTournamentsThisYear,
                nofTournamentsNextYear,
                nofTournamentsOpen
        );
    }

    @Override
    public List<PlannedTournament> loadFromSource() {
        var dataDir = getApplicationSubDir(ApplicationDirectoryConfiguration.TOURNAMENT_DIR_NAME);
        List<File> tournamentFiles = readAllFiles(dataDir);
        Path rankingFilePath = tournamentFiles.getFirst().toPath();
        lastRankingFileUpdate = getFirstFileTimestamp(rankingFilePath);

        return streamsProvider.getPlannedTournamentStreams().stream()
                .flatMap(stream -> parser.parsePlannedTournaments(stream).stream())
                .toList();
    }
}
