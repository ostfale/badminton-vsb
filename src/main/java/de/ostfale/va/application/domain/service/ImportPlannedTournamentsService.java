package de.ostfale.va.application.domain.service;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.port.in.ForImportingPlannedTournaments;
import de.ostfale.va.application.port.in.ForProvidingPlannedTournamentStreams;
import de.ostfale.va.application.port.out.ForParsingPlannedTournaments;
import de.ostfale.va.application.port.out.ForPlannedTournamentsDownloadConfig;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImportPlannedTournamentsService implements ForImportingPlannedTournaments, UseLogging {

    private final ForParsingPlannedTournaments parser;
    private final ForProvidingPlannedTournamentStreams streamsProvider;
    private final ForPlannedTournamentsDownloadConfig downloadConfig;

    public ImportPlannedTournamentsService(
            ForParsingPlannedTournaments parser,
            ForProvidingPlannedTournamentStreams streamsProvider, ForPlannedTournamentsDownloadConfig downloadConfig) {
        this.parser = parser;
        this.streamsProvider = streamsProvider;
        this.downloadConfig = downloadConfig;
    }

    @Override
    public List<PlannedTournament> importFromSource() {
        return streamsProvider.getPlannedTournamentStreams().stream()
                .flatMap(stream -> parser.parsePlannedTournaments(stream).stream())
                .toList();
    }

    @Override
    public String getLastDownloadDate() {
        var result = streamsProvider.getDownloadDateInFileName();
        if (result != null) {
            var dateTimeFromFile = downloadConfig.readDateTimeFromFileName(result);
            log().debug("ImportPlannedTournamentsService :: Last download date from file: {}", dateTimeFromFile);
            return dateTimeFromFile.toString();
        }
        log().error("ImportPlannedTournamentsService :: Last download date not found");
        return "";
    }
}
