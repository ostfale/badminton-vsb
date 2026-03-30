package de.ostfale.va.framework.in.ui.dashboard;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.UI;
import de.ostfale.va.application.domain.model.plannedournaments.TournamentsDashboardStatistics;
import de.ostfale.va.application.port.in.plannedtournaments.ForDownloadingPlannedTournamentsUC;
import de.ostfale.va.application.port.in.plannedtournaments.ForLoadingPlannedTournaments;

import java.util.concurrent.CompletableFuture;

public class TournamentsInfoCard extends BaseInfoCard {

    private static final int DOWNLOAD_POLL_INTERVAL_MS = 500;

    private static final String IMAGE_PATH = "images/info_card_tournaments.png";

    private static final String DOWNLOAD_TOOLTIP = "Turniere herunterladen";
    private static final String REFRESH_TOOLTIP = "Turniere aktualisieren";

    private static final String LABEL_LAST_DOWNLOAD = "Letzter Download";
    private static final String TOURNAMENTS_LABEL_PREFIX = "Turniere ";

    private final ForLoadingPlannedTournaments tournamentsService;
    private final ForDownloadingPlannedTournamentsUC downloadPlannedTournaments;
    private Button downloadButton;

    public TournamentsInfoCard(
            ForLoadingPlannedTournaments tournamentService,
            ForDownloadingPlannedTournamentsUC downloadPlannedTournaments
    ) {
        super(IMAGE_PATH, "Infos Turniere");
        this.tournamentsService = tournamentService;
        this.downloadPlannedTournaments = downloadPlannedTournaments;
        setupActions();
        refresh();
    }

    public void refresh() {
        log().info("TournamentsInfoCard :: Refreshing planned tournament statistics");
        clearContent();
        updateStatisticsContent();
    }

    private void setupActions() {
        downloadButton = createIconButton(VaadinIcon.DOWNLOAD, DOWNLOAD_TOOLTIP, this::handleDownload);
        Button updateButton = createIconButton(VaadinIcon.REFRESH, REFRESH_TOOLTIP, this::refresh);
        addAction(downloadButton);
        addAction(updateButton);
    }

    @Override
    protected String getTitle() {
        int currentYear = getCurrentCalendarYear();
        return "Statistik " + currentYear + "/" + (currentYear + 1);
    }

    private void handleDownload() {
        log().info("TournamentsInfoCard :: Downloading planned tournaments");

        UI ui = UI.getCurrent();
        if (!isDownloadAllowed(ui)) {
            return;
        }

        downloadButton.setEnabled(false);
        final int previousPollInterval = ui.getPollInterval();
        ui.setPollInterval(DOWNLOAD_POLL_INTERVAL_MS);

        CompletableFuture.runAsync(downloadPlannedTournaments::downloadPlannedTournaments)
                .whenComplete((unused, throwable) ->
                        ui.access(() -> handleDownloadResult(ui, previousPollInterval, throwable)));
    }

    private boolean isDownloadAllowed(UI ui) {
        return downloadButton != null && downloadButton.isEnabled() && ui != null;
    }

    private void handleDownloadResult(UI ui, int previousPollInterval, Throwable throwable) {
        if (!ui.isAttached()) {
            return;
        }

        if (throwable != null) {
            log().error("TournamentsInfoCard :: Download failed", throwable);
        }

        downloadButton.setEnabled(true);
        ui.setPollInterval(previousPollInterval);
        refresh();
    }

    private void updateStatisticsContent() {
        TournamentsDashboardStatistics statistics = loadStatistics();
        addStatisticsRows(statistics);
    }

    private void addStatisticsRows(TournamentsDashboardStatistics statistics) {
        int currentYear = getCurrentCalendarYear();

        addContent(createStatRow(LABEL_LAST_DOWNLOAD, statistics.lastDownloadTimestamp(), false));
        addTournamentRow(currentYear, statistics.getThisYearsStatistic());
        addTournamentRow(currentYear + 1, statistics.getNextYearsStatistic());
    }

    private void addTournamentRow(int year, String statistic) {
        addContent(createStatRow(TOURNAMENTS_LABEL_PREFIX + year, statistic, true));
    }

    private TournamentsDashboardStatistics loadStatistics() {
        return tournamentsService.calculateStatistics();
    }
}
