package de.ostfale.va.framework.in.ui.playerinfo;

import de.ostfale.va.application.domain.model.playerrankings.HistoryStatistics;
import de.ostfale.va.application.domain.model.playerrankings.HistoryTimestamp;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.RankingSnapshot;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class HistoryChartDataMapper {

    private static final WeekFields ISO_WEEK_FIELDS = WeekFields.ISO;

    private final DateTimeFormatter monthLabelFormatter;

    HistoryChartDataMapper(Locale locale) {
        this.monthLabelFormatter = DateTimeFormatter.ofPattern("MMM yy", locale);
    }

    HistoryChartData map(Player player) {
        if (player == null || player.getHistory().isEmpty()) {
            return HistoryChartData.empty();
        }

        List<Map.Entry<HistoryTimestamp, HistoryStatistics>> sortedHistory = player.getHistory().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList();

        List<String> categories = buildMonthlyMarkerCategories(sortedHistory);
        HistoryChartData.DisciplineSeries rankingSeries = new HistoryChartData.DisciplineSeries(
                new ArrayList<>(sortedHistory.size()),
                new ArrayList<>(sortedHistory.size()),
                new ArrayList<>(sortedHistory.size())
        );
        HistoryChartData.DisciplineSeries pointsSeries = new HistoryChartData.DisciplineSeries(
                new ArrayList<>(sortedHistory.size()),
                new ArrayList<>(sortedHistory.size()),
                new ArrayList<>(sortedHistory.size())
        );
        HistoryChartData.DisciplineSeries tournamentsSeries = new HistoryChartData.DisciplineSeries(
                new ArrayList<>(sortedHistory.size()),
                new ArrayList<>(sortedHistory.size()),
                new ArrayList<>(sortedHistory.size())
        );

        for (Map.Entry<HistoryTimestamp, HistoryStatistics> entry : sortedHistory) {
            HistoryStatistics stats = entry.getValue();

            rankingSeries.single().add(readAgeRanking(stats.getSingleStatistics()));
            rankingSeries.doubles().add(readAgeRanking(stats.getDoubleStatistics()));
            rankingSeries.mixed().add(readAgeRanking(stats.getMixedStatistics()));

            pointsSeries.single().add(readPoints(stats.getSingleStatistics()));
            pointsSeries.doubles().add(readPoints(stats.getDoubleStatistics()));
            pointsSeries.mixed().add(readPoints(stats.getMixedStatistics()));

            tournamentsSeries.single().add(readTournaments(stats.getSingleStatistics()));
            tournamentsSeries.doubles().add(readTournaments(stats.getDoubleStatistics()));
            tournamentsSeries.mixed().add(readTournaments(stats.getMixedStatistics()));
        }

        return new HistoryChartData(categories, rankingSeries, pointsSeries, tournamentsSeries);
    }

    private List<String> buildMonthlyMarkerCategories(List<Map.Entry<HistoryTimestamp, HistoryStatistics>> sortedHistory) {
        List<String> categories = new ArrayList<>(sortedHistory.size());
        String previousMonthLabel = null;
        for (Map.Entry<HistoryTimestamp, HistoryStatistics> entry : sortedHistory) {
            String currentMonthLabel = toMonthLabel(entry.getKey());
            if (!currentMonthLabel.equals(previousMonthLabel)) {
                categories.add(currentMonthLabel);
                previousMonthLabel = currentMonthLabel;
            } else {
                categories.add("");
            }
        }
        return categories;
    }

    private String toMonthLabel(HistoryTimestamp historyTimestamp) {
        int year = 2000 + historyTimestamp.twoDigitYear();
        int week = historyTimestamp.calendarWeek();
        LocalDate weekDate = LocalDate.of(year, 1, 4)
                .with(ISO_WEEK_FIELDS.weekOfWeekBasedYear(), week)
                .with(ISO_WEEK_FIELDS.dayOfWeek(), DayOfWeek.MONDAY.getValue());
        return weekDate.format(monthLabelFormatter);
    }

    private Integer readAgeRanking(RankingSnapshot snapshot) {
        return snapshot == null ? null : snapshot.ageRanking();
    }

    private Integer readPoints(RankingSnapshot snapshot) {
        return snapshot == null ? null : snapshot.points();
    }

    private Integer readTournaments(RankingSnapshot snapshot) {
        return snapshot == null ? null : snapshot.tournaments();
    }
}
