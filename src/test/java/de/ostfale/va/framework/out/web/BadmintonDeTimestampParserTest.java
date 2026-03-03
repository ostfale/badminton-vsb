package de.ostfale.va.framework.out.web;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Badminton.de Timestamp Parser Tests")
class BadmintonDeTimestampParserTest {
    private static final int EXPECTED_YEAR = 2026;
    private static final int EXPECTED_MONTH = 2;
    private static final int EXPECTED_DAY = 11;
    private static final int EXPECTED_HOUR = 16;
    private static final int EXPECTED_MINUTE = 15;

    private static Playwright playwright;
    private static Browser browser;
    private BadmintonDeTimestampParser parser;

    @BeforeAll
    static void setUpAll() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void tearDownAll() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    void setUp() {
        parser = new BadmintonDeTimestampParser();
    }

    @Test
    @DisplayName("Should parse timestamp from infopop")
    void shouldParseTimestampFromInfopop() {
        try (var context = browser.newContext(); 
             var page = context.newPage()) {
            
            page.setContent("""
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
                    () -> assertTrue(result.isPresent()),
                    () -> assertEquals(EXPECTED_YEAR, result.get().getYear()),
                    () -> assertEquals(EXPECTED_MONTH, result.get().getMonthValue()),
                    () -> assertEquals(EXPECTED_DAY, result.get().getDayOfMonth()),
                    () -> assertEquals(EXPECTED_HOUR, result.get().getHour()),
                    () -> assertEquals(EXPECTED_MINUTE, result.get().getMinute())
            );
        }
    }
}
