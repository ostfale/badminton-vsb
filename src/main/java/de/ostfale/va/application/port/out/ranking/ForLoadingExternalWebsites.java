package de.ostfale.va.application.port.out.ranking;

import com.microsoft.playwright.Page;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ForLoadingExternalWebsites {
    Page loadPage(String url);

    Optional<LocalDateTime> getLastUpdateTimestamp(String url);
}
