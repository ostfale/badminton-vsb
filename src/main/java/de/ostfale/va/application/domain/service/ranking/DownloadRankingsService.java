package de.ostfale.va.application.domain.service.ranking;

import de.ostfale.va.application.port.in.ranking.ForDownloadingRankingsUC;
import de.ostfale.va.application.port.out.ForDownloadingFiles;
import de.ostfale.va.application.port.out.ranking.ForRankingFileDownloadConfig;
import de.ostfale.va.common.UseCase;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Service;

@UseCase
@Service
public class DownloadRankingsService implements ForDownloadingRankingsUC, UseLogging {

    private final ForDownloadingFiles fileDownloader;
    private final ForRankingFileDownloadConfig downloadConfig;

    public DownloadRankingsService(ForDownloadingFiles fileDownloader, ForRankingFileDownloadConfig downloadConfig) {
        this.fileDownloader = fileDownloader;
        this.downloadConfig = downloadConfig;
    }

    @Override
    public boolean downloadRankings() {
        var tasks = downloadConfig.getDownloadTasks();
        if (tasks.isEmpty()) {
            return false;
        }
        fileDownloader.downloadFiles(tasks);
        return true;
    }
}
