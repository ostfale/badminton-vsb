package de.ostfale.va.application.domain.service.ranking;

import com.microsoft.playwright.Page;
import de.ostfale.va.application.domain.model.playerrankings.PlayerRankingResult;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankingPointsTableScraper implements UseLogging {

    public List<PlayerRankingResult> parseRankingTable(Page page) {
        return List.of();
    }
}
