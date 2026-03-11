package de.ostfale.va.application.port.out.ranking;

import de.ostfale.va.application.domain.model.playerrankings.PlayerId;
import de.ostfale.va.application.domain.model.playerrankings.PlayerTournamentId;

import java.util.Optional;

public interface ForScrapingPlayerTournamentId {

    Optional<PlayerTournamentId> scrapePlayerTournamentId(PlayerId playerId);
}
