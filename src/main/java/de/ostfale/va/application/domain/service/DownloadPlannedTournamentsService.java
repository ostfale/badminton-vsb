package de.ostfale.va.application.domain.service;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentDownloadTask;
import de.ostfale.va.application.port.in.ForDownloadingFromWeb;
import de.ostfale.va.application.port.out.plannedtournaments.ForPlannedTournamentsDownloadConfig;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

@Service
public class DownloadPlannedTournamentsService implements ForDownloadingFromWeb, UseFileSystemHandling, UseLogging {

    private final ForPlannedTournamentsDownloadConfig downloadConfig;
    private final HttpClient httpClient;

    public DownloadPlannedTournamentsService(ForPlannedTournamentsDownloadConfig downloadConfig) {
        this.downloadConfig = downloadConfig;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    @Override
    @Scheduled(cron = "0 0 3 * * *") // Runs every night at 3:00 AM
    public void runScheduledDownload() {
        log().info("DownloadPlannedTournamentsService :: Starting scheduled download tasks...");
        performDownload();
    }

    @Async
    @Override
    public void performDownload() {
        log().info("DownloadPlannedTournamentsService :: Starting asynchronous download...");
        var tasks = downloadConfig.getDownloadTasks();
        ensureParentDirectoryExists(tasks.getFirst().destination());
        tasks.forEach(this::downloadFile);
    }

    private void downloadFile(PlannedTournamentDownloadTask task) {
        log().debug("DownloadPlannedTournamentsService :: Downloading from {} to {}", task.url(), task.destination());

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(task.url()))
                    .timeout(Duration.ofMinutes(2))
                    .GET()
                    .build();

            HttpResponse<java.nio.file.Path> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofFile(task.destination()));

            if (response.statusCode() == 200) {
                log().info("DownloadPlannedTournamentsService :: Successfully downloaded: {}", task.destination().getFileName());
            } else {
                log().error("DownloadPlannedTournamentsService :: Failed to download. HTTP Status: {}", response.statusCode());
            }
        } catch (Exception e) {
            log().error("DownloadPlannedTournamentsService :: Error during download from {}", task.url(), e);
        }
    }

    private void ensureParentDirectoryExists(Path destination) {
        Path parentDir = destination.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            try {
                Files.createDirectories(parentDir);
                log().debug("HttpFileDownloadAdapter :: Created directory: {}", parentDir);
            } catch (IOException e) {
                log().error("HttpFileDownloadAdapter :: Failed to create directory: {}", parentDir, e);
            }
        }
        assert parentDir != null;
        deleteAllFiles(parentDir.toString());
    }
}
