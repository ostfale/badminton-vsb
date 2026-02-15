package de.ostfale.va.application.port.out.ranking;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ForRankingFileDownloadConfig {

    String CURRENT_RANKING_FILE_URL = "https://turniere.badminton.de/ranking/download?save=1&gender=&gruppe=&lvname=&bezirk=&firstname=&lastname=&club=&colortype=";
    String DBV_RANKING_URL = "https://www.badminton.de/der-dbv/jugend-wettkampf/u19-ranglistentabellen/u19-rangliste/";

    Optional<LocalDateTime> getLatestRemoteTimestamp(String url);

    boolean downloadRankingFile(String sourceUrl, Path targetPath);

    boolean downloadRankingFileIfNewer(String sourceUrl, Path targetPath);

    String prepareDownloadTargetPath(String appDirName);

    String prepareDownloadFileName(LocalDateTime downloadDateTime);

}
