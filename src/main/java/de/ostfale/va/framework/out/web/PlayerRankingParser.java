package de.ostfale.va.framework.out.web;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import de.ostfale.va.application.domain.model.playerrankings.PlayerRankingResult;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PlayerRankingParser implements UseLogging {

    public List<PlayerRankingResult> parseValidRankingPoints(Page page) {
        log().debug("PlayerRankingParser :: start parsing page content for valid ranking points");
        List<PlayerRankingResult> results = new ArrayList<>();
        Locator rows = page.locator("table.ruler tbody tr");
        int rowCount = rows.count();

        for (int i = 0; i < rowCount; i++) {
            Locator row = rows.nth(i);

            // skip noruler classes
            if (row.locator("td.noruler").count() > 0) {
                continue;
            }

            String tournamentName = row.locator("td").nth(0).innerText().trim();
            String disciplineName = row.locator("td").nth(1).innerText().trim();
            String weekName = row.locator("td").nth(2).innerText().trim();
            String placement = row.locator("td").nth(3).innerText().trim();

            String pointsRaw = row.locator("td").nth(4).innerText().replaceAll("[^0-9]", "");
            int points = pointsRaw.isEmpty() ? 0 : Integer.parseInt(pointsRaw);

            // image exists *
            boolean istRelevant = row.locator("td").nth(6).locator("img").count() > 0;
            results.add(new PlayerRankingResult(tournamentName, disciplineName, weekName, placement, points, istRelevant));
        }

        return results;
    }
}
