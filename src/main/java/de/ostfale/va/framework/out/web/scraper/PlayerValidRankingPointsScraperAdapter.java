package de.ostfale.va.framework.out.web.scraper;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.PlayerRankingRelevantTournaments;
import de.ostfale.va.application.domain.model.playerrankings.PlayerRankingTournamentPoints;
import de.ostfale.va.application.port.out.ranking.ForLoadingExternalWebsites;
import de.ostfale.va.application.port.out.ranking.ForScrapingRelevantRankingPoints;
import de.ostfale.va.application.port.out.ranking.PageProcessor;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class PlayerValidRankingPointsScraperAdapter implements ForScrapingRelevantRankingPoints, UseLogging {

    private static final String WEB_URL_BASE = "https://dbv.turnier.de";
    private static final String WEB_URL_PLAYER_RANKING_VIEW = WEB_URL_BASE + "/player-profile/";

    private final ForLoadingExternalWebsites webLoader;

    public PlayerValidRankingPointsScraperAdapter(ForLoadingExternalWebsites webLoader) {
        this.webLoader = webLoader;
    }

    @Override
    public Optional<PlayerRankingRelevantTournaments> scrapeRelevantRankingPoints(Player player) {
        var urlRankingViewId = prepareUrlForPlayerRankingView(player);
        
        // 1. Scrape the link to the CURRENT ranking points page from the player's profile
        var rankingPointsUrlPath = webLoader.loadPageAndProcess(urlRankingViewId, new PlayerViewIdPageProcessor());

        if (rankingPointsUrlPath.isPresent()) {
            // 2. Build the full URL using the extracted path (which includes the correct list ID and player ID)
            var urlRankingPoints = WEB_URL_BASE + rankingPointsUrlPath.get();
            log().debug("PlayerValidRankingPointsScraperAdapter :: loading points from: {}", urlRankingPoints);
            
            // 3. Scrape the points
            var rankingPoints = webLoader.loadPageAndProcess(urlRankingPoints, new PlayerRankingPointsPageProcessor());
            log().debug("PlayerValidRankingPointsScraperAdapter :: ranking points for player: {} -> found: {}", player, rankingPoints.isPresent());
            return rankingPoints;
        }

        return Optional.empty();
    }

    private String prepareUrlForPlayerRankingView(Player player) {
        var urlString = WEB_URL_PLAYER_RANKING_VIEW + player.getPlayerTournamentId().tournamentId() + "/ranking";
        log().debug("PlayerValidRankingPointsScraperAdapter :: ranking view for player: {} -> {}", player, urlString);
        return urlString;
    }

    public static class PlayerViewIdPageProcessor implements PageProcessor<String>, UseLogging {
        private static final String XPATH_PLAYER_LINK = "//*[@id=\"profile_content\"]/div/div/div/div[2]/div/div[1]/table/tbody/tr[1]/th/a";

        @Override
        public Optional<String> process(Page page) {
            String href = page.locator("xpath=" + XPATH_PLAYER_LINK).getAttribute("href");
            if (href == null || href.isBlank()) {
                return Optional.empty();
            }
            
            // We return the entire href (which looks like "/ranking/player.aspx?id=51829&player=12345")
            // This ensures we get the CURRENT ranking list ID, not a hardcoded old one.
            log().info("PlayerViewIdPageProcessor :: extracted ranking points link: {}", href);
            return Optional.of(href.startsWith("/") ? href : "/" + href);
        }
    }

    public static class PlayerRankingPointsPageProcessor implements PageProcessor<PlayerRankingRelevantTournaments>, UseLogging {
        private static final String XPATH_PLAYER_TABLES = "//*[@id=\"content\"]";
        private static final int SINGLES_TABLE_INDEX = 0;
        private static final int DOUBLES_TABLE_INDEX = 1;
        private static final int MIXED_TABLE_INDEX = 2;
        private static final int MINIMUM_VALID_CELLS = 5;
        private static final int FIRST_DATA_TABLE_INDEX = 1;

        @Override
        public Optional<PlayerRankingRelevantTournaments> process(Page page) {
            List<PlayerRankingTournamentPoints> singlePoints = new ArrayList<>();
            List<PlayerRankingTournamentPoints> doublePoints = new ArrayList<>();
            List<PlayerRankingTournamentPoints> mixedPoints = new ArrayList<>();
            Map<Integer, List<PlayerRankingTournamentPoints>> pointMap = Map.of(
                    SINGLES_TABLE_INDEX, singlePoints,
                    DOUBLES_TABLE_INDEX, doublePoints,
                    MIXED_TABLE_INDEX, mixedPoints
            );

            Locator tables = page.locator("table.ruler");
            int tableCount = tables.count();

            for (int i = FIRST_DATA_TABLE_INDEX; i < tableCount; i++) {
                Locator rows = tables.nth(i).locator("tbody tr");
                processTournamentRows(rows, pointMap.get(i - 1));
            }

            return Optional.of(new PlayerRankingRelevantTournaments(singlePoints, doublePoints, mixedPoints));
        }

        private void processTournamentRows(Locator rows, List<PlayerRankingTournamentPoints> targetList) {
            for (int j = 0; j < rows.count(); j++) {
                Locator row = rows.nth(j);
                if (isSummaryRow(row)) continue;

                List<String> cells = row.locator("td").allInnerTexts();
                if (cells.size() >= MINIMUM_VALID_CELLS) {
                    targetList.add(createTournamentPoints(row, cells));
                }
            }
        }

        private boolean isSummaryRow(Locator row) {
            return row.locator("td.noruler").count() > 0;
        }

        private PlayerRankingTournamentPoints createTournamentPoints(Locator row, List<String> cells) {
            boolean isRelevant = row.locator("img[src*='icon_new.gif']").count() > 0;
            return new PlayerRankingTournamentPoints(
                    cells.get(0).trim(),
                    cells.get(1).trim(),
                    cells.get(2).trim(),
                    cells.get(3).trim(),
                    parsePoints(cells.get(4)),
                    isRelevant
            );
        }

        private int parsePoints(String pointString) {
            if (pointString == null || pointString.isEmpty()) return 0;
            return Integer.parseInt(pointString.replaceAll("[^0-9]", ""));
        }
    }
}
