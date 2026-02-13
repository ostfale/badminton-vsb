package de.ostfale.va.application.port.out;

import org.htmlunit.html.HtmlPage;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ForLoadingExternalWebsites {
    HtmlPage loadPage(String url);

    Optional<LocalDateTime> getLastUpdateTimestamp(String url);
}
