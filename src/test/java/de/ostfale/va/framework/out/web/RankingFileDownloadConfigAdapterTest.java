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

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Ranking File Download Config Adapter Tests")
@ExtendWith(MockitoExtension.class)
class RankingFileDownloadConfigAdapterTest {

    @TempDir
    Path tempDir;
    private RankingFileDownloadConfigAdapter sut;
    @Mock
    private CompletableFuture<Browser> browserFuture;
    @Mock
    private Browser browser;
    @Mock
    private BrowserContext browserContext;
    @Mock
    private Page page;
    @Mock
    private BadmintonDeTimestampParser timestampParser;

    @BeforeEach
    void setUp() throws Exception {
        when(browserFuture.get(anyLong(), any(TimeUnit.class))).thenReturn(browser);
        sut = new RankingFileDownloadConfigAdapter(browserFuture, timestampParser);
    }

    @Test
    @DisplayName("Should return empty list when no newer ranking is available")
    void shouldReturnEmptyListWhenNoNewerRankingAvailable() {
        // Given - remote timestamp is older than LocalDateTime.MIN (impossible scenario)
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
        when(browser.newContext()).thenReturn(browserContext);
        when(browserContext.newPage()).thenReturn(page);
        when(timestampParser.parseLastUpdate(page)).thenReturn(Optional.of(LocalDateTime.now().plusDays(1)));

        // When
        List<DownloadTask> result = sut.getDownloadTasks();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).allSatisfy(task -> {
            assertThat(task.url()).contains("https://turniere.badminton.de/ranking/download");
            assertThat(task.destination()).isNotNull();
        });
    }

    @Test
    @DisplayName("Should return empty list when remote timestamp is not available")
    void shouldReturnEmptyListWhenRemoteTimestampNotAvailable() {
        // Given
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
        when(browser.newContext()).thenReturn(browserContext);
        when(browserContext.newPage()).thenReturn(page);
        when(timestampParser.parseLastUpdate(page)).thenReturn(Optional.of(remoteTime));

        // When
        List<DownloadTask> result = sut.getDownloadTasks();

        // Then
        verify(timestampParser).parseLastUpdate(page);
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Should generate correct file name with calendar week and year")
    void shouldGenerateCorrectFileNameWithCalendarWeekAndYear() {
        // Given
        when(browser.newContext()).thenReturn(browserContext);
        when(browserContext.newPage()).thenReturn(page);
        when(timestampParser.parseLastUpdate(page)).thenReturn(Optional.of(LocalDateTime.now().plusDays(1)));

        // When
        List<DownloadTask> result = sut.getDownloadTasks();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).allSatisfy(task -> {
            String fileName = task.destination().getFileName().toString();
            assertThat(fileName).startsWith("Ranking_");
            assertThat(fileName).contains("_KW");
            assertThat(fileName).endsWith(".xlsx");
        });
    }
}
