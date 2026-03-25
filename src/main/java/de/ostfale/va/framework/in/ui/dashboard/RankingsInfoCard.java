package de.ostfale.va.framework.in.ui.dashboard;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import de.ostfale.va.application.domain.model.playerrankings.RankingDashboardStatistics;
import de.ostfale.va.application.port.in.ranking.ForLoadingRankings;

public class RankingsInfoCard extends BaseInfoCard {

    private static final String IMAGE_PATH = "images/info_card_ranking.png";
    private static final String CARD_TITLE = "Infos Rangliste";

    private static final String DOWNLOAD_TOOLTIP = "Rangliste herunterladen";
    private static final String REFRESH_TOOLTIP = "Rangliste aktualisieren";

    private static final String LABEL_LAST_DOWNLOAD = "Letzter Download";
    private static final String LABEL_PLAYER_COUNT = "Anzahl der Spieler";
    private static final String LABEL_FEMALE_PLAYER_COUNT = "Anzahl weibliche Spieler";
    private static final String LABEL_MALE_PLAYER_COUNT = "Anzahl männliche Spieler";

    private final ForLoadingRankings rankingsService;

    public RankingsInfoCard(ForLoadingRankings rankingsService) {
        super(IMAGE_PATH, CARD_TITLE);
        this.rankingsService = rankingsService;
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
    }
    
    private void setupActions() {
        Button downloadButton = createIconButton(VaadinIcon.DOWNLOAD, DOWNLOAD_TOOLTIP, this::download);
        Button updateButton = createIconButton(VaadinIcon.REFRESH, REFRESH_TOOLTIP, this::refresh);
        addAction(downloadButton);
        addAction(updateButton);
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
