package de.ostfale.va.framework.out.web;

import de.ostfale.va.common.UseLogging;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlPage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Component
public class BadmintonDeTimestampParser implements UseLogging {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public Optional<LocalDateTime> parseLastUpdate(HtmlPage page) {
        HtmlAnchor infoAnchor = page.getHtmlElementById("infopop");
        if (infoAnchor != null) {
            String dateString = infoAnchor.asNormalizedText().trim();
            return parseDateTime(dateString);
        }
        return Optional.empty();
    }

    private Optional<LocalDateTime> parseDateTime(String dateString) {
        try {
            return Optional.of(LocalDateTime.parse(dateString, FORMATTER));
        } catch (DateTimeParseException e) {
            log().warn("Failed to parse timestamp: {}", dateString, e);
            return Optional.empty();
        }
    }
}
