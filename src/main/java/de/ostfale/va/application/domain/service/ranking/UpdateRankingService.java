package de.ostfale.va.application.domain.service.ranking;

import de.ostfale.va.application.port.out.ranking.ForRankingFileDownload;
import de.ostfale.va.common.UseCase;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.out.filesystem.ApplicationDirectoryConfiguration;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@UseCase
@Service
public class UpdateRankingService implements UseFileSystemHandling, UseLogging {

    private static final String RANKING_URL = "https://www.badminton.de/der-dbv/jugend-wettkampf/u19-ranglistentabellen/u19-rangliste/";

    private final ForRankingFileDownload rankingWebPort;

    public UpdateRankingService(ForRankingFileDownload rankingWebPort) {
        this.rankingWebPort = rankingWebPort;
    }

    public void updateRankingIfNewer() {
        Optional<LocalDateTime> remoteTime = rankingWebPort.getLatestRemoteTimestamp(RANKING_URL);

        Path rankingFolder = Path.of(getApplicationSubDir(ApplicationDirectoryConfiguration.RANKING_DIR_NAME));
        Path localFile = rankingFolder.resolve("u19_ranking.xlsx");

        LocalDateTime localTime = getFileTimestamp(localFile);

        if (remoteTime.isPresent() && remoteTime.get().isAfter(localTime)) {
            log().info("Neues Ranking gefunden (Remote: {} / Lokal: {}). Starte Download...", remoteTime.get(), localTime);
            rankingWebPort.downloadRankingFile(RANKING_URL, localFile);
        } else {
            log().info("Das Ranking im Verzeichnis 'ranking' ist aktuell.");
        }
    }

    private LocalDateTime getFileTimestamp(Path path) {
        if (Files.notExists(path)) {
            return LocalDateTime.MIN;
        }
        try {
            return LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneId.systemDefault());
        } catch (IOException e) {
            log().error("Fehler beim Lesen des Dateidatums von {}", path, e);
            return LocalDateTime.MIN;
        }
    }
}
