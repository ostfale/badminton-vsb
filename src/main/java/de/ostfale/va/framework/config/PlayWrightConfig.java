package de.ostfale.va.framework.config;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import de.ostfale.va.common.UseLogging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;


@EnableAsync
@Configuration
public class PlayWrightConfig implements UseLogging {

    @Bean(destroyMethod = "close")
    public Playwright playwright() {
        return Playwright.create();
    }

    @Bean
    public CompletableFuture<Browser> asyncBrowser(Playwright playwright) {
        log().info("PlayWrightConfig :: Start Playwright Browser im Hintergrund...");
        return CompletableFuture.supplyAsync(() -> {
            // Browser starten
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true) // Auf 'false' setzen, wenn du zuschauen willst!
                    .setTimeout(15000));

            log().info("PlayWrightConfig :: Playwright Browser initialisiert.");
            return browser;
        });
    }
}
