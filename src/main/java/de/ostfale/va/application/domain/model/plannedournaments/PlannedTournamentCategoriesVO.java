package de.ostfale.va.application.domain.model.plannedournaments;

import java.util.Arrays;

public enum PlannedTournamentCategoriesVO {

    A("A-Level", "A"),
    B("B-Level", "B"),
    BEC("BEC Event", "BEC"),
    BEC15("BEC-U15", "BEC"),
    BEC17("BEC-U17", "BEC"),
    BWF("BWF Event", "BWF"),
    C("C-Level", "C"),
    C1("C1-Level", "C"),
    C2("C2-Level", "C"),
    C3("C3-Level", "C"),
    D("D-Level", "D"),
    D1("D1-Level", "D"),
    D2("D2-Level", "D"),
    D3("D3-Level", "D"),
    E("E-Level", "E");

    private static final String ERROR_MESSAGE_TEMPLATE = "Unknown TournamentCategory: %s";
    private final String displayName;
    private final String baseCategory;

    PlannedTournamentCategoriesVO(String displayName, String baseCategory) {
        this.displayName = displayName;
        this.baseCategory = baseCategory;
    }

    public static PlannedTournamentCategoriesVO fromDisplayName(String displayName) {
        return Arrays.stream(values())
                .filter(category -> category.displayName.equalsIgnoreCase(displayName))
                .findFirst()
                .orElseThrow(() -> new PlannedTournamentCategoryNotFoundException(String.format(ERROR_MESSAGE_TEMPLATE, displayName)));
    }

    public static PlannedTournamentCategoriesVO[] getFilterValues() {
        return new PlannedTournamentCategoriesVO[]{BEC, BWF, A, B, C, D, E};
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBaseCategory() {
        return baseCategory;
    }
}
