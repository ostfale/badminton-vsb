package de.ostfale.va.framework.in.ui.dashboard;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.UI;
import de.ostfale.va.application.domain.model.playerrankings.RankingDashboardStatistics;
import de.ostfale.va.application.port.in.ranking.ForDownloadingRankingsUC;
import de.ostfale.va.application.port.in.ranking.ForLoadingRankings;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class RankingsInfoCard extends BaseInfoCard {

    private static final String IMAGE_PATH = "images/info_card_ranking.png";
    private static final String CARD_TITLE = "Infos Rangliste";

    private static final String DOWNLOAD_TOOLTIP = "Rangliste herunterladen";
    private static final String REFRESH_TOOLTIP = "Rangliste aktualisieren (kann ein paar Sekunden dauern)";
    private static final String DOWNLOAD_RUNNING_MESSAGE = "Download läuft...";
    private static final String DOWNLOAD_NOT_NECESSARY_MESSAGE = "Kein Download erforderlich. Die Rangliste ist bereits aktuell.";
    private static final String DOWNLOAD_FINISHED_MESSAGE = "Download abgeschlossen.";
    private static final String DOWNLOAD_FAILED_MESSAGE = "Download fehlgeschlagen. Bitte später erneut versuchen.";
    private static final int STATUS_AUTO_CLOSE_SECONDS = 3;

    private static final String LABEL_LAST_DOWNLOAD = "Letzter Download";
    private static final String LABEL_PLAYER_COUNT = "Anzahl der Spieler";
    private static final String LABEL_FEMALE_PLAYER_COUNT = "Anzahl weibliche Spieler";
    private static final String LABEL_MALE_PLAYER_COUNT = "Anzahl männliche Spieler";

    private final ForLoadingRankings rankingsService;
    private final ForDownloadingRankingsUC downloadRankings;
    private final Popover downloadSkippedPopover;
    private final Span downloadStatusText;
    private Button downloadButton;

    public RankingsInfoCard(ForLoadingRankings rankingsService, ForDownloadingRankingsUC downloadRankings) {
        super(IMAGE_PATH, CARD_TITLE);
        this.rankingsService = rankingsService;
        this.downloadRankings = downloadRankings;
        this.downloadStatusText = new Span();
        this.downloadSkippedPopover = createDownloadSkippedPopover();
        setupActions();
        refresh();
    }

    @Override
    protected String getTitle() {
        return "Rangliste KW " + getCurrentCalendarWeek();
    }

    private void refresh() {
        log().info("RankingsInfoCard :: Refreshing ranking statistics");
        clearContent();
        addStatisticsRows(loadStatistics());
    }

    private void download() {
        log().info("RankingsInfoCard :: Downloading ranking statistics");
        if (downloadButton == null || !downloadButton.isEnabled()) {
            return;
        }

        UI ui = UI.getCurrent();
        if (ui == null) {
            return;
        }

        downloadButton.setEnabled(false);
        showStatus(DOWNLOAD_RUNNING_MESSAGE);
        final int previousPollInterval = ui.getPollInterval();
        ui.setPollInterval(500);

        CompletableFuture.supplyAsync(downloadRankings::downloadRankings)
                .whenComplete((downloadPerformed, throwable) ->
                        ui.access(() -> {
                            if (!ui.isAttached()) {
                                return;
                            }
                            if (throwable != null) {
                                log().error("RankingsInfoCard :: Download failed", throwable);
                                showStatus(DOWNLOAD_FAILED_MESSAGE);
                            } else if (Boolean.TRUE.equals(downloadPerformed)) {
                                showStatus(DOWNLOAD_FINISHED_MESSAGE);
                            } else {
                                showStatus(DOWNLOAD_NOT_NECESSARY_MESSAGE);
                            }
                            schedulePopoverClose(ui);
                            downloadButton.setEnabled(true);
                            ui.setPollInterval(previousPollInterval);
                            refresh();
                        }));
    }

    private void setupActions() {
        downloadButton = createIconButton(VaadinIcon.DOWNLOAD, DOWNLOAD_TOOLTIP, this::download);
        downloadSkippedPopover.setTarget(downloadButton);
        Button updateButton = createIconButton(VaadinIcon.REFRESH, REFRESH_TOOLTIP, this::refresh);
        addAction(downloadButton);
        addAction(updateButton);
    }

    private Popover createDownloadSkippedPopover() {
        Popover popover = new Popover(downloadStatusText);
        popover.setOpenOnClick(false);
        popover.setPosition(PopoverPosition.BOTTOM_START);
        return popover;
    }

    private void showStatus(String message) {
        downloadStatusText.setText(message);
        downloadSkippedPopover.close();
        downloadSkippedPopover.open();
    }

    private void schedulePopoverClose(UI ui) {
        CompletableFuture.delayedExecutor(STATUS_AUTO_CLOSE_SECONDS, TimeUnit.SECONDS).execute(
                () -> ui.access(() -> {
                    if (ui.isAttached()) {
                        downloadSkippedPopover.close();
                    }
                })
        );
    }

    private RankingDashboardStatistics loadStatistics() {
        return rankingsService.calculateStatistics();
    }

    private void addStatisticsRows(RankingDashboardStatistics statistics) {
        addContent(createStatRow(LABEL_LAST_DOWNLOAD, statistics.lastDownloadTimestamp(), false));
        addContent(createStatRow(LABEL_PLAYER_COUNT, String.valueOf(statistics.numberOfPlayer()), true));
        addContent(createStatRow(LABEL_FEMALE_PLAYER_COUNT, String.valueOf(statistics.numberOfFemalePlayer()), true));
        addContent(createStatRow(LABEL_MALE_PLAYER_COUNT, String.valueOf(statistics.numberOfMalePlayer()), true));
    }
}
