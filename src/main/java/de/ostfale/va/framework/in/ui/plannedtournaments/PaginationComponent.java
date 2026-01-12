package de.ostfale.va.framework.in.ui.plannedtournaments;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.SelectVariant;
import com.vaadin.flow.theme.lumo.LumoUtility;
import de.ostfale.va.common.UseLogging;
import org.springframework.context.ApplicationEventPublisher;


public class PaginationComponent extends HorizontalLayout implements UseLogging {

    private static final int DEFAULT_PAGE_SIZE = 25;

    private final ApplicationEventPublisher eventPublisher;

    private final Span currentPageLabel;
    private final Button firstPageButton;
    private final Button lastPageButton;
    private final Button goToPreviousPageButton;
    private final Button goToNextPageButton;

    private int totalItemCount = 0;
    private int pageCount = 1;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private int currentPage = 1;

    public PaginationComponent(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        currentPageLabel = createCurrentPageLabel();
        firstPageButton = createFirstPageButton();
        lastPageButton = createLastPageButton();
        goToPreviousPageButton = createPreviousPageButton();
        goToNextPageButton = createNextPageButton();

        setDefaultVerticalComponentAlignment(Alignment.CENTER);
        setSpacing("0.3rem");
        setWidthFull();
        addToStart(createPageSizeField());
        addToEnd(firstPageButton, goToPreviousPageButton, currentPageLabel, goToNextPageButton, lastPageButton);
    }

    public int getPageSize() {
        return pageSize;
    }

    public int calculateOffset() {
        return (currentPage - 1) * pageSize;
    }

    public void recalculatePageCount(int totalItemCount) {
        this.totalItemCount = totalItemCount;
        updatePageCount();
    }

    /**
     * Updates the total item count and refreshes the pagination state.
     * This should be called by the parent view when data is loaded.
     */
    public void setTotalItemCount(int totalItemCount) {
        this.totalItemCount = totalItemCount;
        updatePageCount();
    }

    public void reset() {
        this.currentPage = 1;
    }

    private Component createPageSizeField() {
        Select<Integer> select = new Select<>();
        select.addThemeVariants(SelectVariant.LUMO_SMALL);
        select.getStyle().set("--vaadin-input-field-value-font-size", "var(--lumo-font-size-s)");
        select.setWidth("4.8rem");
        select.setItems(10, 15, 25, 50, 100);
        select.setValue(pageSize);
        select.addValueChangeListener(e -> {
            pageSize = e.getValue();
            updatePageCount();
            // Explicitly fire event because page size change always requires grid refresh
            firePageChangedEvent();
        });
        var label = new Span("Page size");
        label.setId("page-size-label");
        label.addClassName(LumoUtility.FontSize.SMALL);
        select.setAriaLabelledBy("page-size-label");
        final HorizontalLayout layout = new HorizontalLayout(Alignment.CENTER, label, select);
        layout.setSpacing(false);
        layout.getThemeList().add("spacing-s");
        return layout;
    }

    private void updatePageCount() {
        if (totalItemCount == 0) {
            this.pageCount = 1; // we still want to display one page even though there are no items
        } else {
            this.pageCount = (int) Math.ceil((double) totalItemCount / pageSize);
        }

        int oldPage = currentPage;
        // Ensure current page is valid after count update
        if (currentPage > pageCount) {
            currentPage = pageCount;
        } else if (currentPage < 1) {
            currentPage = 1;
        }

        updateControls();
        // Only fire if the page actually changed to avoid infinite loops with DataProvider
        if (oldPage != currentPage) {
            firePageChangedEvent();
        }
    }

    private Button createIconButton(VaadinIcon icon, String ariaLabel, Runnable onClickListener) {
        Button button = new Button(new Icon(icon));
        button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);
        button.addClickListener(e -> {
            onClickListener.run();
            updateControls();
            firePageChangedEvent();
        });
        button.setAriaLabel(ariaLabel);
        return button;
    }

    private void firePageChangedEvent() {
        // Publish Spring event
        if (eventPublisher != null) {
            eventPublisher.publishEvent(
                    new PageChangedEvent(this, currentPage, pageSize, calculateOffset())
            );
        }
    }

    private void updateControls() {
        currentPageLabel.setText(String.format("Page %d of %d", currentPage, pageCount));
        firstPageButton.setEnabled(currentPage > 1);
        lastPageButton.setEnabled(currentPage < pageCount);
        goToPreviousPageButton.setEnabled(currentPage > 1);
        goToNextPageButton.setEnabled(currentPage < pageCount);
    }


    private Button createFirstPageButton() {
        return createIconButton(VaadinIcon.ANGLE_DOUBLE_LEFT, "Go to first page", () -> currentPage = 1);
    }

    private Button createLastPageButton() {
        return createIconButton(VaadinIcon.ANGLE_DOUBLE_RIGHT, "Go to last page", () -> currentPage = pageCount);
    }

    private Button createNextPageButton() {
        return createIconButton(VaadinIcon.ANGLE_RIGHT, "Go to next page", () -> currentPage++);
    }

    private Button createPreviousPageButton() {
        return createIconButton(VaadinIcon.ANGLE_LEFT, "Go to previous page", () -> currentPage--);
    }

    private Span createCurrentPageLabel() {
        var label = new Span();
        label.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.Padding.Horizontal.SMALL);
        return label;
    }
}
