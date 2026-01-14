package de.ostfale.va.application.domain.service;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.port.in.ForImportingPlannedTournaments;
import de.ostfale.va.application.port.in.ForProvidingPlannedTournamentStreams;
import de.ostfale.va.application.port.out.ForParsingPlannedTournaments;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImportPlannedTournamentsService implements ForImportingPlannedTournaments, UseLogging {

    private final ForParsingPlannedTournaments parser;
    private final ForProvidingPlannedTournamentStreams streamsProvider;

    public ImportPlannedTournamentsService(
            ForParsingPlannedTournaments parser,
            ForProvidingPlannedTournamentStreams streamsProvider) {
        this.parser = parser;
        this.streamsProvider = streamsProvider;
    }

    @Override
    public List<PlannedTournament> importFromSource() {
        return streamsProvider.getPlannedTournamentStreams().stream()
                .flatMap(stream -> parser.parsePlannedTournaments(stream).stream())
                .toList();
    }
}
