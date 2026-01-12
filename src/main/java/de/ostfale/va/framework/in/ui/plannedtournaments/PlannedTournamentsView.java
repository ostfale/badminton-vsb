package de.ostfale.va.framework.in.ui.plannedtournaments;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.in.ui.app.MainLayout;
import org.springframework.context.event.EventListener;

@Route(value = PlannedTournamentsView.PATH, layout = MainLayout.class)
public class PlannedTournamentsView extends VerticalLayout implements UseLogging {

    public static final String PATH = "planned-tournaments-view";

    private final PaginationComponentFactory paginationFactory;
    private PaginationComponent pagination;

    public PlannedTournamentsView(PaginationComponentFactory paginationFactory) {
        this.paginationFactory = paginationFactory;
        initializeUI();
    }

    private void initializeUI() {
        pagination = paginationFactory.create();
        add(pagination);
    }

    // Listen to pagination events using Spring's @EventListener
    @EventListener
    public void onPageChanged(PageChangedEvent event) {
        if (event.getSource() == pagination) {
            loadData(event.getOffset(), event.getPageSize());
        }
    }

    private void loadData(int offset, int pageSize) {
        // Load data from file using offset and pageSize
     /*   List<Item> allItems = readFromFile();
        int start = Math.min(offset, allItems.size());
        int end = Math.min(offset + pageSize, allItems.size());
        List<Item> pageItems = allItems.subList(start, end);

        // Update UI with pageItems
        pagination.setTotalItemCount(allItems.size());*/
    }

}
