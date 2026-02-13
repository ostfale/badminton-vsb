package de.ostfale.va.framework.out.web;

import org.htmlunit.MockWebConnection;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Badminton.de Timestamp Parser Tests")
class BadmintonDeTimestampParserTest {
    private static final String TEST_URL = "https://www.badminton.de/u19-rangliste/";
    private static final int EXPECTED_YEAR = 2026;
    private static final int EXPECTED_MONTH = 2;
    private static final int EXPECTED_DAY = 11;
    private static final int EXPECTED_HOUR = 16;
    private static final int EXPECTED_MINUTE = 15;

    private BadmintonDeTimestampParser parser;
    private WebClient webClient;

    @BeforeEach
    void setUp() {
        parser = new BadmintonDeTimestampParser();
        webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(false);
    }

    @Test
    @DisplayName("Should parse timestamp from infopop")
    void shouldParseTimestampFromInfopop() throws Exception {
        HtmlPage page = createMockPage("""
                <html>
                <body>
                    <div class="col-lg-3">
                        zuletzt aktualisiert: <a id="infopop" href="#">
                            11.02.2026 16:15:00
                        </a> 
                    </div>
                </body>
                </html>
                """);

        Optional<LocalDateTime> result = parser.parseLastUpdate(page);

        assertAll("Check timestamp",
                () -> assertNotNull(result.get()),
                () -> assertEquals(EXPECTED_YEAR, result.get().getYear()),
                () -> assertEquals(EXPECTED_MONTH, result.get().getMonthValue()),
                () -> assertEquals(EXPECTED_DAY, result.get().getDayOfMonth()),
                () -> assertEquals(EXPECTED_HOUR, result.get().getHour()),
                () -> assertEquals(EXPECTED_MINUTE, result.get().getMinute())
        );

    }

    private HtmlPage createMockPage(String htmlContent) throws Exception {
        URL url = URI.create(TEST_URL).toURL();
        MockWebConnection connection = new MockWebConnection();
        connection.setResponse(url, htmlContent);
        webClient.setWebConnection(connection);
        return webClient.getPage(url);
    }
}
