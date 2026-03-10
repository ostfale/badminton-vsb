package de.ostfale.va.framework.out.web;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import de.ostfale.va.application.port.out.ranking.ForLoadingExternalWebsites;
import de.ostfale.va.application.port.out.ranking.PageProcessor;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
public class ForLoadingExternalWebsitesAdapter implements ForLoadingExternalWebsites, UseLogging {

    private static final int NAVIGATION_TIMEOUT_MS = 30000;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";

    private final CompletableFuture<Browser> browserFuture;
    private final TurnierDeCookieHandler cookieHandler;

    private BrowserContext sharedContext;

    public ForLoadingExternalWebsitesAdapter(CompletableFuture<Browser> browserFuture, TurnierDeCookieHandler cookieHandler) {
        this.browserFuture = browserFuture;
        this.cookieHandler = cookieHandler;
    }

    @Override
    public <T> Optional<T> loadPageAndProcess(String url, PageProcessor<T> processor) {
        try {
            Browser browser = browserFuture.join();

            if (sharedContext == null) {
                sharedContext = browser.newContext(new Browser.NewContextOptions()
                        .setUserAgent(USER_AGENT)
                        .setViewportSize(1920, 1080));
                log().info("ForLoadingExternalWebsitesAdapter :: Create shared context for faster website loading");
            }

            try (Page page = sharedContext.newPage()) {
                log().debug("ForLoadingExternalWebsitesAdapter :: Navigate to {} (nutze persistenten Kontext)", url);

                page.navigate(url, new Page.NavigateOptions().setTimeout(NAVIGATION_TIMEOUT_MS));

                // Der Handler wird nur aktiv, wenn die Cookie-Wall wirklich erscheint.
                // Da der Cookie im sharedContext gespeichert bleibt, passiert das nur beim ersten Mal.
                cookieHandler.handleIfNecessary(page, sharedContext);

                return processor.process(page);
            }
        } catch (Exception e) {
            log().error("Adapter :: Fehler beim Laden von {} - {}", url, e.getMessage(), e);
            return Optional.empty();
        }
    }
}
