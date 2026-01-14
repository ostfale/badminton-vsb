package de.ostfale.va.application.port.in;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentsStatistics;

import java.util.List;

public interface ForCalculatingTournamentsStatisticsUC {

    PlannedTournamentsStatistics loadTournamentsStatistik(List<PlannedTournament> tournaments, String lastDownloadDate);
}
