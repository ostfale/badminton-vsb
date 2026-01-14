package de.ostfale.va.application.domain.service;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentsStatistics;
import de.ostfale.va.application.port.in.ForCalculatingTournamentsStatisticsUC;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalculateTournamentsStatisticsService implements ForCalculatingTournamentsStatisticsUC, UseLogging {

    public PlannedTournamentsStatistics loadTournamentsStatistik(List<PlannedTournament> tournaments, String lastDownloadDate) {
        var totalTournamentsThisYear = calculateAllTournamentsThisYear(tournaments);
        var totalTournamentsNextYear = calculateAllTournamentsNextYear(tournaments);
        var openTournamentsThisYear = calculateAllOpenTournamentsThisYear(tournaments);

        return new PlannedTournamentsStatistics(lastDownloadDate,
                totalTournamentsThisYear,
                totalTournamentsNextYear,
                openTournamentsThisYear);
    }

    private long calculateAllTournamentsThisYear(List<PlannedTournament> tournaments) {
        var result = tournaments.stream()
                .filter(PlannedTournament::isFromCurrentYear)
                .count();
        log().debug("CalculateTournamentsStatistikService :: Calculating tournaments for this year: {}", result);
        return result;
    }

    private long  calculateAllOpenTournamentsThisYear(List<PlannedTournament> tournaments) {
        var result = tournaments.stream()
                .filter(PlannedTournament::isOpenTournament)
                .count();
        log().debug("CalculateTournamentsStatistikService :: Calculating open tournaments for this year: {}", result);
        return result;
    }

    private long calculateAllTournamentsNextYear(List<PlannedTournament> tournaments) {
        var result = tournaments.stream()
                .filter(PlannedTournament::isFromNextYear)
                .count();
        log().debug("CalculateTournamentsStatistikService :: Calculating tournaments for next year: {}", result);
        return result;
    }
}
