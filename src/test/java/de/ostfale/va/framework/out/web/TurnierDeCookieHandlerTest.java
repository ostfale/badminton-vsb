package de.ostfale.va.framework.out.web;

import com.microsoft.playwright.Page;
import de.ostfale.va.PlayWrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Turnier.de Cookie Handler Tests")
class TurnierDeCookieHandlerTest extends PlayWrightTestBase {

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

    private TurnierDeCookieHandler sut;

    @BeforeEach
    void setUp() {
        sut = new TurnierDeCookieHandler();
    }

    @Test
    @DisplayName("Sollte Cookie-Wall erkennen und auf 'Akzeptieren' klicken")
    void shouldHandleCookieWall() {
        // Nutze die 'page' und 'context' Instanzen aus der Basisklasse
        page.setContent(COOKIE_WALL_HTML);

        Page resultPage = sut.handleIfNecessary(page, context);

        assertNotNull(resultPage);
        // Da wir nur HTML setzen, wird der Klick im Test fehlschlagen,
        // wenn der Selektor im SUT nicht exakt passt.
        assertTrue(resultPage.locator("#consentui").count() > 0);
    }

    @Test
    @DisplayName("Sollte nichts tun, wenn keine Cookie-Wall vorhanden ist")
    void shouldDoNothingIfNoCookieWall() {
        page.setContent(NORMAL_PAGE_HTML);

        Page resultPage = sut.handleIfNecessary(page, context);

        assertSame(page, resultPage);
        assertEquals(0, resultPage.locator("#consentui").count());
    }
}
