package de.ostfale.va.framework.out.web;

import de.ostfale.va.application.port.out.ranking.ForRankingFileDownloadConfig;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.common.UseTimeHandling;
import de.ostfale.va.framework.out.filesystem.ApplicationDirectoryConfiguration;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;

@Component
public class HtmlUnitRankingDownloadAdapter implements ForRankingFileDownloadConfig, UseFileSystemHandling, UseTimeHandling, UseLogging {

    private static final String RANKING_FILE_NAME = "Ranking_";
    private static final String RANKING_FILE_SUFFIX = ".xlsx";
    private static final String RANKING_FILE_NAME_CW = "_KW";


    private final ObjectProvider<WebClient> clientProvider;
    private final BadmintonDeTimestampParser timestampParser;

    public HtmlUnitRankingDownloadAdapter(ObjectProvider<WebClient> clientProvider, BadmintonDeTimestampParser timestampParser) {
        this.clientProvider = clientProvider;
        this.timestampParser = timestampParser;
    }

    @Override
    public Optional<LocalDateTime> getLatestRemoteTimestamp(String url) {

        try (WebClient webClient = clientProvider.getIfAvailable()) {
            // Configure WebClient to handle JavaScript errors gracefully
            Objects.requireNonNull(webClient, "WebClient must not be null").getOptions().setJavaScriptEnabled(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setUseInsecureSSL(true);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

            HtmlPage page = Objects.requireNonNull(webClient, "WebClient must not be null").getPage(URI.create(url).toURL());
            return timestampParser.parseLastUpdate(page);
        } catch (Exception e) {
            log().error("HtmlUnitRankingAdapter :: Could not fetch remote timestamp: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public boolean downloadRankingFile(String sourceUrl, Path targetPath) {
        try {
            URL url = URI.create(sourceUrl).toURL();
            downloadIntoFile(url, targetPath);
            log().info("HtmlUnitRankingAdapter :: finished download: {}", targetPath.getFileName());
            return true;
        } catch (IOException e) {
            log().error("HtmlUnitRankingAdapter :: Failure downloading ranking file", e);
            return false;
        }
    }

    @Override
    public boolean downloadRankingFileIfNewer(String sourceUrl, Path targetPath) {
        log().debug("HtmlUnitRankingAdapter :: downloadRankingFileIfNewer: sourceUrl={}, targetPath={}", sourceUrl, targetPath);
        Optional<LocalDateTime> remoteFileTime = getLatestRemoteTimestamp(DBV_RANKING_URL);

        String destinationPath = prepareDownloadTargetPath(ApplicationDirectoryConfiguration.RANKING_DIR_NAME);
        String targetFileName = prepareDownloadFileName(LocalDateTime.now());
        Path rankingFilePath = Path.of(destinationPath + targetFileName);
        LocalDateTime localFileTime = getFileTimestamp(rankingFilePath);


        if (remoteFileTime.isPresent() && remoteFileTime.get().isAfter(localFileTime)) {
            log().info("UpdateRankingService :: New ranking available: Remote: {} / Lokal: {}). Starte Download...", remoteFileTime.get(), localFileTime);
            return downloadRankingFile(sourceUrl, rankingFilePath);
        } else {
            log().debug("UpdateRankingService :: There is no newer ranking available.");
            return false;
        }
    }

    @Override
    public String prepareDownloadTargetPath(String appDirName) {
        return getApplicationHomeDir() + SEPARATOR + appDirName + SEPARATOR;
    }

    @Override
    public String prepareDownloadFileName(LocalDateTime downloadDateTime) {
        log().debug("HtmlUnitRankingAdapter :: prepareDownloadFileName:  downloadDateTime={}", downloadDateTime);
        var calendarWeek = downloadDateTime.get(DEFAULT_WEEK_FIELDS.weekOfWeekBasedYear());
        var calendarYear = downloadDateTime.getYear();
        String rFileName = RANKING_FILE_NAME + calendarYear + RANKING_FILE_NAME_CW + calendarWeek + RANKING_FILE_SUFFIX;
        log().debug("HtmlUnitRankingAdapter :: Prepared download file name: {}", rFileName);
        return rFileName;
    }

    private void downloadIntoFile(URL url, Path targetPath) throws IOException {
        try (ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(targetPath.toFile());
             FileChannel fileChannel = fileOutputStream.getChannel()) {
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
    }

    private LocalDateTime getFileTimestamp(Path path) {
        if (Files.notExists(path)) {
            return LocalDateTime.MIN;
        }
        try {
            return LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneId.systemDefault());
        } catch (IOException e) {
            log().error("UpdateRankingService :: Failure retrieving timestamp for {}", path, e);
            return LocalDateTime.MIN;
        }
    }
}
