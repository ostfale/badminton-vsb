package de.ostfale.va.framework.in.ui.plannedtournaments;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentKey;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentsFilter;
import de.ostfale.va.application.domain.model.plannedournaments.vo.UserIdendityVO;
import de.ostfale.va.application.port.in.ForManagingFavorites;
import de.ostfale.va.application.port.out.ForGettingUserConfiguration;
import de.ostfale.va.common.UseLogging;

import java.util.function.Function;

public class PlannedTournamentsListCompoment extends VerticalLayout implements UseLogging {

    private final Grid<PlannedTournament> grid;
    private final PaginationComponent paginationComponent;
    private final DataProvider<PlannedTournament, PlannedTournamentsFilter> dataProvider;
    private final ForManagingFavorites forManagingFavorites;
    private final ForGettingUserConfiguration userConfig;

    public PlannedTournamentsListCompoment(DataProvider<PlannedTournament, PlannedTournamentsFilter> dataProvider,
                                           PaginationComponent paginationComponent,
                                           ForManagingFavorites forManagingFavorites,
                                           ForGettingUserConfiguration userConfig) {
        this.grid = new Grid<>();
        this.paginationComponent = paginationComponent;
        this.dataProvider = dataProvider;
        this.forManagingFavorites = forManagingFavorites;
        this.userConfig = userConfig;

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

        DataProvider<PlannedTournament, PlannedTournamentsFilter> wrappedDataProvider =
                DataProvider.fromFilteringCallbacks(
                        query -> {
                            log().debug("Wrapped data provider fetching tournaments...");
                            @SuppressWarnings("unchecked")
                            var typedQuery = (Query<PlannedTournament, PlannedTournamentsFilter>) (Object) query;
                            var stream = this.dataProvider.fetch(typedQuery);
                            var currentUser = userConfig.getCurrentUser();
                            var identity = UserIdendityVO.fromEmail(currentUser.getEmail());
                            var favoriteKeys = forManagingFavorites.getFavorites(identity);
                            return stream.map(tournament -> forManagingFavorites.syncFavoriteState(tournament, favoriteKeys));
                        },
                        this.dataProvider::size
                );

        var configurableDataProvider = wrappedDataProvider.withConfigurableFilter();
        grid.setDataProvider(configurableDataProvider);
        grid.setPageSize(paginationComponent.getPageSize());
    }

    private void addColumns() {
        addFavoriteColumn(PlannedTournament::isFavorite);
        addTextColumn(PlannedTournament::startDate, "Datum", 0);
        addTextColumn(PlannedTournament::closingDate, "Meldeschluss", 0);
        addTextColumn(PlannedTournament::tournamentName, "Turniername", 1);
        addTextColumn(PlannedTournament::location, "Ort", 1);
        addTextColumn(PlannedTournament::tourCategory, "Kategorie", 0);
        addTextColumn(PlannedTournament::organizer, "Organisation", 0);

        addLinkColumn(PlannedTournament::webLinkUrl, VaadinIcon.LINK, "DBV Link");
        addLinkColumn(PlannedTournament::pdfLinkUrl, VaadinIcon.FILE_TEXT_O, "Ausschreibung");
    }

    private void addFavoriteColumn(ValueProvider<PlannedTournament, ?> valueProvider) {
        grid.addComponentColumn(item -> {
            boolean isFav = item.isFavorite();
            Icon icon = isFav ? VaadinIcon.STAR.create() : VaadinIcon.STAR_O.create();
            Button favoriteButton = new Button(icon);
            favoriteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
            icon.setColor(isFav ? "gold" : "gray");

            favoriteButton.addClickListener(e -> {
                log().debug("User clicked favorite button for tournament {}", item.tournamentName());
                toggleFavorite(item);
                // Force complete grid refresh to re-render component columns
                grid.getDataCommunicator().reset();
            });

            return favoriteButton;
        }).setHeader("Fav").setAutoWidth(true).setFlexGrow(0).setResizable(false);

    }

    private void addTextColumn(ValueProvider<PlannedTournament, ?> valueProvider, String header, int flexGrow) {
        grid.addColumn(valueProvider)
                .setHeader(header)
                .setAutoWidth(true)
                .setFlexGrow(flexGrow)
                .setResizable(true);
    }

    private void addLinkColumn(Function<PlannedTournament, String> urlProvider, VaadinIcon icon, String header) {
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

    private void toggleFavorite(PlannedTournament tournament) {
        var user = userConfig.getCurrentUser();
        if (user == null) {
            log().warn("Cannot toggle favorite - no user in context");
            return;
        }

        var identity = UserIdendityVO.fromEmail(user.getEmail());
        var key = tournament.createKey();

        forManagingFavorites.toggleFavorite(identity, key);
        log().debug("PlannedTournamentsListCompoment :: Toggled favorite for tournament {}", tournament.tournamentName());
    }
}
