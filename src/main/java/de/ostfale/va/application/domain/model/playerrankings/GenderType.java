package de.ostfale.va.application.domain.model.playerrankings;

import java.util.Arrays;

public enum GenderType {
    MALE("M"),
    FEMALE("F");

    private final String displayName;

    GenderType(String aDisplayName) {
        this.displayName = aDisplayName;
    }

    public static GenderType lookup(String displayName) {
        return Arrays.stream(values())
                .filter(type -> type.displayName.equalsIgnoreCase(displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("GenderType: " + displayName));
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
