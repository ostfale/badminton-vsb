package de.ostfale.va.framework.in.ui.plannedtournaments;

import com.vaadin.flow.component.masterdetaillayout.MasterDetailLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.Route;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentsFilter;
import de.ostfale.va.application.port.in.ForFilteringPlannedTournaments;
import de.ostfale.va.application.port.out.ForCalculatingTournamentRoutes;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.in.ui.app.MainLayout;
import org.springframework.context.event.EventListener;

import java.util.stream.Stream;

@Route(value = PlannedTournamentsView.PATH, layout = MainLayout.class)
public class PlannedTournamentsView extends VerticalLayout implements UseLogging {

    public static final String PATH = "planned-tournaments-view";
    private final ForFilteringPlannedTournaments forFilteringPlannedTournaments;
    private final ForCalculatingTournamentRoutes routingService;
    private final DataProvider<PlannedTournament, PlannedTournamentsFilter> pagingDataProvider;
    private PaginationComponent paginationComponent = new PaginationComponent();


    public PlannedTournamentsView(ForFilteringPlannedTournaments filter,ForCalculatingTournamentRoutes routingService) {
        this.forFilteringPlannedTournaments = filter;
        this.routingService = routingService;
        this.pagingDataProvider = DataProvider.fromFilteringCallbacks(this::fetchTournaments, this::countTournaments);

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        initLayout();
    }

    private Stream<PlannedTournament> fetchTournaments(Query<PlannedTournament, PlannedTournamentsFilter> query) {
        // Use the offset and limit provided by the Grid's query (via PaginationComponent)
        var offset = paginationComponent.calculateOffset();
        var limit = paginationComponent.getPageSize();
        var filter = query.getFilter().orElse(null);

        log().debug("TournamentView :: pagingDataProvider :: limit: {}, offset: {}", limit, offset);
        // Pass query parameters directly to the backend
        return forFilteringPlannedTournaments.fetch(filter, offset, limit);
    }

    private int countTournaments(Query<PlannedTournament, PlannedTournamentsFilter> query) {
        var filter = query.getFilter().orElse(null);
        // Get TOTAL count for the pagination component to update UI buttons
        int totalItems = forFilteringPlannedTournaments.count(filter);
        paginationComponent.setTotalItemCount(totalItems);

        // Return the count of items for the CURRENT PAGE only to the Grid
        // This tricks the Grid into displaying only the current page's worth of data
        var offset = paginationComponent.calculateOffset();
        var limit = paginationComponent.getPageSize();
        return Math.max(0, Math.min(limit, totalItems - offset));
    }

    private void initLayout() {
        log().debug("TournamentView :: initLayout");
        var tournamentListComponent = createTournamentListComponent(pagingDataProvider, paginationComponent);
        var tournamentFilterComponent = createFilterComponent(tournamentListComponent);
        var tournamentMasterDetailComponent = createTournamentMasterDetailComponent(tournamentListComponent);


        // Use VerticalLayout to stack filter above master-detail
        VerticalLayout mainLayout = new VerticalLayout(tournamentFilterComponent, tournamentMasterDetailComponent);
        mainLayout.setSizeFull();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(false);
        mainLayout.setFlexGrow(0, tournamentFilterComponent);       // Filter panel takes only needed space
        mainLayout.setFlexGrow(1, tournamentMasterDetailComponent); // Master-detail takes remaining space
        add(mainLayout);
    }

    private PlannedTournamentsFilterComponent createFilterComponent(PlannedTournamentsListCompoment tListComponent) {
        log().debug("TournamentView :: createFilterPanel");
        var filterComponent = new PlannedTournamentsFilterComponent();

        filterComponent.addFilterChangeListener(event -> {
            PlannedTournamentsFilter filter = event.getFilter();
            tListComponent.refresh(filter);
        });

        return filterComponent;
    }

    private PlannedTournamentsListCompoment createTournamentListComponent(DataProvider<PlannedTournament, PlannedTournamentsFilter> pagingDataProvider, PaginationComponent paginationComponent) {
        log().debug("TournamentView :: createTournamentListComponent");
        var component = new PlannedTournamentsListCompoment(pagingDataProvider, paginationComponent);
        component.setSizeFull();
        return component;
    }

    private MasterDetailLayout createTournamentMasterDetailComponent(PlannedTournamentsListCompoment tournamentListComponent) {
        log().debug("TournamentView :: createTournamentMasterDetailComponent");

        var tournamentDetailsComponent = createTournamentDetailsComponent();

        MasterDetailLayout masterDetailLayout = new MasterDetailLayout();
        masterDetailLayout.setMaster(tournamentListComponent);
        masterDetailLayout.setOverlayMode(MasterDetailLayout.OverlayMode.DRAWER);
        masterDetailLayout.setSizeFull();

        tournamentListComponent.getGrid().asSingleSelect().addValueChangeListener(event -> {
            var selectedTournament = event.getValue();
            if (selectedTournament != null) {
                tournamentDetailsComponent.setTournament(selectedTournament);
                masterDetailLayout.setDetail(tournamentDetailsComponent);
            } else {
                masterDetailLayout.setDetail(null);
            }
        });

        tournamentDetailsComponent.addCloseListener(event -> tournamentListComponent.getGrid().deselectAll());
        return masterDetailLayout;
    }

    private PlannedTournamentsDetailsComponent createTournamentDetailsComponent() {
        log().debug("TournamentView :: createTournamentDetailsComponent");
        return new PlannedTournamentsDetailsComponent(routingService);
    }
}
