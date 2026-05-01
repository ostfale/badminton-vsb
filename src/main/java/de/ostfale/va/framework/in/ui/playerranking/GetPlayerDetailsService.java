package de.ostfale.va.framework.in.ui.playerranking;

import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.PlayerRankingRelevantTournaments;
import de.ostfale.va.application.port.out.ranking.ForScrapingPlayerTournamentId;
import de.ostfale.va.application.port.out.ranking.ForScrapingRelevantRankingPoints;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetPlayerDetailsService implements UseLogging {

    private final ForScrapingPlayerTournamentId playerTournamentIdScraper;
    private final ForScrapingRelevantRankingPoints relevantRankingPointsScraper;

    public GetPlayerDetailsService(
            ForScrapingPlayerTournamentId playerTournamentIdScraper,
            ForScrapingRelevantRankingPoints relevantRankingPointsScraper
    ) {
        this.playerTournamentIdScraper = playerTournamentIdScraper;
        this.relevantRankingPointsScraper = relevantRankingPointsScraper;
    }

    public void addPlayerTournamentIdToPlayer(Player player) {
        log().debug("GetPlayerDetailsService :: read player tournamentId for player: {}", player);
        if (player.getPlayerTournamentId() != null) {
            return;
        }
        playerTournamentIdScraper.scrapePlayerTournamentId(player.getPlayerId()).ifPresent(tournamentId -> {
            log().info("GetPlayerDetailsService :: readValidRankingPoints found tournamentId {}", tournamentId);
            player.setPlayerTournamentId(tournamentId);
            // Wir scrapen hier nicht mehr direkt die Turniere, das passiert lazy in getRelevantRankingPoints
        });
    }

    public Optional<PlayerRankingRelevantTournaments> getRelevantRankingPoints(Player player) {
        // ALWAYS scrape fresh data from turnier.de to ensure up-to-date tournament information.
        // We do not cache this on the in-memory Player object anymore.
        var relevantTournaments = relevantRankingPointsScraper.scrapeRelevantRankingPoints(player);
        log().debug("GetPlayerDetailsService :: live scrape getRelevantRankingPoints: found: {} ", relevantTournaments.isPresent());
        return relevantTournaments;
    }
}
