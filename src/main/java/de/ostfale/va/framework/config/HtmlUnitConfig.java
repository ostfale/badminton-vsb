package de.ostfale.va.framework.config;

import org.htmlunit.BrowserVersion;
import org.htmlunit.NicelyResynchronizingAjaxController;
import org.htmlunit.WebClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class HtmlUnitConfig {

    @Bean
    @Scope("prototype") // creates each time a new instance
    public WebClient webClient() {
        // simulate chrome browser
        WebClient client = new WebClient(BrowserVersion.CHROME);

        client.getOptions().setJavaScriptEnabled(true); // needed for cookie-wall
        client.getOptions().setCssEnabled(false);       // save bandwidth
        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        client.getOptions().setUseInsecureSSL(true);

        // increase timeout for slow connections
        client.getOptions().setTimeout(15000);

        client.getCookieManager().setCookiesEnabled(true);

        // important for turnier.de: wait for AJAX/JS redirects
        client.setAjaxController(new NicelyResynchronizingAjaxController());

        return client;
    }
}
