package de.ostfale.va.framework.out.web;

import de.ostfale.va.application.domain.model.download.DownloadTask;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Ranking File Download Config Adapter Tests")
@ExtendWith(MockitoExtension.class)
class RankingFileDownloadConfigAdapterTest {

    private RankingFileDownloadConfigAdapter sut;

    @Mock
    private ObjectProvider<WebClient> clientProvider;

    @Mock
    private WebClient webClient;

    @Mock
    private BadmintonDeTimestampParser timestampParser;

    @Mock
    private HtmlPage htmlPage;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        sut = new RankingFileDownloadConfigAdapter(clientProvider, timestampParser);
    }

    @Test
    @DisplayName("Should return empty list when no newer ranking is available")
    void shouldReturnEmptyListWhenNoNewerRankingAvailable() throws Exception {
        // Given
        when(clientProvider.getIfAvailable()).thenReturn(webClient);
        when(webClient.getOptions()).thenReturn(new org.htmlunit.WebClientOptions());
        when(webClient.getPage(any(java.net.URL.class))).thenReturn(htmlPage);
        when(timestampParser.parseLastUpdate(htmlPage)).thenReturn(Optional.of(LocalDateTime.now().minusDays(1)));

        // When
        List<DownloadTask> result = sut.getDownloadTasks();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return download task when newer ranking is available online")
    void shouldReturnDownloadTaskWhenNewerRankingAvailable() throws Exception {
        // Given
        when(clientProvider.getIfAvailable()).thenReturn(webClient);
        when(webClient.getOptions()).thenReturn(new org.htmlunit.WebClientOptions());
        when(webClient.getPage(any(java.net.URL.class))).thenReturn(htmlPage);
        when(timestampParser.parseLastUpdate(htmlPage)).thenReturn(Optional.of(LocalDateTime.now().plusDays(1)));

        // When
        List<DownloadTask> result = sut.getDownloadTasks();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).url()).contains("https://turniere.badminton.de/ranking/download");
        assertThat(result.get(0).destination()).isNotNull();
    }

    @Test
    @DisplayName("Should return empty list when remote timestamp is not available")
    void shouldReturnEmptyListWhenRemoteTimestampNotAvailable() throws Exception {
        // Given
        when(clientProvider.getIfAvailable()).thenReturn(webClient);
        when(webClient.getOptions()).thenReturn(new org.htmlunit.WebClientOptions());
        when(webClient.getPage(any(java.net.URL.class))).thenReturn(htmlPage);
        when(timestampParser.parseLastUpdate(htmlPage)).thenReturn(Optional.empty());

        // When
        List<DownloadTask> result = sut.getDownloadTasks();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list when WebClient throws exception")
    void shouldReturnEmptyListWhenWebClientThrowsException() throws Exception {
        // Given
        when(clientProvider.getIfAvailable()).thenReturn(webClient);
        when(webClient.getOptions()).thenReturn(new org.htmlunit.WebClientOptions());
        when(webClient.getPage(any(java.net.URL.class))).thenThrow(new IOException("Connection failed"));

        // When
        List<DownloadTask> result = sut.getDownloadTasks();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should configure WebClient with correct options")
    void shouldConfigureWebClientWithCorrectOptions() throws Exception {
        // Given
        org.htmlunit.WebClientOptions options = new org.htmlunit.WebClientOptions();
        when(clientProvider.getIfAvailable()).thenReturn(webClient);
        when(webClient.getOptions()).thenReturn(options);
        when(webClient.getPage(any(java.net.URL.class))).thenReturn(htmlPage);
        when(timestampParser.parseLastUpdate(htmlPage)).thenReturn(Optional.of(LocalDateTime.now().plusDays(1)));

        // When
        sut.getDownloadTasks();

        // Then
        assertFalse(options.isJavaScriptEnabled());
        assertFalse(options.isCssEnabled());
        assertTrue(options.isUseInsecureSSL());
        assertFalse(options.isThrowExceptionOnFailingStatusCode());
    }

    @Test
    @DisplayName("Should compare file timestamp with remote timestamp")
    void shouldCompareFileTimestampWithRemoteTimestamp() throws Exception {
        // Given - use a future timestamp to ensure it's newer than any existing file
        LocalDateTime remoteTime = LocalDateTime.now().plusDays(1);
        when(clientProvider.getIfAvailable()).thenReturn(webClient);
        when(webClient.getOptions()).thenReturn(new org.htmlunit.WebClientOptions());
        when(webClient.getPage(any(java.net.URL.class))).thenReturn(htmlPage);
        when(timestampParser.parseLastUpdate(htmlPage)).thenReturn(Optional.of(remoteTime));

        // When
        List<DownloadTask> result = sut.getDownloadTasks();

        // Then
        verify(timestampParser).parseLastUpdate(htmlPage);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should generate correct file name with calendar week and year")
    void shouldGenerateCorrectFileNameWithCalendarWeekAndYear() throws Exception {
        // Given
        when(clientProvider.getIfAvailable()).thenReturn(webClient);
        when(webClient.getOptions()).thenReturn(new org.htmlunit.WebClientOptions());
        when(webClient.getPage(any(java.net.URL.class))).thenReturn(htmlPage);
        when(timestampParser.parseLastUpdate(htmlPage)).thenReturn(Optional.of(LocalDateTime.now().plusDays(1)));

        // When
        List<DownloadTask> result = sut.getDownloadTasks();

        // Then
        assertThat(result).hasSize(1);
        String fileName = result.get(0).destination().getFileName().toString();
        assertThat(fileName).startsWith("Ranking_");
        assertThat(fileName).contains("_KW");
        assertThat(fileName).endsWith(".xlsx");
    }

    @Test
    @DisplayName("Should close WebClient after use")
    void shouldCloseWebClientAfterUse() throws Exception {
        // Given
        when(clientProvider.getIfAvailable()).thenReturn(webClient);
        when(webClient.getOptions()).thenReturn(new org.htmlunit.WebClientOptions());
        when(webClient.getPage(any(java.net.URL.class))).thenReturn(htmlPage);
        when(timestampParser.parseLastUpdate(htmlPage)).thenReturn(Optional.of(LocalDateTime.now().plusDays(1)));

        // When
        sut.getDownloadTasks();

        // Then
        verify(webClient).close();
    }
}
