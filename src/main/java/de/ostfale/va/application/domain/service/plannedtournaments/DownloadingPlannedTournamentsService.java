package de.ostfale.va.application.domain.service.plannedtournaments;

import de.ostfale.va.application.port.in.plannedtournaments.ForDownloadingPlannedTournamentsUC;
import de.ostfale.va.application.port.out.ForDownloadingFiles;
import de.ostfale.va.application.port.out.plannedtournaments.ForPlannedTournamentsDownloadConfig;
import de.ostfale.va.common.UseCase;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Service;

@UseCase
@Service
public class DownloadingPlannedTournamentsService implements ForDownloadingPlannedTournamentsUC, UseFileSystemHandling, UseLogging {

    private final ForDownloadingFiles downloadService;
    private final ForPlannedTournamentsDownloadConfig downloadConfig;

    public DownloadingPlannedTournamentsService(ForDownloadingFiles downloadService, ForPlannedTournamentsDownloadConfig downloadConfig) {
        this.downloadService = downloadService;
        this.downloadConfig = downloadConfig;
    }

    @Override
    public void downloadPlannedTournaments() {
        var tasks = downloadConfig.getDownloadTasks();
        downloadService.downloadFiles(tasks);
    }
}
