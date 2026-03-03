package de.ostfale.va.framework.out.web;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import de.ostfale.va.application.domain.model.download.DownloadTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Ranking File Download Config Adapter Tests")
@ExtendWith(MockitoExtension.class)
class RankingFileDownloadConfigAdapterTest {

    @TempDir
    Path tempDir;
    private RankingFileDownloadConfigAdapter sut;
    @Mock
    private ObjectProvider<Browser> browserProvider;
    @Mock
    private Browser browser;
    @Mock
    private BrowserContext browserContext;
    @Mock
    private Page page;
    @Mock
    private BadmintonDeTimestampParser timestampParser;

    @BeforeEach
    void setUp() {
        sut = new RankingFileDownloadConfigAdapter(browserProvider, timestampParser);
    }

    @Test
    @DisplayName("Should return empty list when no newer ranking is available")
    void shouldReturnEmptyListWhenNoNewerRankingAvailable() {
        // Given - remote timestamp is older than LocalDateTime.MIN (impossible scenario)
        when(browserProvider.getIfAvailable()).thenReturn(browser);
        when(browser.newContext()).thenReturn(browserContext);
        when(browserContext.newPage()).thenReturn(page);
        when(timestampParser.parseLastUpdate(page)).thenReturn(Optional.of(LocalDateTime.MIN));

        // When
        List<DownloadTask> result = sut.getDownloadTasks();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return download task when newer ranking is available online")
    void shouldReturnDownloadTaskWhenNewerRankingAvailable() {
        // Given
        when(browserProvider.getIfAvailable()).thenReturn(browser);
        when(browser.newContext()).thenReturn(browserContext);
        when(browserContext.newPage()).thenReturn(page);
        when(timestampParser.parseLastUpdate(page)).thenReturn(Optional.of(LocalDateTime.now().plusDays(1)));

        // When
        List<DownloadTask> result = sut.getDownloadTasks();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).url()).contains("https://turniere.badminton.de/ranking/download");
        assertThat(result.get(0).destination()).isNotNull();
    }

    @Test
    @DisplayName("Should return empty list when remote timestamp is not available")
    void shouldReturnEmptyListWhenRemoteTimestampNotAvailable() {
        // Given
        when(browserProvider.getIfAvailable()).thenReturn(browser);
        when(browser.newContext()).thenReturn(browserContext);
        when(browserContext.newPage()).thenReturn(page);
        when(timestampParser.parseLastUpdate(page)).thenReturn(Optional.empty());

        // When
        List<DownloadTask> result = sut.getDownloadTasks();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list when Browser throws exception")
    void shouldReturnEmptyListWhenBrowserThrowsException() {
        // Given
        when(browserProvider.getIfAvailable()).thenReturn(browser);
        when(browser.newContext()).thenReturn(browserContext);
        when(browserContext.newPage()).thenReturn(page);
        when(page.navigate(any(String.class))).thenThrow(new RuntimeException("Connection failed"));

        // When
        List<DownloadTask> result = sut.getDownloadTasks();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should compare file timestamp with remote timestamp")
    void shouldCompareFileTimestampWithRemoteTimestamp() {
        // Given - use a future timestamp to ensure it's newer than any existing file
        LocalDateTime remoteTime = LocalDateTime.now().plusDays(1);
        when(browserProvider.getIfAvailable()).thenReturn(browser);
        when(browser.newContext()).thenReturn(browserContext);
        when(browserContext.newPage()).thenReturn(page);
        when(timestampParser.parseLastUpdate(page)).thenReturn(Optional.of(remoteTime));

        // When
        List<DownloadTask> result = sut.getDownloadTasks();

        // Then
        verify(timestampParser).parseLastUpdate(page);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should generate correct file name with calendar week and year")
    void shouldGenerateCorrectFileNameWithCalendarWeekAndYear() {
        // Given
        when(browserProvider.getIfAvailable()).thenReturn(browser);
        when(browser.newContext()).thenReturn(browserContext);
        when(browserContext.newPage()).thenReturn(page);
        when(timestampParser.parseLastUpdate(page)).thenReturn(Optional.of(LocalDateTime.now().plusDays(1)));

        // When
        List<DownloadTask> result = sut.getDownloadTasks();

        // Then
        assertThat(result).hasSize(1);
        String fileName = result.get(0).destination().getFileName().toString();
        assertThat(fileName).startsWith("Ranking_");
        assertThat(fileName).contains("_KW");
        assertThat(fileName).endsWith(".xlsx");
    }
}
