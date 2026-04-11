package de.ostfale.va.application.domain.model.playerrankings;

public record RankingSnapshot(
        int points,
        int ranking,
        int ageRanking,
        int tournaments
) {
}
