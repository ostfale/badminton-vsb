package de.ostfale.va.framework.in.ui.dashboard;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentsDashboardStatistics;
import de.ostfale.va.application.port.in.plannedtournaments.ForLoadingPlannedTournaments;

public class PlannedTournamentsInfoCard extends BaseInfoCard {

    private static final String IMAGE_PATH = "images/info_card_tournaments.png";

    private static final String DOWNLOAD_TOOLTIP = "Turniere herunterladen";
    private static final String REFRESH_TOOLTIP = "Turniere aktualisieren";

    private static final String LABEL_LAST_DOWNLOAD = "Letzter Download";
    private static final String TOURNAMENTS_LABEL_PREFIX = "Turniere ";

    private final ForLoadingPlannedTournaments tournamentsService;

    public PlannedTournamentsInfoCard(ForLoadingPlannedTournaments tournamentService) {
        super(IMAGE_PATH, "Infos Turniere");
        this.tournamentsService = tournamentService;
        setupActions();
        refresh();
    }


    public void refresh() {
        updateStatisticsContent();
    }

    private void setupActions() {
        Button downloadButton = createIconButton(VaadinIcon.DOWNLOAD, DOWNLOAD_TOOLTIP, this::handleDownload);
        Button updateButton = createIconButton(VaadinIcon.REFRESH, REFRESH_TOOLTIP, this::updateStatisticsContent);
        addAction(downloadButton);
        addAction(updateButton);
    }

    @Override
    protected String getTitle() {
        int currentYear = getCurrentCalendarYear();
        return "Statistik " + currentYear + "/" + (currentYear + 1);
    }

    private void handleDownload() {
    }

    private void updateStatisticsContent() {
        PlannedTournamentsDashboardStatistics statistics = loadStatistics();
        addStatisticsRows(statistics);
    }

    private void addStatisticsRows(PlannedTournamentsDashboardStatistics statistics) {
        int currentYear = getCurrentCalendarYear();

        addContent(createStatRow(LABEL_LAST_DOWNLOAD, statistics.lastDownloadTimestamp(), false));
        addTournamentRow(currentYear, statistics.getThisYearsStatistic());
        addTournamentRow(currentYear + 1, statistics.getNextYearsStatistic());
    }

    private void addTournamentRow(int year, String statistic) {
        addContent(createStatRow(TOURNAMENTS_LABEL_PREFIX + year, statistic, true));
    }

    private PlannedTournamentsDashboardStatistics loadStatistics() {
        return tournamentsService.calculateStatistics();
    }
}
