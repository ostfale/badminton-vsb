package de.ostfale.va.application.domain.model.playerrankings;

public record RankingDashboardStatistics(
        String lastDownloadTimestamp,
        long numberOfPlayer,
        long numberOfFemalePlayer,
        long numberOfMalePlayer
) {
}
