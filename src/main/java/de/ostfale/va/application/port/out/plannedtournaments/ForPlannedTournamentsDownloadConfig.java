package de.ostfale.va.application.port.out.plannedtournaments;

import de.ostfale.va.application.domain.model.download.DownloadTask;

import java.util.List;

public interface ForPlannedTournamentsDownloadConfig {

    List<DownloadTask> getDownloadTasks();
}

