package de.ostfale.va.framework.in.ui.playerinfo;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.Route;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.in.ui.app.MainLayout;
import de.ostfale.va.framework.in.ui.playerinfo.matches.PlayerMatchesTabComponent;

@Route(value = PlayerInfoView.PATH, layout = MainLayout.class)
public class PlayerInfoView extends VerticalLayout implements UseLogging {

    public static final String PATH = "player-info-view";

    public PlayerInfoView(PlayerInfoSearchComponent playerInfoSearchComponent,
                          PlayerDataTabComponent playerDataTabComponent,
                          PlayerRankingTabComponent playerRankingTabComponent,
                          PlayerHistoryTabComponent playerHistoryTabComponent,
                          PlayerMatchesTabComponent playerMatchesTabComponent) {
        log().debug("PlayerInfoView :: constructor");
        // components

        TabSheet tabSheet = new TabSheet();
        tabSheet.add("Stammdaten", playerDataTabComponent);
        tabSheet.add("Ranglisten Statistik", playerRankingTabComponent);
        tabSheet.add("Leistungsentwicklung", playerHistoryTabComponent);
        tabSheet.add("Matches", playerMatchesTabComponent);
        tabSheet.setWidthFull();
        tabSheet.setHeightFull();

        setSizeFull();
        add(playerInfoSearchComponent.getComponent(), tabSheet);
        setFlexGrow(1, tabSheet);
    }

}
