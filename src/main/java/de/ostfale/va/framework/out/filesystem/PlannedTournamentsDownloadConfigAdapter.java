package de.ostfale.va.framework.out.filesystem;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentDownloadTask;
import de.ostfale.va.application.port.out.ForPlannedTournamentsDownloadConfig;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.common.UseTimeHandling;
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
    String TOURNAMENT_DATE_TIME_FILE_PATTERN = "yyyy-MM-dd-HH-mm";


    @Override
    public List<PlannedTournamentDownloadTask> getDownloadTasks() {
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

    @Override
    public String prepareDownloadFileName(String year, LocalDateTime downloadDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TOURNAMENT_DATE_TIME_FILE_PATTERN);
        String dateTimeFormatted = downloadDateTime.format(dateTimeFormatter);
        var fileName = "%s%s%s%s%s%s".formatted(FILE_NAME, DATE_SEPARATOR, year, DATE_SEPARATOR, dateTimeFormatted, FILE_SUFFIX);
        log().debug("PlannedTournamentsDownloadConfigAdapter :: Prepared download file name: {}", fileName);
        return fileName;
    }

    @Override
    public String readDateTimeFromFileName(String fileName) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TOURNAMENT_DATE_TIME_FILE_PATTERN);

        // Match the datetime pattern: YYYY-MM-DD-HH-MM
        Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2}-\\d{2}-\\d{2})");
        Matcher matcher = pattern.matcher(fileName);

        if (matcher.find()) {
            var dateTime= LocalDateTime.parse(matcher.group(1), dateTimeFormatter);
            return getProvidedDateTimeFormatted(dateTime);
        }
        throw new IllegalArgumentException("Cannot parse datetime from filename: " + fileName);
    }

    @Override
    public String prepareDownloadTargetPath(String appDirName) {
        return getApplicationHomeDir() + SEPARATOR + appDirName + SEPARATOR;
    }

    @Override
    public String prepareDownloadUrl(String year) {
        return String.format("%s%s%s", TOURNAMENT_DOWNLOAD_URL_PREFIX, year, TOURNAMENT_DOWNLOAD_SEARCH_PARAM);
    }

    private PlannedTournamentDownloadTask createDownloadTask(String url, String destination) {
        return new PlannedTournamentDownloadTask(url, Path.of(destination));
    }
}
