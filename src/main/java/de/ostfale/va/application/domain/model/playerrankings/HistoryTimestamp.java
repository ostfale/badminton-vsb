package de.ostfale.va.application.domain.model.playerrankings;

import de.ostfale.va.common.UseLogging;
import org.jspecify.annotations.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record HistoryTimestamp(
        String cwyear
) implements Comparable<HistoryTimestamp> , UseLogging {

    private static final Pattern INPUT_PATTERN = Pattern.compile("^Ranking_(\\d{4})_KW(\\d{1,2})\\.xlsx$");

    public HistoryTimestamp {
        if (cwyear == null) {
            throw new IllegalArgumentException("input must not be null");
        }

        Matcher matcher = INPUT_PATTERN.matcher(cwyear);
        if (!matcher.matches()) {
            log().error("HistoryTimestamp :: Invalid input format: {}", cwyear);
            throw new IllegalArgumentException("input must match format Ranking_YYYY_KWd.xlsx or Ranking_YYYY_KWdd.xlsx, e.g. Ranking_2026_KW5.xlsx");
        }

        int calendarWeek = Integer.parseInt(matcher.group(2));
        if (calendarWeek < 1 || calendarWeek > 53) {
            throw new IllegalArgumentException("calendar week must be between 01 and 53");
        }

        String yearLastTwoDigits = matcher.group(1).substring(2, 4);
        cwyear = String.format("%02d_%s", calendarWeek, yearLastTwoDigits);
    }

    public int calendarWeek() {
        return Integer.parseInt(cwyear.substring(0, 2));
    }

    public int twoDigitYear() {
        return Integer.parseInt(cwyear.substring(3, 5));
    }

    @Override
    public int compareTo(HistoryTimestamp other) {
        int yearCompare = Integer.compare(this.twoDigitYear(), other.twoDigitYear());
        if (yearCompare != 0) {
            return yearCompare;
        }
        return Integer.compare(this.calendarWeek(), other.calendarWeek());
    }

    @Override
    public @NonNull String toString() {
        return "Jahr: " + twoDigitYear() + " KW: " + calendarWeek();
    }
}
