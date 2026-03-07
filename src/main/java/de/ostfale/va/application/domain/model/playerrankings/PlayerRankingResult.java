package de.ostfale.va.application.domain.model.playerrankings;

public record PlayerRankingResult(
        String tournamentName,
        String disciplineName,
        String weekName,
        String placement,
        int points,
        boolean isRelevant
) {
}
