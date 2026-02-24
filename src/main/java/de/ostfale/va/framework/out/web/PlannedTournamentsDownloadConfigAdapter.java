package de.ostfale.va.framework.out.web;

import de.ostfale.va.application.domain.model.download.DownloadTask;
import de.ostfale.va.application.port.out.plannedtournaments.ForPlannedTournamentsDownloadConfig;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.common.UseTimeHandling;
import de.ostfale.va.framework.out.filesystem.ApplicationDirectoryConfiguration;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PlannedTournamentsDownloadConfigAdapter implements ForPlannedTournamentsDownloadConfig, UseFileSystemHandling, UseTimeHandling, UseLogging {

    private static final String FILE_NAME = "Tournament";
    private static final String FILE_SUFFIX = ".csv";
    private static final String DATE_SEPARATOR = "_";
    private static final String TOURNAMENT_DATE_TIME_FILE_PATTERN = "yyyy-MM-dd-HH-mm";


    private static final String TOURNAMENT_DOWNLOAD_URL_PREFIX = "https://turniere.badminton.de/download?name=&year=";
    private static final String TOURNAMENT_DOWNLOAD_SEARCH_PARAM = "&remaining=all&colortype=&jws=0&form%5Bsearch%5D=1&federation%5B%5D=70&federation%5B%5D" +
            "=89&federation%5B%5D=90&federation%5B%5D=91&federation%5B%5D=92&federation%5B%5D=71&federation%5B%5D=72&federation%5B%5D" +
            "=77&federation%5B%5D=79&federation%5B%5D=80&federation%5B%5D=81&federation%5B%5D=78&federation%5B%5D=74&federation%5B%5D" +
            "=85&federation%5B%5D=76&federation%5B%5D=73&federation%5B%5D=83&federation%5B%5D=82&federation%5B%5D=84&federation%5B%5D" +
            "=86&federation%5B%5D=87&federation%5B%5D=75&federation%5B%5D=88&agegroup%5B%5D=U9&agegroup%5B%5D=U11&agegroup%5B%5D=U13&agegroup%5B%5D" +
            "=U15&agegroup%5B%5D=U17&agegroup%5B%5D=U19&agegroup%5B%5D=U22&agegroup%5B%5D=O19&agegroup%5B%5D=O35&category%5B%5D=79&category%5B%5D" +
            "=80&category%5B%5D=81&category%5B%5D=82&category%5B%5D=83&category%5B%5D=107&category%5B%5D=106&category%5B%5D=104&category%5B%5D" +
            "=97&category%5B%5D=102&category%5B%5D=92&category%5B%5D=98&category%5B%5D=93&category%5B%5D=87&category%5B%5D=95&category%5B%5D" +
            "=91&category%5B%5D=88&category%5B%5D=103&category%5B%5D=94&category%5B%5D=84&category%5B%5D=86&category%5B%5D=100&category%5B%5D" +
            "=89&category%5B%5D=85&discipline%5B%5D=Einzel&discipline%5B%5D=Doppel&discipline%5B%5D=Mixed";


    @Override
    public List<DownloadTask> getDownloadTasks() {
        log().debug("PlannedTournamentsDownloadConfigAdapter :: Preparing download tasks");
        var timestamp = LocalDateTime.now();
        String currentYear = String.valueOf(getCurrentCalendarYear());
        String nextYear = String.valueOf(getNextCalendarYear());

        String fileNameThisYear = prepareDownloadFileName(currentYear, timestamp);
        String fileNameNextYear = prepareDownloadFileName(nextYear, timestamp);

        String destinationPath = prepareDownloadTargetPath(ApplicationDirectoryConfiguration.TOURNAMENT_DIR_NAME);

        String downloadUrlThisYear = prepareDownloadUrl(currentYear);
        String downloadUrlNextYear = prepareDownloadUrl(nextYear);

        var downloadTaskThisYear = createDownloadTask(downloadUrlThisYear, destinationPath + fileNameThisYear);
        var downloadTaskNextYear = createDownloadTask(downloadUrlNextYear, destinationPath + fileNameNextYear);

        return List.of(downloadTaskThisYear, downloadTaskNextYear);
    }

    private String prepareDownloadFileName(String year, LocalDateTime downloadDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TOURNAMENT_DATE_TIME_FILE_PATTERN);
        String dateTimeFormatted = downloadDateTime.format(dateTimeFormatter);
        var fileName = "%s%s%s%s%s%s".formatted(FILE_NAME, DATE_SEPARATOR, year, DATE_SEPARATOR, dateTimeFormatted, FILE_SUFFIX);
        log().debug("PlannedTournamentsDownloadConfigAdapter :: Prepared download file name: {}", fileName);
        return fileName;
    }

    private String prepareDownloadTargetPath(String appDirName) {
        return getApplicationHomeDir() + SEPARATOR + appDirName + SEPARATOR;
    }

    private String prepareDownloadUrl(String year) {
        return String.format("%s%s%s", TOURNAMENT_DOWNLOAD_URL_PREFIX, year, TOURNAMENT_DOWNLOAD_SEARCH_PARAM);
    }

    private DownloadTask createDownloadTask(String url, String destination) {
        return new DownloadTask(url, Path.of(destination));
    }
}
