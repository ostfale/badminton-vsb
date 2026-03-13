package de.ostfale.va.application.domain.model.playerrankings;

import de.ostfale.va.common.UseLogging;

import java.util.List;

public record PlayerRankingRelevantTournaments(
        List<PlayerRankingTournamentPoints> singleTournaments,
        List<PlayerRankingTournamentPoints> doubleTournaments,
        List<PlayerRankingTournamentPoints> mixedTournaments

) implements UseLogging {

    public List<PlayerRankingTournamentPoints> getRelevantSingleTournaments() {
        List<PlayerRankingTournamentPoints> relevantTournaments = singleTournaments.stream().filter(PlayerRankingTournamentPoints::isRelevant).toList();
        log().debug("PlayerRankingRelevantTournaments :: getRelevantSingleTournaments: {}", relevantTournaments);
        return relevantTournaments;
    }

    public List<PlayerRankingTournamentPoints> getRelevantDoubleTournaments() {
        List<PlayerRankingTournamentPoints> relevantTournaments = doubleTournaments.stream().filter(PlayerRankingTournamentPoints::isRelevant).toList();
        log().debug("PlayerRankingRelevantTournaments :: getRelevantDoubleTournaments: {}", relevantTournaments);
        return relevantTournaments;
    }

    public List<PlayerRankingTournamentPoints> getRelevantMixedTournaments() {
        List<PlayerRankingTournamentPoints> relevantTournaments = mixedTournaments.stream().filter(PlayerRankingTournamentPoints::isRelevant).toList();
        log().debug("PlayerRankingRelevantTournaments :: getRelevantMixedTournaments: {}", relevantTournaments);
        return relevantTournaments;
    }
}
