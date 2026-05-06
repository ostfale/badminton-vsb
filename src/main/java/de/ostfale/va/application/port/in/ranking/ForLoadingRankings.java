package de.ostfale.va.application.port.in.ranking;

import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.RankingDashboardStatistics;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;

import java.util.List;

public interface ForLoadingRankings extends UseFileSystemHandling, UseLogging {

    List<Player> getAllPlayers();

    List<Player> findPlayers(String filert, int offset, int limit);

    RankingDashboardStatistics calculateStatistics();

    int countPlayers(String filter);

    /**
     * Explicitly triggers parsing the downloaded ranking file and storing it in the database.
     */
    void importRankingsFromFile();

}
