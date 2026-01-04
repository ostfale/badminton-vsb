package de.ostfale.va.application.domain.service;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.port.in.ForImportingPlannedTournamentsUC;
import de.ostfale.va.application.port.out.ForParsingPlannedTournaments;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
public class ImportPlannedTournamentsService implements ForImportingPlannedTournamentsUC, UseLogging {

    private final ForParsingPlannedTournaments parser;

    public ImportPlannedTournamentsService(ForParsingPlannedTournaments parser) {
        this.parser = parser;
    }

    @Override
    public List<PlannedTournament> importFromSource(InputStream... streams) {
        return Arrays.stream(streams)
                .flatMap(stream -> parser.parsePlannedTournaments(stream).stream())
                .toList();
    }
}
