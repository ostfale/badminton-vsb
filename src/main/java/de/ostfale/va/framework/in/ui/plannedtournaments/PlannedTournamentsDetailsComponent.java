package de.ostfale.va.framework.in.ui.plannedtournaments;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentAgeClassDisciplines;
import de.ostfale.va.application.port.out.ForRoutingAndGeocoding;
import de.ostfale.va.common.UseLogging;

public class PlannedTournamentsDetailsComponent extends VerticalLayout implements UseLogging {

    private static final String TOURNAMENT_NAME_LABEL = "Name";
    private static final String TOURNAMENT_LOCATION_LABEL = "Ort";
    private static final String TOURNAMENT_DATE_LABEL = "Datum";
    private static final String TOURNAMENT_CLOSING_DATE_LABEL = "Meldeschluss";
    private static final String TOURNAMENT_ORGANIZATION_LABEL = "Veranstalter";
    private static final String TOURNAMENT_COUNTRYN_LABEL = "Land";
    private static final String TOURNAMENT_CATEGORY_LABEL = "Kategorie";
    private static final String ROW_HEIGHT = "30px";

    private final Span tournamentNameValue;
    private final Span tournamentLocationValue;
    private final Span tournamentCountryValue;
    private final Span tournamentDateValue;
    private final Span tournamentClosingDateValue;
    private final Span tournamentOrganizationValue;
    private final Span tournamentCategoryValue;
    private final Div disciplinesContainer;
    private final Span routeDistanceValue;
    private final Span routeDurationValue;
    private final ForRoutingAndGeocoding routingService;

    public PlannedTournamentsDetailsComponent(ForRoutingAndGeocoding routingService) {
        this.routingService = routingService;

        // Initialize route fields
        routeDistanceValue = new Span();
        routeDurationValue = new Span();

        tournamentNameValue = new Span();
        tournamentLocationValue = new Span();
        tournamentCountryValue = new Span();
        tournamentDateValue = new Span();
        tournamentClosingDateValue = new Span();
        tournamentOrganizationValue = new Span();
        tournamentCategoryValue = new Span();
        disciplinesContainer = new Div();

        // Row 1: Tournament Name (full width)
        Div nameRow = createDataRow(TOURNAMENT_NAME_LABEL, tournamentNameValue);
        nameRow.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("font-weight", "bold");

        add(nameRow,
                createTwoColumnRow(TOURNAMENT_COUNTRYN_LABEL, tournamentCountryValue, TOURNAMENT_LOCATION_LABEL, tournamentLocationValue),
                createTwoColumnRow(TOURNAMENT_DATE_LABEL, tournamentDateValue, TOURNAMENT_CLOSING_DATE_LABEL, tournamentClosingDateValue),
                createTwoColumnRow(TOURNAMENT_ORGANIZATION_LABEL, tournamentOrganizationValue, TOURNAMENT_CATEGORY_LABEL, tournamentCategoryValue),
                createDisciplinesHeader(),
                disciplinesContainer,
                createRouteHeader(),
                createTwoColumnRow("Entfernung", routeDistanceValue, "Fahrzeit", routeDurationValue)
        );
        setPadding(true);
        setSpacing(true);
        setSizeFull();
        getStyle().set("overflow-y", "auto");
    }

    public void setTournament(PlannedTournament tournament) {
        if (tournament != null) {
            tournamentNameValue.setText(tournament.tournamentName());
            tournamentLocationValue.setText(tournament.location());
            tournamentCountryValue.setText(tournament.countryCode());
            tournamentDateValue.setText(tournament.startDate().toString());
            tournamentClosingDateValue.setText(tournament.closingDate());
            tournamentOrganizationValue.setText(tournament.organizer());
            tournamentCategoryValue.setText(tournament.tourCategory().getBaseCategory());
            updateDisciplines(tournament);
            updateRoute(tournament);
        } else {
            clearFields();
        }
    }

    public void addCloseListener(ComponentEventListener<CloseEvent> listener) {
        addListener(CloseEvent.class, listener);
    }

    private Div createRouteHeader() {
        Div headerContainer = new Div();
        headerContainer.getStyle().set("margin-top", "var(--lumo-space-m)");

        Span header = new Span("Route von Hamburg");
        header.getStyle()
                .set("font-size", "var(--lumo-font-size-m)")
                .set("font-weight", "bold")
                .set("color", "var(--lumo-primary-text-color)");

        headerContainer.add(header);
        return headerContainer;
    }

    private void updateRoute(PlannedTournament tournament) {
        routeDistanceValue.setText("Berechne...");
        routeDurationValue.setText("Berechne...");

        routingService.calculateRouteFromHamburg(tournament).ifPresentOrElse(
                route -> {
                    routeDistanceValue.setText(route.getFormattedDistance());
                    routeDurationValue.setText(route.getFormattedDuration());
                },
                () -> {
                    routeDistanceValue.setText("Nicht verfügbar");
                    routeDurationValue.setText("Nicht verfügbar");
                }
        );
    }

    private HorizontalLayout createTwoColumnRow(String label1, Span value1, String label2, Span value2) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.getStyle().set("gap", "var(--lumo-space-l)");

        Div field1 = createDataRow(label1, value1);
        field1.setWidth("50%");
        Div field2 = createDataRow(label2, value2);
        field2.setWidth("50%");

        row.add(field1, field2);
        return row;
    }

    private void updateDisciplines(PlannedTournament tournament) {
        disciplinesContainer.removeAll();
        if (tournament.ageClassDisciplines() == null || tournament.ageClassDisciplines().isEmpty()) {
            return;
        }

        HorizontalLayout matrixLayout = new HorizontalLayout();
        matrixLayout.setSpacing(true);
        matrixLayout.setPadding(false);

        // First Column: Labels (Empty top, then Single, Double, Mixed)
        matrixLayout.add(createLabelColumn());

        // Dynamic Columns: One for each AgeClass
        tournament.ageClassDisciplines().forEach(ad -> {
            matrixLayout.add(createAgeClassColumn(ad));
        });

        disciplinesContainer.add(matrixLayout);
    }

    private void clearFields() {
        tournamentNameValue.setText("");
        tournamentLocationValue.setText("");
        tournamentCountryValue.setText("");
        tournamentDateValue.setText("");
        tournamentClosingDateValue.setText("");
        tournamentOrganizationValue.setText("");
        tournamentCategoryValue.setText("");
        disciplinesContainer.removeAll();
        routeDistanceValue.setText("");
        routeDurationValue.setText("");
    }

    private Div createDataRow(String label, Span valueSpan) {
        Div container = new Div();
        container.getStyle().set("margin-bottom", "var(--lumo-space-s)");

        Span labelSpan = new Span(label + ":");
        labelSpan.getStyle()
                .set("font-weight", "500")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("display", "block")
                .set("font-size", "var(--lumo-font-size-s)");

        valueSpan.getStyle()
                .set("display", "block")
                .set("margin-top", "var(--lumo-space-xs)");

        container.add(labelSpan, valueSpan);
        return container;
    }

    private VerticalLayout createLabelColumn() {
        VerticalLayout col = new VerticalLayout();
        col.setPadding(false);
        col.setSpacing(false);
        col.setWidth("80px");

        // Header Spacer (matches the AgeClass header height)
        Span spacer = new Span("");
        spacer.setHeight(ROW_HEIGHT);

        col.add(spacer);
        col.add(createLabelSpan("Einzel"));
        col.add(createLabelSpan("Doppel"));
        col.add(createLabelSpan("Mixed"));

        return col;
    }

    private Span createLabelSpan(String text) {
        Span span = new Span(text);
        span.setHeight(ROW_HEIGHT);
        span.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "flex-end")
                .set("font-size", "var(--lumo-font-size-s)")
                .set("font-weight", "bold") // Added this line
                .set("color", "var(--lumo-secondary-text-color)")
                .set("padding-right", "var(--lumo-space-s)");
        return span;
    }

    private Div createDisciplinesHeader() {
        Div headerContainer = new Div();
        headerContainer.getStyle().set("margin-top", "var(--lumo-space-m)");

        Span header = new Span("Disziplinen");
        header.getStyle()
                .set("font-size", "var(--lumo-font-size-m)")
                .set("font-weight", "bold")
                .set("color", "var(--lumo-primary-text-color)");

        headerContainer.add(header);
        return headerContainer;
    }

    private Div createStatusCell(boolean played) {
        Div cell = new Div();
        cell.setHeight(ROW_HEIGHT);
        cell.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center");

        Icon icon = VaadinIcon.CIRCLE.create();
        icon.setSize("16px");
        icon.setColor(played ? "var(--lumo-success-color)" : "var(--lumo-error-color)");

        cell.add(icon);
        return cell;
    }

    private VerticalLayout createAgeClassColumn(PlannedTournamentAgeClassDisciplines ad) {
        VerticalLayout col = new VerticalLayout();
        col.setPadding(false);
        col.setSpacing(false);
        col.setAlignItems(Alignment.CENTER);
        col.setWidth("50px");

        // Header: AgeClass Name
        Span ageHeader = new Span(ad.ageClass().name());
        ageHeader.setHeight(ROW_HEIGHT);
        ageHeader.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("font-weight", "bold")
                .set("font-size", "var(--lumo-font-size-s)");

        col.add(ageHeader);
        col.add(createStatusCell(ad.isSingle()));
        col.add(createStatusCell(ad.isDouble()));
        col.add(createStatusCell(ad.isMixed()));

        return col;
    }

    public static class CloseEvent extends ComponentEvent<PlannedTournamentsDetailsComponent> {
        public CloseEvent(PlannedTournamentsDetailsComponent source, boolean fromClient) {
            super(source, fromClient);
        }
    }
}
