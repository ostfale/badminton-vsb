package de.ostfale.va.framework.out.web;

import de.ostfale.va.application.domain.model.download.DownloadTask;
import de.ostfale.va.application.port.out.ForDownloadingFiles;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.out.filesystem.ApplicationDirectoryConfiguration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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

            // The BodyHandler writes the stream directly to the target file
            HttpResponse<Path> response = httpClient.send(request, HttpResponse.BodyHandlers.ofFile(task.destination()));

            if (response.statusCode() == 200) {
                log().info("HttpDownloadAdapter :: Successfully downloaded: {}", task.destination().getFileName());
            } else {
                log().error("HttpDownloadAdapter :: Failed to download. HTTP Status: {}", response.statusCode());
                // If the download fails, we delete the (possibly corrupt) file
                Files.deleteIfExists(task.destination());
            }
        } catch (Exception e) {
            log().error("HttpDownloadAdapter :: Error during download from {}", task.url(), e);
        }
    }

    private void prepareTargetDirectory(Path targetDir) {
        try {
            if (Files.exists(targetDir)) {
                log().debug("HttpDownloadAdapter :: Moving existing files to history directory before downloading new files to: {}", targetDir);
                if (!targetDir.endsWith(ApplicationDirectoryConfiguration.TOURNAMENT_DIR_NAME)) {
                    archiveFiles(targetDir);
                }
            } else {
                log().debug("HttpDownloadAdapter :: Creating target directory: {}", targetDir);
                Files.createDirectories(targetDir);
            }
        } catch (IOException e) {
            log().error("HttpDownloadAdapter :: Failed to prepare target directory: {}", targetDir, e);
        }
    }

    private void archiveFiles(Path sourceDir) throws IOException {
        String historyDirPath = getApplicationHomeDir() + SEPARATOR + ApplicationDirectoryConfiguration.HISTORY_DIR_NAME;
        Path historyPath = Path.of(historyDirPath);

        if (!Files.exists(historyPath)) {
            Files.createDirectories(historyPath);
        }

        try (Stream<Path> stream = Files.list(sourceDir)) {
            stream.filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Path targetFile = historyPath.resolve(file.getFileName());
                            log().info("HttpDownloadAdapter :: Moving file {} to history: {}", file.getFileName(), historyDirPath);
                            Files.move(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            log().error("HttpDownloadAdapter :: Failed to move file {} to history", file.getFileName(), e);
                        }
                    });
        }
    }
}
