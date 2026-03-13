package de.ostfale.va.framework.out.web.scraper;

import de.ostfale.va.application.domain.model.playerrankings.PlayerId;
import de.ostfale.va.application.domain.model.playerrankings.PlayerTournamentId;
import de.ostfale.va.application.port.out.ranking.ForScrapingPlayerTournamentId;
import de.ostfale.va.application.port.out.ranking.ForLoadingExternalWebsites;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PlayerTournamentIdWebScraperAdapter implements ForScrapingPlayerTournamentId, UseLogging {

    private final ForLoadingExternalWebsites webLoader;

    public PlayerTournamentIdWebScraperAdapter(ForLoadingExternalWebsites webLoader) {
        this.webLoader = webLoader;
    }

    @Override
    public Optional<PlayerTournamentId> scrapePlayerTournamentId(PlayerId playerId) {
        String url = "https://dbv.turnier.de/find/player?q=" + playerId.playerId();

        // Wir erstellen den Scraper-Prozess hier lokal oder injizieren ihn
        ScrapePlayerTournamentId scraper = new ScrapePlayerTournamentId();
        scraper.setTargetPlayerId(playerId.playerId());

        return webLoader.loadPageAndProcess(url, scraper);
    }
}
