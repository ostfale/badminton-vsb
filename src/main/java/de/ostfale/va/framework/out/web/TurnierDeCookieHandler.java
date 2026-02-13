package de.ostfale.va.framework.out.web;

import de.ostfale.va.common.UseLogging;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlPage;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TurnierDeCookieHandler implements UseLogging {
    // identification for cookie consent UI
    private static final String CONSENT_UI_ID = "consentui";
    // searching for green div
    private static final String ACCEPT_DIV_XPATH = "//div[@id='buttons']/div[contains(@class, 'green')]";

    public HtmlPage handleIfNecessary(HtmlPage page, WebClient webClient) {
        boolean consentUiPresent = page.getElementById(CONSENT_UI_ID) != null;
        if (!consentUiPresent) {
            return page;
        }

        log().info("Turnier.de Consent-UI (ID: consentui) found. Attempting to accept...");
        return acceptCookieConsent(page, webClient);
    }

    private HtmlPage acceptCookieConsent(HtmlPage page, WebClient webClient) {
        try {
            HtmlElement acceptBtnDiv = page.getFirstByXPath(ACCEPT_DIV_XPATH);
            if (acceptBtnDiv == null) {
                log().warn("The green accept button was not found (XPath incorrect?)");
                return page;
            }

            HtmlPage resultPage = acceptBtnDiv.click();
            webClient.waitForBackgroundJavaScript(2500);
            log().debug("Cookie wall bypassed. Current URL: {}", resultPage.getUrl());
            return resultPage;
        } catch (IOException e) {
            log().error("Error clicking the consent button", e);
            return page;
        }
    }
}
