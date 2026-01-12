package de.ostfale.va.framework.in.ui.dashboard;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.common.UseTimeHandling;

public class PlannedTournamentsInfoCard extends Div implements UseLogging, UseTimeHandling {

    public PlannedTournamentsInfoCard() {
        log().debug("PlannedTournamentsInfoCard :: Created");
        setWidth("600px");
        setHeight("600px");
        initLayout();
    }

    private void initLayout() {
        Card tournamentImageCard = createCard();
        Image image = createImage();
        VerticalLayout contentLayout = new VerticalLayout();
        // contentLayout.add(createYearHeader(), prepareDownloadRow(), prepareCurrentYearRow(), prepareNextYearRow());

        VerticalLayout cardContent = createCardContent(image, contentLayout, createButtonLayout());
        tournamentImageCard.add(cardContent);
        add(tournamentImageCard);
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
        buttonLayout.getStyle().set("padding", "0 1rem 1rem 0");
        return buttonLayout;
    }

    private Button createIconButton(VaadinIcon icon, String tooltip, Runnable clickHandler) {
        Button button = new Button(icon.create());
        button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
        button.setTooltipText(tooltip);
        button.addClickListener(e -> clickHandler.run());
        return button;
    }

    private void handleDownload() {
        log().info("Download button clicked");
        // Implement download logic
    }

    private void handleUpdate() {
        log().info("Update button clicked");
        // Implement update logic
    }
}
