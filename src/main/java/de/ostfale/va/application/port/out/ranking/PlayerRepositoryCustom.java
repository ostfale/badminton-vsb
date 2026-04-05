package de.ostfale.va.application.port.out.ranking;

import java.time.LocalDateTime;

public interface PlayerRepositoryCustom {

    // Retrieves the timestamp of the last successful ranking import from the storage root
    LocalDateTime getLastUpdate();

    // Updates the timestamp of the last successful ranking import in the storage root.
    void setLastUpdate(LocalDateTime timestamp);
}
