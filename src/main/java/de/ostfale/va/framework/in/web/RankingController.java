package de.ostfale.va.framework.in.web;

import de.ostfale.va.application.port.out.ranking.ForRankingFileDownload;
import de.ostfale.va.common.UseLogging;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/ranking")
public class RankingController implements UseLogging {

    private final ForRankingFileDownload rankingFileDownload;

    public RankingController(ForRankingFileDownload rankingFileDownload) {
        this.rankingFileDownload = rankingFileDownload;
    }

    @GetMapping("/timestamp")
    public LocalDateTime getLastRankingUpdateTimestamp() {
        log().info("RankingController :: Fetching latest ranking update timestamp");
        var result = rankingFileDownload.getLatestRemoteTimestamp(ForRankingFileDownload.DBV_RANKINGURL);
        if (result.isPresent()) {
            log().info("RankingController :: Latest ranking update timestamp is {}", result.get());
            return result.get();
        } else {
            log().warn("RankingController :: No ranking update timestamp found");
            return LocalDateTime.MIN;
        }
    }
}
