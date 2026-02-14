package de.ostfale.va.application.port.out.plannedtournaments;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;

import java.io.InputStream;
import java.util.List;

public interface ForParsingPlannedTournaments {

    List<PlannedTournament> parsePlannedTournaments(InputStream inputStream);
}
