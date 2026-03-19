package de.ostfale.va.application.domain.service.ranking;

import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.PlayerRankingsStatistics;
import de.ostfale.va.application.port.in.ranking.ForCalculatingRankingStatisticsUC;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalculateRankingStatisticService implements ForCalculatingRankingStatisticsUC, UseFileSystemHandling, UseLogging {

    @Override
    public PlayerRankingsStatistics loadStatistics(List<Player> players, String lastDownloadDate, String lastOnlineDate) {
        return null;
    }
}
