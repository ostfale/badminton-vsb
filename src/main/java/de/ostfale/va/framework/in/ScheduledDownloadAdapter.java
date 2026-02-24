package de.ostfale.va.framework.in;

import de.ostfale.va.application.port.in.plannedtournaments.ForDownloadingPlannedTournamentsUC;
import de.ostfale.va.application.port.in.ranking.ForDownloadingRankingsUC;
import de.ostfale.va.common.UseLogging;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledDownloadAdapter implements  UseLogging {

    private final ForDownloadingRankingsUC rankingUseCase;
    private final ForDownloadingPlannedTournamentsUC tournamentUseCase;

    public ScheduledDownloadAdapter(
            ForDownloadingRankingsUC rankingUseCase,
            ForDownloadingPlannedTournamentsUC tournamentUseCase) {
        this.rankingUseCase = rankingUseCase;
        this.tournamentUseCase = tournamentUseCase;
    }

    @Scheduled(cron = "${app.download.ranking.schedule:0 0 3 * * *}")
    @Async
    public void downloadRankingFiles() {
        log().info("Starting scheduled ranking file download...");
        rankingUseCase.downloadRankings();
    }

    @Scheduled(cron = "${app.download.tournaments.schedule:0 0 4 * * *}")
    @Async
    public void downloadTournamentFiles() {
        log().info("Starting scheduled tournament file download...");
        tournamentUseCase.downloadPlannedTournaments();
    }
}
