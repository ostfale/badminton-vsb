package de.ostfale.va.application.domain.model.plannedournaments;

public record PlannedTournamentsStatistics(
        String lastDownloadTimestamp,
        long totalTournamentsThisYear,
        long totalTournamentsNextYear,
        long openTournamentsThisYear
) {
}
