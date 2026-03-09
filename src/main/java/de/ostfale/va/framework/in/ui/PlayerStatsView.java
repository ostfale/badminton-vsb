package de.ostfale.va.framework.in.ui;

import com.vaadin.flow.component.masterdetaillayout.MasterDetailLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.ostfale.va.application.port.in.ranking.ForLoadingRankings;
import de.ostfale.va.application.port.out.ForGettingUserConfiguration;
import de.ostfale.va.application.port.out.ForStoringUserData;
import de.ostfale.va.application.port.out.ranking.ForLoadingExternalWebsites;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.in.ui.app.MainLayout;
import de.ostfale.va.framework.in.ui.playerranking.PlayerDetailsView;

@Route(value = PlayerStatsView.PATH, layout = MainLayout.class)
public class PlayerStatsView extends VerticalLayout implements UseLogging {

    public static final String PATH = "player-stats-view";


    public PlayerStatsView(ForLoadingRankings rankingService,
                           ForStoringUserData forStoringUserData,
                           ForGettingUserConfiguration userConfiguration,
                           ForLoadingExternalWebsites loadingExternalWebsites) {
        log().debug("PlayerRankingView :: constructor");
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        initLayout(rankingService, forStoringUserData, userConfiguration, loadingExternalWebsites);
    }

    private void initLayout(ForLoadingRankings rankingService, ForStoringUserData forStoringUserData, ForGettingUserConfiguration userConfiguration, ForLoadingExternalWebsites loadingExternalWebsites) {
        log().debug("PlayerRankingView :: initLayout");
        PlayerDetailsView playerDetailsView = new PlayerDetailsView(rankingService, forStoringUserData, userConfiguration, loadingExternalWebsites);
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
