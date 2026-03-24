package de.ostfale.va.application.domain.model.playerrankings;

public record RankingDashboardStatistics(
        String lastDownloadTimestamp,
        String lastOnlineTimestamp,
        long numberOfPlayer,
        long numberOfFemalePlayer,
        long numberOfMalePlayer
) {
}
