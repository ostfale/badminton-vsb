package de.ostfale.va.framework.in.ui.dashboard;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentsStatistics;
import de.ostfale.va.application.port.in.plannedtournaments.ForCalculatingTournamentsStatisticsUC;
import de.ostfale.va.application.port.out.ForDownloadingFiles;
import de.ostfale.va.application.port.in.plannedtournaments.ForLoadingPlannedTournaments;
import de.ostfale.va.application.port.out.plannedtournaments.ForPlannedTournamentsDownloadConfig;

public class PlannedTournamentsInfoCard extends BaseInfoCard  {

    private static final String IMAGE_PATH = "images/info_card_tournaments.png";

    private final ForCalculatingTournamentsStatisticsUC calcService;
    private final ForLoadingPlannedTournaments importService;
    private final ForDownloadingFiles downloadService;
    private final ForPlannedTournamentsDownloadConfig plannedTournamentsDownloadConfig;
    private final VerticalLayout statsContainer = new VerticalLayout();

    public PlannedTournamentsInfoCard(
            ForCalculatingTournamentsStatisticsUC statCalcService,
            ForLoadingPlannedTournaments importService,
            ForDownloadingFiles downloadService,
            ForPlannedTournamentsDownloadConfig plannedTournamentsDownloadConfig) {
        super(IMAGE_PATH, "Infos Turniere");

        this.plannedTournamentsDownloadConfig = plannedTournamentsDownloadConfig;
        log().debug("PlannedTournamentsInfoCard :: Created");
        this.importService = importService;
        this.calcService = statCalcService;
        this.downloadService = downloadService;

        initStatsContainer();
        setupActions();
        refreshStatistics();
    }


    @Override
    protected String getTitle() {
        int currentYear = getCurrentCalendarYear();
        return "Statistik " + currentYear + "/" + (currentYear + 1);
    }

    private void initStatsContainer() {
        statsContainer.setPadding(false);
        statsContainer.setSpacing(false);
        statsContainer.getStyle().set("padding", "0 1rem");
    }

    private void setupActions() {
        Button downloadButton = createIconButton(VaadinIcon.DOWNLOAD, "Geplante Turniere herunterladen", this::handleDownload);
        Button updateButton = createIconButton(VaadinIcon.REFRESH, "Geplante Turniere aktualisieren", this::handleUpdate);

        addAction(downloadButton);
        addAction(updateButton);
    }

    private void updateStatisticsRows(PlannedTournamentsStatistics stats) {
        int currentYear = getCurrentCalendarYear();

        addContent(createStatRow("Letzter Download", stats.lastDownloadTimestamp(), false));
        addContent(createStatRow("Turniere " + currentYear, stats.getThisYearsStatistic(), true));
        addContent(createStatRow("Turniere " + (currentYear + 1), stats.getNextYearsStatistic(), true));
    }


    private void refreshStatistics() {
        var tournamentsList = importService.loadFromSource();
        var lastDownloadDateFromFile = importService.getLastDownloadDate();
        var statResult = calcService.loadStatistic(tournamentsList, lastDownloadDateFromFile);
        updateStatisticsRows(statResult);
    }

    private void handleDownload() {
        log().info("Download button clicked");
        var downloadTasks = plannedTournamentsDownloadConfig.getDownloadTasks();
        downloadService.downloadFiles(downloadTasks);
        refreshStatistics();
    }

    private void handleUpdate() {
        log().info("Update button clicked");
        refreshStatistics();
    }
}
