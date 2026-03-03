package de.ostfale.va.framework.out.web;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import de.ostfale.va.application.port.out.ranking.ForLoadingExternalWebsites;
import de.ostfale.va.common.UseLogging;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class ForLoadingExternalWebsitesAdapter implements ForLoadingExternalWebsites, UseLogging {

    private static final int NAVIGATION_TIMEOUT_MS = 30000;

    private final ObjectProvider<Browser> browserProvider;
    private final TurnierDeCookieHandler cookieHandler;

    public ForLoadingExternalWebsitesAdapter(ObjectProvider<Browser> browserProvider, TurnierDeCookieHandler cookieHandler) {
        this.browserProvider = browserProvider;
        this.cookieHandler = cookieHandler;
    }

    @Override
    public Page loadPage(String url) {
        Browser browser = browserProvider.getIfAvailable();
        if (browser == null) {
            log().error("Browser not available for loading {}", url);
            return null;
        }

        try {
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            page.navigate(url, new Page.NavigateOptions().setTimeout(NAVIGATION_TIMEOUT_MS));
            page.waitForLoadState();

            return cookieHandler.handleIfNecessary(page, context);
        } catch (Exception e) {
            log().error("Failed to load page from {}", url, e);
            return null;
        }
    }

    @Override
    public Optional<LocalDateTime> getLastUpdateTimestamp(String url) {
        Browser browser = browserProvider.getIfAvailable();
        if (browser == null) {
            log().error("Browser not available");
            return Optional.empty();
        }

        BrowserContext context = browser.newContext();
        try (Page page = context.newPage()) {
            page.navigate(url);
            Locator timestampElement = page.locator("#infopop");

            if (timestampElement.count() > 0) {
                String timestampText = timestampElement.textContent().trim();
                return parseTimestamp(timestampText);
            }
        } catch (Exception e) {
            log().error("Konnte Zeitstempel nicht lesen", e);
        } finally {
            context.close();
        }
        return Optional.empty();
    }

    private Optional<LocalDateTime> parseTimestamp(String timestampText) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return Optional.of(LocalDateTime.parse(timestampText, formatter));
    }

}
