package de.ostfale.va.application.port.in.plannedtournaments;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentsStatistics;

import java.util.List;

public interface ForCalculatingTournamentsStatisticsUC {

    PlannedTournamentsStatistics loadStatistic(List<PlannedTournament> tournaments, String lastDownloadDate);
}
