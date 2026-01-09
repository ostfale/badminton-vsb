package de.ostfale.va.framework.in.ui.playerranking;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.in.ui.app.MainLayout;

@Route(value = PlayerRankingView.PATH, layout = MainLayout.class)
public class PlayerRankingView extends VerticalLayout implements UseLogging {

    public static final String PATH = "player-ranking-view";

}
