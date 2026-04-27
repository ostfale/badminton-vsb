package de.ostfale.va.framework.in;

import de.ostfale.va.application.port.in.ranking.ForLoadingRankings;
import de.ostfale.va.common.UseLogging;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupAdapter implements UseLogging {

    private final ForLoadingRankings forLoadingRankings;

    public StartupAdapter(ForLoadingRankings forLoadingRankings) {
        this.forLoadingRankings = forLoadingRankings;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() {
        log().info("StartupAdapter :: Loading players at startup");
        forLoadingRankings.getAllPlayers();
    }
}
