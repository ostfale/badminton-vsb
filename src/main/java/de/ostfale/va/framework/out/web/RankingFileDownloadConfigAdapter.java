package de.ostfale.va.framework.out.web;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import de.ostfale.va.application.domain.model.download.DownloadTask;
import de.ostfale.va.application.port.out.ranking.ForRankingFileDownloadConfig;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.common.UseTimeHandling;
import de.ostfale.va.framework.out.filesystem.ApplicationDirectoryConfiguration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class RankingFileDownloadConfigAdapter implements ForRankingFileDownloadConfig, UseFileSystemHandling, UseTimeHandling, UseLogging {

    private static final String RANKING_FILE_NAME = "Ranking_";
    private static final String RANKING_FILE_SUFFIX = ".xlsx";
    private static final String RANKING_FILE_NAME_CW = "_KW";
    private static final long BROWSER_READY_TIMEOUT_SECONDS = 20L;

    private final CompletableFuture<Browser> browserFuture;
    private final BadmintonDeTimestampParser timestampParser;

    String CURRENT_RANKING_FILE_URL = "https://turniere.badminton.de/ranking/download?save=1&gender=&gruppe=&lvname=&bezirk=&firstname=&lastname=&club=&colortype=";
    String DBV_RANKING_URL = "https://www.badminton.de/der-dbv/jugend-wettkampf/u19-ranglistentabellen/u19-rangliste/";

    public RankingFileDownloadConfigAdapter(CompletableFuture<Browser> browserFuture, BadmintonDeTimestampParser timestampParser) {
        this.browserFuture = browserFuture;
        this.timestampParser = timestampParser;
    }

    @Override
    public List<DownloadTask> getDownloadTasks() {
        String destination = prepareDownloadTargetPath(ApplicationDirectoryConfiguration.RANKING_DIR_NAME);
        Path destinationPath = Path.of(destination);
        if (!isThereNewerRankingAvailableOnline(destinationPath)) {
            log().info("RankingFileDownloadConfigAdapter :: No newer ranking available online. Download skipped.");
            return List.of();
        }
        DownloadTask downloadTask = new DownloadTask(CURRENT_RANKING_FILE_URL, destinationPath);
        return List.of(downloadTask);
    }

    private boolean isThereNewerRankingAvailableOnline(Path destinationPath) {
        var remoteTimestamp = getLatestRemoteTimestamp(DBV_RANKING_URL);
        if (remoteTimestamp.isPresent()) {
            log().debug("RankingFileDownloadConfigAdapter :: Remote timestamp: {}", remoteTimestamp.get());
            var webTime = remoteTimestamp.get();
            var fileTime = getFileTimestamp(destinationPath);
            return webTime.isAfter(fileTime);
        }
        return false;
    }

    private Optional<LocalDateTime> getLatestRemoteTimestamp(String url) {
        Browser browser = awaitBrowserReady();
        if (browser == null) {
            log().error("Browser not available");
            return Optional.empty();
        }

        BrowserContext context = browser.newContext();
        try (Page page = context.newPage()) {
            page.navigate(url);
            page.waitForLoadState();
            return timestampParser.parseLastUpdate(page);
        } catch (Exception e) {
            log().error("PlaywrightRankingAdapter :: Could not fetch remote timestamp: {}", e.getMessage());
            return Optional.empty();
        } finally {
            context.close();
        }
    }

    private Browser awaitBrowserReady() {
        try {
            return browserFuture.get(BROWSER_READY_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log().warn("Playwright browser still initializing after {} seconds", BROWSER_READY_TIMEOUT_SECONDS);
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log().warn("Interrupted while waiting for Playwright browser");
            return null;
        } catch (ExecutionException e) {
            log().error("Playwright browser initialization failed", e.getCause());
            return null;
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

    private String prepareDownloadTargetPath(String appDirName) {
        LocalDateTime downloadDateTime = LocalDateTime.now();
        var calendarWeek = downloadDateTime.get(DEFAULT_WEEK_FIELDS.weekOfWeekBasedYear());
        var calendarYear = downloadDateTime.getYear();
        String rFileName = RANKING_FILE_NAME + calendarYear + RANKING_FILE_NAME_CW + calendarWeek + RANKING_FILE_SUFFIX;

        String targetPath = getApplicationHomeDir() + SEPARATOR + appDirName + SEPARATOR + rFileName;
        log().debug("RankingFileDownloadConfigAdapter :: prepareDownloadTargetPath: targetPath={}", targetPath);
        return targetPath;
    }
}
