package de.ostfale.va.application.port.out.ranking;

import de.ostfale.va.application.domain.model.matches.PlayerTournaments;
import de.ostfale.va.application.domain.model.playerrankings.PlayerTournamentId;

import java.util.Optional;

public interface ForScrapingPlayerMatches {

    Optional<PlayerTournaments> scrapePlayerMatches(PlayerTournamentId playerId);
}
