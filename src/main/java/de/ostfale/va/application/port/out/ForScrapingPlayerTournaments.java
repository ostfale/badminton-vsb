package de.ostfale.va.application.port.out;

import de.ostfale.va.application.domain.model.matches.Tournament;
import de.ostfale.va.application.domain.model.playerrankings.PlayerTournamentId;

import java.util.List;

public interface ForScrapingPlayerTournaments {

    List<Tournament> scrapeTournaments(PlayerTournamentId tournamentId);
}
