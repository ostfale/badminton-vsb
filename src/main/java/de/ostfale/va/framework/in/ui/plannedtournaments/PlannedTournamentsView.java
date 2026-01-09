package de.ostfale.va.framework.in.ui.plannedtournaments;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.in.ui.app.MainLayout;

@Route(value = PlannedTournamentsView.PATH, layout = MainLayout.class)
public class PlannedTournamentsView extends VerticalLayout implements UseLogging {

    public static final String PATH = "planned-tournaments-view";

}
