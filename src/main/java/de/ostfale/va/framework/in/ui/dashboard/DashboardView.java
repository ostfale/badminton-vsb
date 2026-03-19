package de.ostfale.va.framework.in.ui.dashboard;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.ostfale.va.application.port.in.plannedtournaments.ForCalculatingTournamentsStatisticsUC;
import de.ostfale.va.application.port.in.plannedtournaments.ForLoadingPlannedTournaments;
import de.ostfale.va.application.port.out.ForDownloadingFiles;
import de.ostfale.va.application.port.out.plannedtournaments.ForPlannedTournamentsDownloadConfig;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.in.ui.app.MainLayout;

@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout implements UseLogging {
    public static final String PATH = "";

    public DashboardView(
            ForCalculatingTournamentsStatisticsUC calcService,
            ForLoadingPlannedTournaments importService,
            ForDownloadingFiles downloadService,
            ForPlannedTournamentsDownloadConfig plannedTournamentsDownloadConfig) {
        log().info("DashboardView :: constructor");
        setSizeFull();
        getStyle().set("padding-top", "calc(var(--lumo-space-m) * 2)");
        getStyle().set("padding-left", "calc(var(--lumo-space-m) * 2)");

        var plannedTournamentsInfoCard = new PlannedTournamentsInfoCard(calcService, importService, downloadService, plannedTournamentsDownloadConfig);
        var rankingsInfoCard = new RankingsInfoCard();

        HorizontalLayout cardsLayout = new HorizontalLayout(plannedTournamentsInfoCard, rankingsInfoCard);
        cardsLayout.getStyle().set("gap", "calc(var(--lumo-space-m) * 3)");
        cardsLayout.setAlignItems(Alignment.START);
        cardsLayout.setWidthFull();
        cardsLayout.getStyle().set("flex-wrap", "nowrap");

        add(cardsLayout);
    }
}
