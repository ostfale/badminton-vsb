package de.ostfale.va.application.port.in.plannedtournaments;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.domain.model.plannedournaments.TournamentsDashboardStatistics;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.common.UseTimeHandling;

import java.util.List;

public interface ForLoadingPlannedTournaments extends UseFileSystemHandling, UseTimeHandling, UseLogging {

    List<PlannedTournament> getAllPlannedTournaments();

    List<PlannedTournament> loadFromSource();

    TournamentsDashboardStatistics calculateStatistics();
}
