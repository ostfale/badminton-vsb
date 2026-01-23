package de.ostfale.va.application.port.in;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentsFilter;

import java.util.stream.Stream;

public interface ForFilteringPlannedTournaments {

    Stream<PlannedTournament> fetch(PlannedTournamentsFilter filter, int offset, int limit);

    int count(PlannedTournamentsFilter filter);
}
