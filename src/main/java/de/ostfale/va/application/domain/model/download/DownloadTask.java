package de.ostfale.va.application.domain.model.download;

import java.nio.file.Path;

public record DownloadTask(
        String url,
        Path destination
) {

    public DownloadTask {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        if (destination == null) {
            throw new IllegalArgumentException("Destination cannot be null");
        }
    }
}
