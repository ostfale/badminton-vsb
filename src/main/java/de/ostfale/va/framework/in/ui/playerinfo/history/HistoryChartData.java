package de.ostfale.va.framework.in.ui.playerinfo.history;

import java.util.List;

public record HistoryChartData(
        List<String> categories,
        DisciplineSeries ranking,
        DisciplineSeries points,
        DisciplineSeries tournaments
) {
    public static HistoryChartData empty() {
        return new HistoryChartData(
                List.of(),
                DisciplineSeries.empty(),
                DisciplineSeries.empty(),
                DisciplineSeries.empty()
        );
    }

    public boolean isEmpty() {
        return categories.isEmpty();
    }

    public record DisciplineSeries(
            List<Integer> single,
            List<Integer> doubles,
            List<Integer> mixed
    ) {
        public static DisciplineSeries empty() {
            return new DisciplineSeries(List.of(), List.of(), List.of());
        }
    }
}
