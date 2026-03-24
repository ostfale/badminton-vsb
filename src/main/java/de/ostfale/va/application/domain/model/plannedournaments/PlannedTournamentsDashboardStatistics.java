package de.ostfale.va.application.domain.model.plannedournaments;

public record PlannedTournamentsDashboardStatistics(
        String lastDownloadTimestamp,
        long totalTournamentsThisYear,
        long totalTournamentsNextYear,
        long openTournamentsThisYear
) {

    public String getThisYearsStatistic() {
        return String.format("Total: %d, Open: %d", totalTournamentsThisYear, openTournamentsThisYear);
    }

    public String getNextYearsStatistic() {
        return String.format("Total: %d", totalTournamentsNextYear);
    }
}
