package de.ostfale.va.framework.in.ui.dashboard;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentsStatistics;
import de.ostfale.va.application.port.in.ForCalculatingTournamentsStatisticsUC;
import de.ostfale.va.application.port.in.ForDownloadingPlannedTournaments;
import de.ostfale.va.application.port.in.ForImportingPlannedTournaments;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.common.UseTimeHandling;

public class PlannedTournamentsInfoCard extends Div implements UseLogging, UseTimeHandling {

    private final ForCalculatingTournamentsStatisticsUC calcService;
    private final ForImportingPlannedTournaments importService;
    private final ForDownloadingPlannedTournaments downloadService;
    private final VerticalLayout statsContainer = new VerticalLayout();

    public PlannedTournamentsInfoCard(
            ForCalculatingTournamentsStatisticsUC statCalcService,
            ForImportingPlannedTournaments importService,
            ForDownloadingPlannedTournaments downloadService) {
        log().debug("PlannedTournamentsInfoCard :: Created");
        this.importService = importService;
        this.calcService = statCalcService;
        this.downloadService = downloadService;
        setWidth("600px");
        setHeight("600px");
        initStatsContainer();
        initLayout();
        refreshStatistics();
    }

    private void initStatsContainer() {
        statsContainer.setPadding(false);
        statsContainer.setSpacing(false);
        statsContainer.getStyle().set("padding", "0 1rem");
    }

    private void initLayout() {
        Card tournamentImageCard = createCard();
        Image image = createImage();
        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.add(createYearHeader());
        contentLayout.add(statsContainer);

        VerticalLayout cardContent = createCardContent(image, contentLayout, createButtonLayout());
        tournamentImageCard.add(cardContent);
        add(tournamentImageCard);
    }

    private void updateStatisticsRows(PlannedTournamentsStatistics stats) {
        statsContainer.removeAll();
        int currentYear = getCurrentCalendarYear();

        statsContainer.add(createStatRow("Letzter Download", stats.lastDownloadTimestamp(), false));
        statsContainer.add(createStatRow("Turniere " + currentYear, stats.getThisYearsStatistic(), true));
        statsContainer.add(createStatRow("Turniere " + (currentYear + 1), stats.getNextYearsStatistic(), true));
    }

    private HorizontalLayout createStatRow(String labelText, String valueText, boolean indented) {
        com.vaadin.flow.component.html.Span label = new Span(labelText);
        com.vaadin.flow.component.html.Span value = new Span(valueText);
        value.getStyle().set("font-weight", "normal");

        HorizontalLayout row = new HorizontalLayout(label, value);
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        row.getStyle().set("margin-bottom", "0.75rem");

        if (indented) {
            // Apply a "tab" (indentation) to the left
            row.getStyle().set("padding-left", "2rem");
        }
        return row;
    }

    private HorizontalLayout createYearHeader() {
        int currentYear = getCurrentCalendarYear();
        H2 sectionTitle = new H2("Statistik " + currentYear + "/" + (currentYear + 1));
        sectionTitle.getStyle().set("margin", "0");

        HorizontalLayout yearHeader = new HorizontalLayout(sectionTitle);
        yearHeader.setWidthFull();
        yearHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        yearHeader.setAlignItems(FlexComponent.Alignment.BASELINE);
        yearHeader.getStyle().set("padding", "0 1rem");
        return yearHeader;
    }

    private VerticalLayout createCardContent(Image image, VerticalLayout content, HorizontalLayout buttons) {
        VerticalLayout layout = new VerticalLayout(image, content, buttons);
        layout.setFlexGrow(1, content);
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setSizeFull();
        return layout;
    }

    private Card createCard() {
        var card = new Card();
        card.addClassName("download-info-card");
        return card;
    }

    private Image createImage() {
        var tournamentImage = new Image("images/info_card_tournaments.png", "Tournaments");
        tournamentImage.setWidth("100%");
        return tournamentImage;
    }

    private HorizontalLayout createButtonLayout() {
        Button downloadButton = createIconButton(VaadinIcon.DOWNLOAD, "Geplante Turniere herunterladen", this::handleDownload);
        Button updateButton = createIconButton(VaadinIcon.REFRESH, "Geplante Turniere aktualisieren", this::handleUpdate);

        HorizontalLayout buttonLayout = new HorizontalLayout(downloadButton, updateButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        // Adjust padding to align with the text (1rem on the left)
        buttonLayout.getStyle().set("padding", "0 1rem 1rem 1rem");
        return buttonLayout;
    }

    private Button createIconButton(VaadinIcon icon, String tooltip, Runnable clickHandler) {
        Button button = new Button(icon.create());
        button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
        button.setTooltipText(tooltip);
        button.addClickListener(e -> clickHandler.run());
        return button;
    }

    private void refreshStatistics() {
        var tournamentsList = importService.importFromSource();
        var lastDownloadDateFromFile = importService.getLastDownloadDate();
        var statResult = calcService.loadTournamentsStatistik(tournamentsList, lastDownloadDateFromFile);
        updateStatisticsRows(statResult);
    }

    private void handleDownload() {
        log().info("Download button clicked");
        downloadService.performDownload();
        refreshStatistics();
    }

    private void handleUpdate() {
        log().info("Update button clicked");
        refreshStatistics();
    }
}
