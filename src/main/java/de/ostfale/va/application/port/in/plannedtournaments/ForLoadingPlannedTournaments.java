package de.ostfale.va.application.port.in.plannedtournaments;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;

import java.util.List;

public interface ForLoadingPlannedTournaments {

    List<PlannedTournament> loadFromSource();

    String getLastDownloadDate();
}
