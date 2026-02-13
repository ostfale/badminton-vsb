package de.ostfale.va.framework.out.web;

import de.ostfale.va.application.port.out.ForLoadingExternalWebsites;
import de.ostfale.va.common.UseLogging;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlPage;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

@Component
public class ForLoadingExternalWebsitesAdapter implements ForLoadingExternalWebsites, UseLogging {
    private static final int BACKGROUND_JS_WAIT_MS = 1000;

    private final ObjectProvider<WebClient> clientProvider;
    private final TurnierDeCookieHandler cookieHandler;

    public ForLoadingExternalWebsitesAdapter(ObjectProvider<WebClient> clientProvider, TurnierDeCookieHandler cookieHandler) {
        this.clientProvider = clientProvider;
        this.cookieHandler = cookieHandler;
    }

    @Override
    public HtmlPage loadPage(String url) {
        try (WebClient webClient = clientProvider.getIfAvailable()) {
            if (webClient == null) {
                log().error("WebClient not available for loading {}", url);
                return null;
            }
            return loadPageWithCookieHandling(webClient, url);
        } catch (Exception e) {
            log().error("Failed to load page from {}", url, e);
            return null;
        }
    }

    @Override
    public Optional<LocalDateTime> getLastUpdateTimestamp(String url) {
        try (WebClient client = clientProvider.getIfAvailable()) {
            HtmlPage page = Objects.requireNonNull(client, "WebClient provider returned null").getPage(url);
            HtmlElement timestampElement = page.getHtmlElementById("infopop");

            if (timestampElement != null) {
                String timestampText = timestampElement.asNormalizedText().trim();
                return parseTimestamp(timestampText);
            }
        } catch (Exception e) {
            log().error("Konnte Zeitstempel nicht lesen", e);
        }
        return Optional.empty();
    }

    private Optional<LocalDateTime> parseTimestamp(String timestampText) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return Optional.of(LocalDateTime.parse(timestampText, formatter));
    }

    private HtmlPage loadPageWithCookieHandling(WebClient webClient, String url) throws Exception {
        HtmlPage page = webClient.getPage(url);
        webClient.waitForBackgroundJavaScript(BACKGROUND_JS_WAIT_MS);
        return cookieHandler.handleIfNecessary(page, webClient);
    }
}
