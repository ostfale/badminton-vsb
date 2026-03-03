package de.ostfale.va.framework.out.web;

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
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("zuletzt aktualisiert:\\s*(\\d{2}\\.\\d{2}\\.\\d{4}\\s+\\d{2}:\\d{2}:\\d{2})");

    public Optional<LocalDateTime> parseLastUpdate(Page page) {
        String wholeText = page.textContent("body");
        Matcher matcher = TIMESTAMP_PATTERN.matcher(wholeText);

        if (matcher.find()) {
            String dateStr = matcher.group(1);
            log().info("BadmintonDeTimestampParser :: Timestamp found per regex: {}", dateStr);
            return parseDateTime(dateStr);
        }
        log().warn("BadmintonDeTimestampParser :: Timestamp not found in page text");
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
