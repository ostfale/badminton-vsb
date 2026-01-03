package de.ostfale.va.application.domain.model.plannedournaments;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum PlannedTournamentTypesVO {
    RANKING("Rangliste"),
    CHAMPIONSHIP("Meisterschaft");

    private static final String ERROR_MESSAGE_TEMPLATE = "Unknown TournamentType: %s";
    private static final Map<String, PlannedTournamentTypesVO> BY_DISPLAY_STRING;

    static {
        Map<String, PlannedTournamentTypesVO> map = new HashMap<>();
        for (PlannedTournamentTypesVO type : values()) {
            map.put(type.displayString.toLowerCase(), type);
        }
        BY_DISPLAY_STRING = Collections.unmodifiableMap(map);
    }

    private final String displayString;

    PlannedTournamentTypesVO(String displayString) {
        this.displayString = displayString;
    }

    public static PlannedTournamentTypesVO fromDisplayString(String displayString) {
        return Optional.ofNullable(displayString)
                .map(String::toLowerCase)
                .map(BY_DISPLAY_STRING::get)
                .orElseThrow(() -> new PlannedTournamentTypeNotFoundException(String.format(ERROR_MESSAGE_TEMPLATE, displayString)));
    }

    public String getDisplayString() {
        return displayString;
    }
}
