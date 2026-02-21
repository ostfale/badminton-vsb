package de.ostfale.va.framework.in.ui.dashboard;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.ostfale.va.application.port.in.ForCalculatingTournamentsStatisticsUC;
import de.ostfale.va.application.port.in.ForDownloadingFromWeb;
import de.ostfale.va.application.port.in.ForLoadingPlannedTournaments;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.in.ui.app.MainLayout;

@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout implements UseLogging {
    public static final String PATH = "";

    public DashboardView(
            ForCalculatingTournamentsStatisticsUC calcService,
            ForLoadingPlannedTournaments importService,
            ForDownloadingFromWeb downloadService) {
        log().info("OverviewView :: constructor");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new PlannedTournamentsInfoCard(calcService, importService, downloadService));
    }
}
