package de.ostfale.va.framework.in.ui.plannedtournaments;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentsFilter;
import de.ostfale.va.application.domain.model.plannedournaments.vo.PlannedTournamentCategoriesVO;
import de.ostfale.va.application.domain.model.plannedournaments.vo.TournamentAgeClassesVO;
import de.ostfale.va.common.UseLogging;

public class PlannedTournamentsFilterComponent extends VerticalLayout implements UseLogging {

    private static final String TOURNAMENT_VIEW_NAME = "Termine des DBV";
    private static final String TOURNAMENT_NAME_FILTER = "Turniername";
    private static final String TOURNAMENT_NAME_PLACEHOLDER = "Name eingeben";
    private static final String LOCATION_NAME_FILTER = "Turnierort";
    private static final String LOCATION_NAME_PLACEHOLDER = "Ort eingeben";
    private static final String AGE_CLASS_FILTER = "Altersklasse";
    private static final String AGE_CLASS_PLACEHOLDER = "Auswählen";
    private static final String TOUR_CAT_FILTER = "Spielklasse";
    private static final String TOUR_CAT_PLACEHOLDER = "Auswählen";
    private static final String TOUR_VALID_NAME = "Verbleibende Turniere";
    private static final String TOUR_CURRENT_YEAR_NAME = "Turniere dieses Jahr";

    private static final String FILTER_BUTTON_LABEL = "Filter";
    private static final String RESET_BUTTON_LABEL = "Reset";

    private static final String FIELD_WIDTH = "15%";

    // filter label
    private final TextField nameFilter = new TextField(TOURNAMENT_NAME_FILTER);
    private final TextField locationFilter = new TextField(LOCATION_NAME_FILTER);
    private final MultiSelectComboBox<TournamentAgeClassesVO> ageClassFilter = new MultiSelectComboBox<>(AGE_CLASS_FILTER);
    private final MultiSelectComboBox<PlannedTournamentCategoriesVO> tourCategoryFilter = new MultiSelectComboBox<>(TOUR_CAT_FILTER);

    private final Button applyButton = new Button(FILTER_BUTTON_LABEL);
    private final Button clearButton = new Button(RESET_BUTTON_LABEL);

    private final Checkbox remainingTournamentsCheckbox = new Checkbox(TOUR_VALID_NAME);
    private final Checkbox currentYearTournamentsCheckbox = new Checkbox(TOUR_CURRENT_YEAR_NAME);

    public PlannedTournamentsFilterComponent() {
        log().debug("PlannedTournamentsFilterComponent :: Created");
        initLayoutSettings();
        add(createTitle(), createCheckboxLayout(), createFilterLayout(), createButtonLayout());
    }

    public PlannedTournamentsFilter getCurrentFilter() {
        var filter = PlannedTournamentsFilter.builder()
                .withName(nameFilter.getValue())
                .withLocation(locationFilter.getValue())
                .withOnlyThisYearsTournaments(currentYearTournamentsCheckbox.getValue())
                .withValidTournamentsOnly(remainingTournamentsCheckbox.getValue())
                .withAgeClasses(ageClassFilter.getValue())
                .withTourCategories(tourCategoryFilter.getValue())
                .build();
        log().info("TournamentFilterPanel :: getCurrentFilter: {}", filter);
        return filter;
    }

    public void addFilterChangeListener(ComponentEventListener<FilterChangeEvent> listener) {
        addListener(FilterChangeEvent.class, listener);
    }

    private void initLayoutSettings() {
        setSpacing(false);
        setPadding(true);
        setWidthFull();
    }

    private Component createTitle() {
        Paragraph title = new Paragraph(TOURNAMENT_VIEW_NAME);
        title.setId("view-title");
        return title;
    }

    private Component createCheckboxLayout() {
        remainingTournamentsCheckbox.setValue(true);
        currentYearTournamentsCheckbox.setValue(false);

        HorizontalLayout checkboxLayout = new HorizontalLayout(remainingTournamentsCheckbox, currentYearTournamentsCheckbox);
        checkboxLayout.setSpacing(true);

        return checkboxLayout;
    }

    private Component createFilterLayout() {
        nameFilter.setPlaceholder(TOURNAMENT_NAME_PLACEHOLDER);
        nameFilter.setWidth(FIELD_WIDTH);
        nameFilter.setClearButtonVisible(true);

        locationFilter.setPlaceholder(LOCATION_NAME_PLACEHOLDER);
        locationFilter.setWidth(FIELD_WIDTH);
        locationFilter.setClearButtonVisible(true);

        ageClassFilter.setPlaceholder(AGE_CLASS_PLACEHOLDER);
        ageClassFilter.setWidth(FIELD_WIDTH);
        ageClassFilter.setItems(TournamentAgeClassesVO.getFilterValues());

        tourCategoryFilter.setPlaceholder(TOUR_CAT_PLACEHOLDER);
        tourCategoryFilter.setWidth(FIELD_WIDTH);
        tourCategoryFilter.setItems(PlannedTournamentCategoriesVO.getFilterValues());

        HorizontalLayout textFields = new HorizontalLayout(nameFilter, locationFilter, ageClassFilter, tourCategoryFilter);
        textFields.setSpacing(true);
        textFields.setWidthFull();

        return textFields;
    }

    private Component createButtonLayout() {
        applyButton.setIcon(VaadinIcon.FILTER.create());
        applyButton.addClickListener(e -> fireFilterChangeEvent());

        clearButton.setIcon(VaadinIcon.CLOSE_CIRCLE.create());
        clearButton.addClickListener(e -> clearFilters());

        HorizontalLayout buttons = new HorizontalLayout(applyButton, clearButton);
        buttons.setSpacing(true);
        buttons.setJustifyContentMode(JustifyContentMode.END);
        buttons.getStyle().set("margin-top", "20px");
        return buttons;
    }

    private void clearFilters() {
        nameFilter.clear();
        locationFilter.clear();
        ageClassFilter.clear();
        tourCategoryFilter.clear();
        fireFilterChangeEvent();
    }

    private void fireFilterChangeEvent() {
        fireEvent(new FilterChangeEvent(this, false, getCurrentFilter()));
    }

    public static class FilterChangeEvent extends ComponentEvent<PlannedTournamentsFilterComponent> {
        private final PlannedTournamentsFilter filter;

        public FilterChangeEvent(PlannedTournamentsFilterComponent source, boolean fromClient, PlannedTournamentsFilter filter) {
            super(source, fromClient);
            this.filter = filter;
        }

        public PlannedTournamentsFilter getFilter() {
            return filter;
        }
    }
}
