package de.ostfale.va.framework.in.ui;

import com.vaadin.flow.component.masterdetaillayout.MasterDetailLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.ostfale.va.framework.in.ui.playerranking.GetPlayerDetailsService;
import de.ostfale.va.application.port.in.ranking.ForLoadingRankings;
import de.ostfale.va.application.port.out.ForGettingUserConfiguration;
import de.ostfale.va.application.port.out.ForStoringUserData;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.in.ui.app.MainLayout;
import de.ostfale.va.framework.in.ui.playerranking.PlayerDetailsView;

@Route(value = PlayerStatsView.PATH, layout = MainLayout.class)
public class PlayerStatsView extends VerticalLayout implements UseLogging {

    public static final String PATH = "player-stats-view";


    public PlayerStatsView(ForLoadingRankings rankingService,
                           ForStoringUserData forStoringUserData,
                           ForGettingUserConfiguration userConfiguration,
                           GetPlayerDetailsService playerDetailsService) {
        log().debug("PlayerRankingView :: constructor");
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        initLayout(playerDetailsService, rankingService, forStoringUserData, userConfiguration);
    }

    private void initLayout(GetPlayerDetailsService playerDetailsService, ForLoadingRankings rankingService,
                            ForStoringUserData forStoringUserData,
                            ForGettingUserConfiguration userConfiguration) {
        log().debug("PlayerRankingView :: initLayout");
        PlayerDetailsView playerDetailsView = new PlayerDetailsView(playerDetailsService, rankingService, forStoringUserData, userConfiguration);
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
