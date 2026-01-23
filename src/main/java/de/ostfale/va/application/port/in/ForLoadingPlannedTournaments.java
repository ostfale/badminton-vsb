package de.ostfale.va.application.port.in;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;

import java.util.List;

public interface ForLoadingPlannedTournaments {

    List<PlannedTournament> loadFromSource();

    String getLastDownloadDate();
}
