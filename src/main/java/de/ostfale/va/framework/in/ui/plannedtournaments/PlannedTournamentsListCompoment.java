package de.ostfale.va.framework.in.ui.plannedtournaments;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentsFilter;
import de.ostfale.va.common.UseLogging;

public class PlannedTournamentsListCompoment extends VerticalLayout implements UseLogging {

    private final Grid<PlannedTournament> grid;
    private final PaginationComponent paginationComponent;
    private final DataProvider<PlannedTournament, PlannedTournamentsFilter> dataProvider;

    public PlannedTournamentsListCompoment(DataProvider<PlannedTournament, PlannedTournamentsFilter> dataProvider, PaginationComponent paginationComponent) {
        this.grid = new Grid<>();
        this.paginationComponent = paginationComponent;
        this.dataProvider = dataProvider;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        initializeUI();
        add(grid, paginationComponent);
        setFlexGrow(1, grid);

        this.paginationComponent.setPageChangedListener(() -> {
            grid.setPageSize(paginationComponent.getPageSize());
            grid.getDataProvider().refreshAll();
        });
    }

    public void refresh(PlannedTournamentsFilter filter) {
        @SuppressWarnings("unchecked")
        var dataProvider = (ConfigurableFilterDataProvider<PlannedTournament, Void, PlannedTournamentsFilter>) grid.getDataProvider();
        dataProvider.setFilter(filter);
        paginationComponent.reset();
    }

    public Grid<PlannedTournament> getGrid() {
        return grid;
    }

    private void initializeUI() {
        grid.addClassName("tournament-grid");
        grid.setHeightFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        addColumns();

        var dataProvider = this.dataProvider.withConfigurableFilter();
        grid.setDataProvider(dataProvider);
        grid.setPageSize(paginationComponent.getPageSize());
    }

    private void addColumns() {
        addTextColumn(PlannedTournament::startDate, "Datum", 0);
        addTextColumn(PlannedTournament::closingDate, "Meldeschluss", 0);
        addTextColumn(PlannedTournament::tournamentName, "Turniername", 1);
        addTextColumn(PlannedTournament::location, "Ort", 1);
        addTextColumn(PlannedTournament::tourCategory, "Kategorie", 0);
        addTextColumn(PlannedTournament::organizer, "Organisation", 0);

        addLinkColumn(PlannedTournament::webLinkUrl, VaadinIcon.LINK, "DBV Link");
        addLinkColumn(PlannedTournament::pdfLinkUrl, VaadinIcon.FILE_TEXT_O, "Ausschreibung");
    }

    private void addTextColumn(com.vaadin.flow.function.ValueProvider<PlannedTournament, ?> valueProvider, String header, int flexGrow) {
        grid.addColumn(valueProvider)
                .setHeader(header)
                .setAutoWidth(true)
                .setFlexGrow(flexGrow)
                .setResizable(true);
    }

    private void addLinkColumn(java.util.function.Function<PlannedTournament, String> urlProvider, VaadinIcon icon, String header) {
        grid.addColumn(new ComponentRenderer<>(tournament ->
                        createLinkComponent(urlProvider.apply(tournament), icon.create())))
                .setHeader(header)
                .setAutoWidth(true)
                .setFlexGrow(0);
    }

    private Component createLinkComponent(String url, Component icon) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layout.setWidthFull();

        if (url != null && !url.isEmpty()) {
            icon.addClassName("tournament-link-icon");
            Anchor link = new Anchor(url, icon);
            link.setTarget("_blank");
            layout.add(link);
        } else {
            layout.add(new Span("-"));
        }
        return layout;
    }
}
