package de.ostfale.va.application.port.out.ranking;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ForRankingFileDownload {

    String DBV_RANKINGURL = "https://www.badminton.de/der-dbv/jugend-wettkampf/u19-ranglistentabellen/u19-rangliste/";

    Optional<LocalDateTime> getLatestRemoteTimestamp(String url);

    void downloadRankingFile(String sourceUrl, Path targetPath);
}
