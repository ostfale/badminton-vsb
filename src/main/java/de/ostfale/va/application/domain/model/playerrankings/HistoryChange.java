package de.ostfale.va.application.domain.model.playerrankings;

import org.jspecify.annotations.NonNull;

public record HistoryChange(
        String timestamp,
        String oldValue,
        String newValue
) {

    @Override
    public @NonNull String toString() {
        return "Kalenderwoche: " + timestamp + " bisher " + oldValue + " -> " + newValue;
    }
}
