package de.ostfale.va.framework.out.web;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BadmintonDeTimestampParser implements UseLogging {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public Optional<LocalDateTime> parseLastUpdate(Page page) {
        try {
            // 1. Switch to the iframe with ID "myIframe"
            FrameLocator rankingFrame = page.frameLocator("#myIframe");

            // 2. Search for the element #infopop within this frame
            // We use .first() in case the element appears multiple times in the DOM
            Locator infoPopLocator = rankingFrame.locator("#infopop").first();

            // 3. Extract text (Playwright automatically waits for the iframe to load here)
            String rawText = infoPopLocator.textContent();

            if (rawText == null || rawText.isBlank()) {
                log().warn("BadmintonDeTimestampParser :: Element #infopop im Iframe gefunden, aber leer.");
                return Optional.empty();
            }

            String cleanDateStr = rawText.trim();
            log().info("BadmintonDeTimestampParser :: Zeitstempel im Iframe gefunden: {}", cleanDateStr);

            return parseDateTime(cleanDateStr);

        } catch (com.microsoft.playwright.TimeoutError e) {
            log().error("BadmintonDeTimestampParser :: Timeout beim Warten auf den Iframe oder #infopop.");
            return Optional.empty();
        } catch (Exception e) {
            log().error("BadmintonDeTimestampParser :: Unerwarteter Fehler beim Parsen", e);
            return Optional.empty();
        }
    }

    private Optional<LocalDateTime> parseDateTime(String dateString) {
        try {
            return Optional.of(LocalDateTime.parse(dateString, FORMATTER));
        } catch (DateTimeParseException e) {
            log().warn("Failed to parse timestamp: {}", dateString);
            return Optional.empty();
        }
    }
}
