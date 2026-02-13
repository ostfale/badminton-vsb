package de.ostfale.va.framework.out.web;

import org.htmlunit.MockWebConnection;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Turnier.de Cookie Handler Tests")
class TurnierDeCookieHandlerTest {

    private static final String TEST_BASE_URL = "https://www.turnier.de";
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
    private WebClient webClient;

    @BeforeEach
    void setUp() {
        sut = new TurnierDeCookieHandler();
        webClient = createConfiguredWebClient();
    }

    @Test
    @DisplayName("Sollte Cookie-Wall erkennen und auf 'Akzeptieren' klicken")
    void shouldHandleCookieWall() throws Exception {
        HtmlPage initialPage = setupMockPage("/cookiewall", COOKIE_WALL_HTML);

        HtmlPage resultPage = sut.handleIfNecessary(initialPage, webClient);

        assertNotNull(resultPage);
    }

    @Test
    @DisplayName("Sollte nichts tun, wenn keine Cookie-Wall vorhanden ist")
    void shouldDoNothingIfNoCookieWall() throws Exception {
        MockWebConnection connection = new MockWebConnection();
        HtmlPage page = setupMockPage("/tournaments", NORMAL_PAGE_HTML, connection);

        HtmlPage resultPage = sut.handleIfNecessary(page, webClient);

        assertEquals(page, resultPage);
        assertEquals(0, connection.getRequestCount() - 1);
    }

    private WebClient createConfiguredWebClient() {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setCssEnabled(false);
        return client;
    }

    private HtmlPage setupMockPage(String path, String htmlContent) throws Exception {
        return setupMockPage(path, htmlContent, new MockWebConnection());
    }

    private HtmlPage setupMockPage(String path, String htmlContent, MockWebConnection connection) throws Exception {
        URL url = URI.create(TEST_BASE_URL + path).toURL();
        connection.setResponse(url, htmlContent);
        webClient.setWebConnection(connection);
        return webClient.getPage(url);
    }
}
