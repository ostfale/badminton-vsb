package de.ostfale.va.application.domain.service;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.port.in.ForProvidingPlannedTournamentStreams;
import de.ostfale.va.application.port.in.plannedtournaments.ForLoadingPlannedTournaments;
import de.ostfale.va.application.port.out.plannedtournaments.ForParsingPlannedTournaments;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.common.UseTimeHandling;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ImportPlannedTournamentsService implements ForLoadingPlannedTournaments, UseFileSystemHandling, UseTimeHandling, UseLogging {

    private static final String TOURNAMENT_DATE_TIME_FILE_PATTERN = "yyyy-MM-dd-HH-mm";


    private final ForParsingPlannedTournaments parser;
    private final ForProvidingPlannedTournamentStreams streamsProvider;

    public ImportPlannedTournamentsService(
            ForParsingPlannedTournaments parser,
            ForProvidingPlannedTournamentStreams streamsProvider) {
        this.parser = parser;
        this.streamsProvider = streamsProvider;
    }

    @Override
    public List<PlannedTournament> loadFromSource() {
        return streamsProvider.getPlannedTournamentStreams().stream()
                .flatMap(stream -> parser.parsePlannedTournaments(stream).stream())
                .toList();
    }

    @Override
    public String getLastDownloadDate() {
        var result = streamsProvider.getDownloadDateInFileName();
        if (result != null && !result.isEmpty()) {
            var dateTimeFromFile = readDateTimeFromFileName(result);
            log().debug("ImportPlannedTournamentsService :: Last download date from file: {}", dateTimeFromFile);
            return dateTimeFromFile;
        }
        log().warn("ImportPlannedTournamentsService :: Last download date not found");
        return "";
    }

    private String readDateTimeFromFileName(String fileName) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TOURNAMENT_DATE_TIME_FILE_PATTERN);

        // Match the datetime pattern: YYYY-MM-DD-HH-MM
        Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2}-\\d{2}-\\d{2})");
        Matcher matcher = pattern.matcher(fileName);

        if (matcher.find()) {
            var dateTime = LocalDateTime.parse(matcher.group(1), dateTimeFormatter);
            return getProvidedDateTimeFormatted(dateTime);
        }
        throw new IllegalArgumentException("Cannot parse datetime from filename: " + fileName);
    }
}
