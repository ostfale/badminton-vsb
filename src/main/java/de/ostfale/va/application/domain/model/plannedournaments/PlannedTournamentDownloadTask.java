package de.ostfale.va.application.domain.model.plannedournaments;

import java.nio.file.Path;

public record PlannedTournamentDownloadTask(
        String url,
        Path destination) {
}
