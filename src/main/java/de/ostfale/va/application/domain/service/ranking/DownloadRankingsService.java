package de.ostfale.va.application.domain.service.ranking;

import de.ostfale.va.application.port.in.ranking.ForDownloadingRankingsUC;
import de.ostfale.va.application.port.in.ranking.ForLoadingRankings;
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
    private final ForLoadingRankings loadingRankings;

    public DownloadRankingsService(
            ForDownloadingFiles fileDownloader, 
            ForRankingFileDownloadConfig downloadConfig,
            ForLoadingRankings loadingRankings) {
        this.fileDownloader = fileDownloader;
        this.downloadConfig = downloadConfig;
        this.loadingRankings = loadingRankings;
    }

    @Override
    public boolean downloadRankings() {
        var tasks = downloadConfig.getDownloadTasks();
        if (tasks.isEmpty()) {
            return false;
        }
        
        // 1. Download the files to the local file system
        fileDownloader.downloadFiles(tasks);
        
        // 2. Trigger the import into EclipseStore (Database)
        log().info("DownloadRankingsService :: Triggering import to EclipseStore after successful download.");
        loadingRankings.importRankingsFromFile();
        
        return true;
    }
}
