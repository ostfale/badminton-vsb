package de.ostfale.va.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;

public interface UseTimeHandling {

    String TOURNAMENT_DATE_FILE_PATTERN = "yyyy-MM-dd";
    String TOURNAMENT_DATE_DISPLAY_PATTERN = "dd.MM.yyyy";

    DateTimeFormatter FILE_FORMATTER = DateTimeFormatter.ofPattern(TOURNAMENT_DATE_FILE_PATTERN);
    DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern(TOURNAMENT_DATE_DISPLAY_PATTERN);
    WeekFields DEFAULT_WEEK_FIELDS = WeekFields.of(Locale.getDefault());

    default int getCurrentCalendarWeek() {
        return LocalDate.now().get(DEFAULT_WEEK_FIELDS.weekOfWeekBasedYear());
    }

    default int getCalendarYearFromLastWeek() {
        return LocalDate.now().minusWeeks(1).get(DEFAULT_WEEK_FIELDS.weekBasedYear());
    }

    default int getCurrentCalendarYear() {
        return LocalDate.now().getYear();
    }

    default int getNextCalendarYear() {
        return LocalDate.now().plusYears(1).getYear();
    }

    default int getLastCalendarWeek() {
        return LocalDate.now().minusWeeks(1).get(DEFAULT_WEEK_FIELDS.weekOfWeekBasedYear());
    }

    default String formatDateToTournamentFormat(String date) {
        return LocalDate.parse(date, FILE_FORMATTER).format(DISPLAY_FORMATTER);
    }

    default LocalDate parseDateToTournamentFormat(String date) {
        return LocalDate.parse(date, DISPLAY_FORMATTER);
    }
}
