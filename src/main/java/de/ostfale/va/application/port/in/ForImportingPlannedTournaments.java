package de.ostfale.va.application.port.in;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;

import java.io.InputStream;
import java.util.List;

public interface ForImportingPlannedTournaments {
    List<PlannedTournament> importFromSource();

    String getLastDownloadDate();
}
