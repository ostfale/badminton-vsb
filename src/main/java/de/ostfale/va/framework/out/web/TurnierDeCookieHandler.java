package de.ostfale.va.framework.out.web;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;

@Component
public class TurnierDeCookieHandler implements UseLogging {

    private static final String COOKIE_WALL_URL_PART = "cookiewall";
    // Der eindeutige Selektor für den "Accept"-Button aus dem HTML
    private static final String ACCEPT_BUTTON_SELECTOR = ".js-accept-basic";

    public void handleIfNecessary(Page page, BrowserContext context) {
        if (page.url().contains(COOKIE_WALL_URL_PART)) {
            log().info("TurnierDeCookieHandler :: Cookiewall erkannt. Sende Formular ab...");

            try {
                page.evaluate("document.forms[0].submit();");
                page.waitForURL(url -> !url.contains(COOKIE_WALL_URL_PART),
                        new Page.WaitForURLOptions().setTimeout(3000));
                log().info("TurnierDeCookieHandler :: Aktuelle URL nach Behandlung: {}", page.url());
            } catch (Exception e) {
                log().error("TurnierDeCookieHandler :: Fehler beim Cookie-Handling", e);
            }
        }
    }
}
