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

    private static final String WEB_URL_PLAYER_RANKING_VIEW = "https://dbv.turnier.de/player-profile/";
    private static final String WEB_URL_PLAYER_RANKING_POINTS_VIEW = "https://dbv.turnier.de/ranking/player.aspx?id=50731&player=";

    private final ForLoadingExternalWebsites webLoader;

    public PlayerValidRankingPointsScraperAdapter(ForLoadingExternalWebsites webLoader) {
        this.webLoader = webLoader;
    }

    @Override
    public Optional<PlayerRankingRelevantTournaments> scrapeRelevantRankingPoints(Player player) {
        var urlRankingViewId = prepareUrlForPlayerRankingView(player);
        var rankingViewId = webLoader.loadPageAndProcess(urlRankingViewId, new PlayerViewIdPageProcessor());

        if (rankingViewId.isPresent()) {
            var urlRankingPoints = prepareUrlForPlayerRankingPointsView(rankingViewId.get());
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

    private String prepareUrlForPlayerRankingPointsView(String playerViewId) {
        var urlString = WEB_URL_PLAYER_RANKING_POINTS_VIEW + playerViewId;
        log().debug("PlayerValidRankingPointsScraperAdapter :: ranking points view for player: {} -> {}", playerViewId, urlString);
        return urlString;
    }

    public static class PlayerViewIdPageProcessor implements PageProcessor<String>, UseLogging {
        private static final String XPATH_PLAYER_LINK = "//*[@id=\"profile_content\"]/div/div/div/div[2]/div/div[1]/table/tbody/tr[1]/th/a";
        private static final String PLAYER_ID_PARAMETER = "player=";

        @Override
        public Optional<String> process(Page page) {
            String href = page.locator("xpath=" + XPATH_PLAYER_LINK).getAttribute("href");
            return extractPlayerId(href);
        }

        private Optional<String> extractPlayerId(String href) {
            if (href == null || !href.contains(PLAYER_ID_PARAMETER)) {
                return Optional.empty();
            }

            int playerIdStartIndex = href.indexOf(PLAYER_ID_PARAMETER) + PLAYER_ID_PARAMETER.length();
            String playerId = href.substring(playerIdStartIndex);
            log().info("PlayerViewIdPageProcessor :: extracted player id: {}", playerId);
            return Optional.of(playerId);
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
