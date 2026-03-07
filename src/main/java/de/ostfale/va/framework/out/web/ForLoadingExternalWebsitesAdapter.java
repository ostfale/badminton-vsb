package de.ostfale.va.framework.out.web;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import de.ostfale.va.application.port.out.ranking.ForLoadingExternalWebsites;
import de.ostfale.va.application.port.out.ranking.PageProcessor;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class ForLoadingExternalWebsitesAdapter implements ForLoadingExternalWebsites, UseLogging {

    private static final int NAVIGATION_TIMEOUT_MS = 30000;

    private final CompletableFuture<Browser> browserFuture;
    private final TurnierDeCookieHandler cookieHandler;

    public ForLoadingExternalWebsitesAdapter(CompletableFuture<Browser> browserFuture, TurnierDeCookieHandler cookieHandler) {
        this.browserFuture = browserFuture;
        this.cookieHandler = cookieHandler;
    }

    @Override
    public <T> T loadPageAndProcess(String url, PageProcessor<T> processor) {
        if (!browserFuture.isDone()) {
            log().warn("ForLoadingExternalWebsitesAdapter :: request for {}, playwright browser still starting", url);
            return null;
        }

        try (BrowserContext context = browserFuture.join().newContext();
             Page page = context.newPage()) {

            page.navigate(url, new Page.NavigateOptions().setTimeout(NAVIGATION_TIMEOUT_MS));
            page.waitForLoadState();

            Page handledPage = cookieHandler.handleIfNecessary(page, context);
            return processor.process(handledPage);
        } catch (Exception e) {
            log().error("ForLoadingExternalWebsitesAdapter :: Error loading page", e);
            return null;
        }
    }
}
