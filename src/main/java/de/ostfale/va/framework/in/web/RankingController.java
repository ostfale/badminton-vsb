package de.ostfale.va.framework.in.web;

import de.ostfale.va.application.port.out.ranking.ForRankingFileDownloadConfig;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.out.filesystem.ApplicationDirectoryConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/ranking")
public class RankingController implements UseLogging {

    private final ForRankingFileDownloadConfig rankingFileDownload;

    public RankingController(ForRankingFileDownloadConfig rankingFileDownload) {
        this.rankingFileDownload = rankingFileDownload;
    }

   /* @GetMapping("/timestamp")
    public LocalDateTime getLastRankingUpdateTimestamp() {
        log().info("RankingController :: Fetching latest ranking update timestamp");
        var result = rankingFileDownload.getLatestRemoteTimestamp(ForRankingFileDownloadConfig.DBV_RANKING_URL);
        if (result.isPresent()) {
            log().info("RankingController :: Latest ranking update timestamp is {}", result.get());
            return result.get();
        } else {
            log().warn("RankingController :: No ranking update timestamp found");
            return LocalDateTime.MIN;
        }
    }
    @GetMapping("/download")
    public boolean downloadRankingFile() {
        log().info("RankingController :: Triggering download of ranking file");
        String destinationPath = rankingFileDownload.prepareDownloadTargetPath(ApplicationDirectoryConfiguration.RANKING_DIR_NAME);
        String targetFileName = rankingFileDownload.prepareDownloadFileName(LocalDateTime.now());
        Path rankingFilePath = Path.of(destinationPath + targetFileName);
        return rankingFileDownload.downloadRankingFileIfNewer(ForRankingFileDownloadConfig.CURRENT_RANKING_FILE_URL, rankingFilePath);
    }*/
}
