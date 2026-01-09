package de.ostfale.va.framework.in.ui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.in.ui.app.MainLayout;

@Route(value = PlayerStats.PATH, layout = MainLayout.class)
public class PlayerStats extends VerticalLayout implements UseLogging {

    public static final String PATH = "player-stats-view";

}
