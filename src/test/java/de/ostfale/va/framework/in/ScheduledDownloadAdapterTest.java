package de.ostfale.va.framework.in;

import de.ostfale.va.application.port.in.plannedtournaments.ForDownloadingPlannedTournamentsUC;
import de.ostfale.va.application.port.in.ranking.ForDownloadingRankingsUC;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Scheduled Download Adapter Tests")
class ScheduledDownloadAdapterTest {

    @Test
    void shouldTriggerRankingDownloadUseCase() {
        ForDownloadingRankingsUC rankingUseCase = mock(ForDownloadingRankingsUC.class);
        ForDownloadingPlannedTournamentsUC tournamentUseCase = mock(ForDownloadingPlannedTournamentsUC.class);
        ScheduledDownloadAdapter adapter = new ScheduledDownloadAdapter(rankingUseCase, tournamentUseCase);

        adapter.downloadRankingFiles();

        verify(rankingUseCase, times(1)).downloadRankings();
    }

    @Test
    void shouldTriggerTournamentDownloadUseCase() {
        ForDownloadingRankingsUC rankingUseCase = mock(ForDownloadingRankingsUC.class);
        ForDownloadingPlannedTournamentsUC tournamentUseCase = mock(ForDownloadingPlannedTournamentsUC.class);
        ScheduledDownloadAdapter adapter = new ScheduledDownloadAdapter(rankingUseCase, tournamentUseCase);

        adapter.downloadTournamentFiles();

        verify(tournamentUseCase, times(1)).downloadPlannedTournaments();
    }

    @Test
    void shouldKeepNightlyDefaultCronSchedules() throws NoSuchMethodException {
        Method rankingMethod = ScheduledDownloadAdapter.class.getMethod("downloadRankingFiles");
        Method tournamentMethod = ScheduledDownloadAdapter.class.getMethod("downloadTournamentFiles");

        Scheduled rankingSchedule = rankingMethod.getAnnotation(Scheduled.class);
        Scheduled tournamentSchedule = tournamentMethod.getAnnotation(Scheduled.class);

        assertNotNull(rankingSchedule);
        assertNotNull(tournamentSchedule);
        assertEquals("${app.download.ranking.schedule:0 0 3 * * *}", rankingSchedule.cron());
        assertEquals("${app.download.tournaments.schedule:0 0 4 * * *}", tournamentSchedule.cron());
    }
}
