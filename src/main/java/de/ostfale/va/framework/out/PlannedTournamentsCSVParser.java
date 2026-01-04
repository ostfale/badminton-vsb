package de.ostfale.va.framework.out;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.port.out.ForParsingPlannedTournaments;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class PlannedTournamentsCSVParser implements ForParsingPlannedTournaments, UseLogging {

    @Override
    public List<PlannedTournament> parsePlannedTournaments(InputStream inputStream) {
        return List.of();
    }
}
