package de.ostfale.va.application.port.out.ranking;

import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.PlayerRankingRelevantTournaments;

import java.util.Optional;

public interface ForScrapingRelevantRankingPoints {

    Optional<PlayerRankingRelevantTournaments> scrapeRelevantRankingPoints(Player player);
}
