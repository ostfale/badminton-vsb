package de.ostfale.va.framework.out.web.scraper.match;

import de.ostfale.va.application.domain.model.matches.Tournament;
import de.ostfale.va.application.domain.model.playerrankings.PlayerTournamentId;
import de.ostfale.va.application.port.out.ForScrapingPlayerTournaments;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlayerMatchesScraperAdapter implements ForScrapingPlayerTournaments, UseLogging {
    @Override
    public List<Tournament> scrapeTournaments(PlayerTournamentId tournamentId) {
        return List.of();
    }
}
