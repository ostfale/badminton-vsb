package de.ostfale.va.application.domain.model.playerrankings;

public record PlayerRankingsStatistics(
        String lastDownloadTimestamp,
        String lastOnlineTimestamp,
        int numberOfPlayer,
        int numberOfFemalePlayer,
        int numberOfMalePlayer
) {
}
