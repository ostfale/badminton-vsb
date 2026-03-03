package de.ostfale.va.framework.out.web;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;

@Component
public class TurnierDeCookieHandler implements UseLogging {
    // identification for cookie consent UI
    private static final String CONSENT_UI_ID = "consentui";
    // searching for green div
    private static final String ACCEPT_DIV_XPATH = "//div[@id='buttons']/div[contains(@class, 'green')]";

    public Page handleIfNecessary(Page page, BrowserContext context) {
        Locator consentUi = page.locator("#" + CONSENT_UI_ID);
        boolean consentUiPresent = consentUi.count() > 0;

        if (!consentUiPresent) {
            return page;
        }

        log().info("TurnierDeCookieHandler :: Turnier.de Consent-UI (ID: consentui) found. Attempting to accept...");
        return acceptCookieConsent(page);
    }

    private Page acceptCookieConsent(Page page) {
        try {
            Locator acceptBtn = page.locator(ACCEPT_DIV_XPATH).first();

            if (acceptBtn.count() == 0) {
                log().warn("TurnierDeCookieHandler :: The green accept button was not found (XPath incorrect?)");
                return page;
            }

            acceptBtn.click();
            page.waitForTimeout(2500); // wait for JS to complete
            log().debug("TurnierDeCookieHandler :: Cookie wall bypassed. Current URL: {}", page.url());
            return page;
        } catch (Exception e) {
            log().error("TurnierDeCookieHandler :: Error clicking the consent button", e);
            return page;
        }
    }
}
