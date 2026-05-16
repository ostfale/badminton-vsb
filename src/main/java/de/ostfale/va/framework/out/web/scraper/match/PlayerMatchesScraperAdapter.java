package de.ostfale.va.framework.out.web.scraper.match;

import de.ostfale.va.application.domain.model.matches.PlayerTournaments;
import de.ostfale.va.application.domain.model.playerrankings.PlayerTournamentId;
import de.ostfale.va.application.port.out.ranking.ForLoadingExternalWebsites;
import de.ostfale.va.application.port.out.ranking.ForScrapingPlayerMatches;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PlayerMatchesScraperAdapter implements ForScrapingPlayerMatches, UseLogging {

    private final ForLoadingExternalWebsites webLoader;

    public PlayerMatchesScraperAdapter(ForLoadingExternalWebsites webLoader) {
        this.webLoader = webLoader;
    }

    @Override
    public Optional<PlayerTournaments> scrapePlayerMatches(PlayerTournamentId playerId) {

        String url = prepareUrlForPlayerMatches(playerId);
        ScrapePlayerMatches scraper = new ScrapePlayerMatches();
        Optional<PlayerTournaments> playerTournaments = webLoader.loadPageAndProcess(url, scraper);
        log().info("PlayerMatchesScraperAdapter :: player matches for playerId: {} -> found: {}", playerId, playerTournaments.isPresent());
        return playerTournaments;
    }

    private String prepareUrlForPlayerMatches(PlayerTournamentId playerTournamentId) {
        return "https://dbv.turnier.de/player-profile/" + playerTournamentId.tournamentId() + "/tournaments/2026";
    }
}
