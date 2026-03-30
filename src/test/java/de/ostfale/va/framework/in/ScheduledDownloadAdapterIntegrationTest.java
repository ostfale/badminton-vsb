package de.ostfale.va.framework.in;

import de.ostfale.va.application.port.in.plannedtournaments.ForDownloadingPlannedTournamentsUC;
import de.ostfale.va.application.port.in.ranking.ForDownloadingRankingsUC;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        classes = ScheduledDownloadAdapterIntegrationTest.TestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@TestPropertySource(properties = {
        "app.download.ranking.schedule=*/1 * * * * *",
        "app.download.tournaments.schedule=*/1 * * * * *"
})
@DisplayName("Scheduled Download Adapter Integration Tests")
class ScheduledDownloadAdapterIntegrationTest {

    @Autowired
    private RankingProbe rankingProbe;

    @Autowired
    private TournamentProbe tournamentProbe;

    @Test
    void shouldTriggerBothScheduledDownloadsAutomatically() throws InterruptedException {
        waitUntilAtLeastOneInvocation(rankingProbe::count);
        waitUntilAtLeastOneInvocation(tournamentProbe::count);

        assertTrue(rankingProbe.count() >= 1);
        assertTrue(tournamentProbe.count() >= 1);
    }

    private void waitUntilAtLeastOneInvocation(CountSupplier counter) throws InterruptedException {
        Instant deadline = Instant.now().plus(Duration.ofSeconds(10));
        while (Instant.now().isBefore(deadline)) {
            if (counter.count() >= 1) {
                return;
            }
            Thread.sleep(100);
        }
    }

    @FunctionalInterface
    private interface CountSupplier {
        int count();
    }

    @EnableScheduling
    @EnableAsync(proxyTargetClass = true)
    @Import(ScheduledDownloadAdapter.class)
    static class TestConfig {

        @Bean
        RankingProbe rankingProbe() {
            return new RankingProbe();
        }

        @Bean
        TournamentProbe tournamentProbe() {
            return new TournamentProbe();
        }

        @Bean
        ForDownloadingRankingsUC rankingUseCase(RankingProbe probe) {
            return () -> {
                probe.increment();
                return true;
            };
        }

        @Bean
        ForDownloadingPlannedTournamentsUC tournamentUseCase(TournamentProbe probe) {
            return probe::increment;
        }
    }

    static class RankingProbe {
        private final AtomicInteger counter = new AtomicInteger();

        void increment() {
            counter.incrementAndGet();
        }

        int count() {
            return counter.get();
        }
    }

    static class TournamentProbe {
        private final AtomicInteger counter = new AtomicInteger();

        void increment() {
            counter.incrementAndGet();
        }

        int count() {
            return counter.get();
        }
    }
}
