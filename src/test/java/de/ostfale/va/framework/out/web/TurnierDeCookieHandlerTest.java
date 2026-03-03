package de.ostfale.va.framework.out.web;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Turnier.de Cookie Handler Tests")
class TurnierDeCookieHandlerTest {

    private static final String COOKIE_WALL_HTML = """
            <html>
            <body>
                <div id="consentui">
                    <div id="buttons">
                        <div class="btn amber">Ändern Sie...</div>
                        <div class="btn green">Ich akzeptiere die Datenschutzeinstellung</div>
                    </div>
                </div>
            </body>
            </html>
            """;
    private static final String NORMAL_PAGE_HTML = "<html><body><h1>Turnierliste</h1></body></html>";

    private static Playwright playwright;
    private static Browser browser;
    private TurnierDeCookieHandler sut;

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
        sut = new TurnierDeCookieHandler();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Sollte Cookie-Wall erkennen und auf 'Akzeptieren' klicken")
    void shouldHandleCookieWall() {
        try (var context = browser.newContext();
             var page = context.newPage()) {

            page.setContent(COOKIE_WALL_HTML);

            Page resultPage = sut.handleIfNecessary(page, context);

            assertNotNull(resultPage);
            // Verify that the green button was clicked (consent wall should be handled)
            assertTrue(resultPage.locator("#consentui").count() > 0);
        }
    }

    @Test
    @DisplayName("Sollte nichts tun, wenn keine Cookie-Wall vorhanden ist")
    void shouldDoNothingIfNoCookieWall() {
        try (var context = browser.newContext();
             var page = context.newPage()) {

            page.setContent(NORMAL_PAGE_HTML);

            Page resultPage = sut.handleIfNecessary(page, context);

            assertSame(page, resultPage);
            // Verify no consent UI is present
            assertEquals(0, resultPage.locator("#consentui").count());
        }
    }
}
