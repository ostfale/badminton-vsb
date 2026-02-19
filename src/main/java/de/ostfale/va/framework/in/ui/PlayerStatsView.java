package de.ostfale.va.framework.in.ui;

import com.vaadin.flow.component.masterdetaillayout.MasterDetailLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.ostfale.va.application.port.in.ranking.ForLoadingRankings;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.in.ui.app.MainLayout;
import de.ostfale.va.framework.in.ui.playerranking.PlayerDetailsView;

@Route(value = PlayerStatsView.PATH, layout = MainLayout.class)
public class PlayerStatsView extends VerticalLayout implements UseLogging {

    public static final String PATH = "player-stats-view";

    private final ForLoadingRankings rankingService;

    public PlayerStatsView(ForLoadingRankings rankingService) {
        log().debug("PlayerRankingView :: constructor");
        this.rankingService = rankingService;
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        initLayout();
    }

    private void initLayout() {
        log().debug("PlayerRankingView :: initLayout");
        PlayerDetailsView playerDetailsView = new PlayerDetailsView(rankingService);
        add(masterDetailLayout(playerDetailsView));
    }

    private MasterDetailLayout masterDetailLayout(PlayerDetailsView playerDetailsView) {
        log().debug("PlayerRankingView :: masterDetailLayout");

        MasterDetailLayout masterDetailLayout = new MasterDetailLayout();
        masterDetailLayout.setMaster(playerDetailsView);
        masterDetailLayout.setOverlayMode(MasterDetailLayout.OverlayMode.DRAWER);
        masterDetailLayout.setSizeFull();
        return masterDetailLayout;
    }
}
