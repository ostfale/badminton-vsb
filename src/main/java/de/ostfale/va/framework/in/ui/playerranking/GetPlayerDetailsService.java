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
        playerTournamentIdScraper.scrapePlayerTournamentId(player.getPlayerId()).ifPresent(tournamentId -> {
            log().info("GetPlayerDetailsService :: readValidRankingPoints found tournamentId {}", tournamentId);
            player.setPlayerTournamentId(tournamentId);

            relevantRankingPointsScraper.scrapeRelevantRankingPoints(player);
        });
    }

    public Optional<PlayerRankingRelevantTournaments> getRelevantRankingPoints(Player player) {

        if (player.getRelevantTournaments() != null) {
            log().debug("GetPlayerDetailsService :: getRelevantRankingPoints:found: {} ", true);
            return Optional.of(player.getRelevantTournaments());
        }

        var relevantTournaments = relevantRankingPointsScraper.scrapeRelevantRankingPoints(player);
        player.setRelevantTournaments(relevantTournaments.orElse(null));
        log().debug("GetPlayerDetailsService :: load getRelevantRankingPoints: found: {} ", relevantTournaments.isPresent());
        return relevantTournaments;
    }
}
