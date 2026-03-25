package de.ostfale.va.framework.out.web;

import de.ostfale.va.application.domain.model.download.DownloadTask;
import de.ostfale.va.application.port.out.ForDownloadingFiles;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Component
public class HttpFileDownloadAdapter implements ForDownloadingFiles, UseFileSystemHandling, UseLogging {

    private final HttpClient httpClient;

    public HttpFileDownloadAdapter() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    @Override
    public void downloadFiles(List<DownloadTask> tasks) {
        if (tasks.isEmpty()) {
            return;
        }

        log().debug("HttpDownloadAdapter :: Processing {} download tasks", tasks.size());

        tasks.stream()
                .map(task -> task.destination().getParent())
                .filter(Objects::nonNull)
                .distinct()
                .forEach(this::prepareTargetDirectory);

        tasks.forEach(this::downloadFile);
    }

    private void downloadFile(DownloadTask task) {
        log().debug("HttpDownloadAdapter :: Downloading from {} to {}", task.url(), task.destination());

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(task.url()))
                    .timeout(Duration.ofMinutes(2))
                    .GET()
                    .build();

            // Der BodyHandler schreibt den Stream direkt in die Ziel-Datei
            HttpResponse<Path> response = httpClient.send(request, HttpResponse.BodyHandlers.ofFile(task.destination()));

            if (response.statusCode() == 200) {
                log().info("HttpDownloadAdapter :: Successfully downloaded: {}", task.destination().getFileName());
            } else {
                log().error("HttpDownloadAdapter :: Failed to download. HTTP Status: {}", response.statusCode());
                // Falls der Download fehlschlägt, löschen wir die (eventuell korrupte) Datei
                Files.deleteIfExists(task.destination());
            }
        } catch (Exception e) {
            log().error("HttpDownloadAdapter :: Error during download from {}", task.url(), e);
        }
    }

    private void prepareTargetDirectory(Path targetDir) {
        try {
            if (Files.exists(targetDir)) {
                log().debug("HttpDownloadAdapter :: Cleaning existing directory: {}", targetDir);
                deleteAllFiles(targetDir.toString());
            } else {
                log().debug("HttpDownloadAdapter :: Creating target directory: {}", targetDir);
                Files.createDirectories(targetDir);
            }
        } catch (IOException e) {
            log().error("HttpDownloadAdapter :: Failed to prepare target directory: {}", targetDir, e);
        }
    }
}
