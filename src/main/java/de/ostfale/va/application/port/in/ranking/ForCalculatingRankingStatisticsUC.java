package de.ostfale.va.application.port.in.ranking;

import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.PlayerRankingsStatistics;

import java.util.List;

public interface ForCalculatingRankingStatisticsUC {

    PlayerRankingsStatistics loadStatistics(List<Player> players, String lastDownloadDate, String lastOnlineDate);
}
