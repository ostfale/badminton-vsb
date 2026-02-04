package de.ostfale.va.application.domain.model.plannedournaments;

import java.time.LocalDate;

public record PlannedTournamentKey(
        LocalDate startDate,
        String name,
        String location
) {
    /**
     * Converts this key to a string representation for storage.
     * Format: "startDate|name|location"
     */
    public String toStorageString() {
        return startDate + "|" + name + "|" + location;
    }

    /**
     * Creates a PlannedTournamentKey from a storage string.
     */
    public static PlannedTournamentKey fromStorageString(String storageString) {
        String[] parts = storageString.split("\\|", 3);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid storage string format: " + storageString);
        }
        return new PlannedTournamentKey(
                LocalDate.parse(parts[0]),
                parts[1],
                parts[2]
        );
    }
}
