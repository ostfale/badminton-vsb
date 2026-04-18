package de.ostfale.va.framework.in.ui.playerranking;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.ChartBuilder;
import com.github.appreciated.apexcharts.config.builder.LegendBuilder;
import com.github.appreciated.apexcharts.config.builder.MarkersBuilder;
import com.github.appreciated.apexcharts.config.builder.NoDataBuilder;
import com.github.appreciated.apexcharts.config.builder.StrokeBuilder;
import com.github.appreciated.apexcharts.config.builder.XAxisBuilder;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.xaxis.TickPlacement;
import com.github.appreciated.apexcharts.config.xaxis.builder.TitleBuilder;
import com.github.appreciated.apexcharts.helper.Coordinate;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.ostfale.va.application.domain.model.playerrankings.DisciplineType;
import de.ostfale.va.application.domain.model.playerrankings.HistoryStatistics;
import de.ostfale.va.application.domain.model.playerrankings.HistoryTimestamp;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.RankingSnapshot;
import de.ostfale.va.common.UseLogging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerHistoryChartComponent implements UseLogging {

    private enum MetricType {
        POINTS("Punkte"),
        TOURNAMENTS("Turniere"),
        AGE_RANKING("Altersrang");

        private final String label;

        MetricType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private final VerticalLayout rootContainer;
    private final ApexCharts lineChart;
    private final ComboBox<MetricType> metricSelect;

    private Player currentPlayer;

    public PlayerHistoryChartComponent() {
        this.lineChart = initChart();
        this.metricSelect = initMetricSelect();
        this.rootContainer = initLayout(metricSelect, lineChart);
    }

    public void updatePlayer(Player player) {
        this.currentPlayer = player;
        updateChart();
    }

    public void clear() {
        this.currentPlayer = null;
        resetChartData();
    }

    private ApexCharts initChart() {
        var chart = ApexChartsBuilder.get()
                .withChart(ChartBuilder.get().withType(Type.LINE).withHeight("340").build())
                .withStroke(StrokeBuilder.get().withCurve(Curve.STEPLINE).withWidth(1.5).build())
                .withColors("#2563eb", "#16a34a", "#f59e0b")
                .withMarkers(MarkersBuilder.get().withSize(3.0, 3.0).build())
                .withNoData(NoDataBuilder.get().withText("Keine Verlaufsdaten verfuegbar").build())
                .withXaxis(XAxisBuilder.get()
                        .withTitle(TitleBuilder.get().withText("Kalenderwoche").build())
                        .withCategories(List.of())
                        .withTickPlacement(TickPlacement.BETWEEN)
                        .build())
                .withLegend(LegendBuilder.get()
                        .withShow(true)
                        .withShowForSingleSeries(true)
                        .build())
                .withSeries(
                        new Series<>(DisciplineType.SINGLE.getDisplayString(), new Number[0]),
                        new Series<>(DisciplineType.DOUBLE.getDisplayString(), new Number[0]),
                        new Series<>(DisciplineType.MIXED.getDisplayString(), new Number[0]))
                .build();

        chart.setWidthFull();
        chart.setHeight("340px");
        chart.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)");
        chart.getStyle().set("border-radius", "4px");
        chart.getStyle().set("background", "var(--lumo-contrast-5pct)");
        return chart;
    }

    private VerticalLayout initLayout(ComboBox<MetricType> metricSelect, ApexCharts lineChart) {
        HorizontalLayout controls = new HorizontalLayout(metricSelect);
        controls.setPadding(false);
        controls.setSpacing(true);
        controls.setWidthFull();

        var verticalLayout = new VerticalLayout();
        verticalLayout.setPadding(false);
        verticalLayout.setSpacing(false);
        verticalLayout.add(controls, lineChart);
        return verticalLayout;
    }

    private ComboBox<MetricType> initMetricSelect() {
        var comboBox = new ComboBox<MetricType>("Metrik");
        comboBox.setItems(MetricType.POINTS, MetricType.TOURNAMENTS, MetricType.AGE_RANKING);
        comboBox.setItemLabelGenerator(MetricType::getLabel);
        comboBox.setValue(MetricType.POINTS);
        comboBox.setWidth("220px");
        comboBox.addValueChangeListener(event -> updateChart());
        return comboBox;
    }

    private void resetChartData() {
        lineChart.setSeries(
                new Series<>(DisciplineType.SINGLE.getDisplayString(), new Number[0]),
                new Series<>(DisciplineType.DOUBLE.getDisplayString(), new Number[0]),
                new Series<>(DisciplineType.MIXED.getDisplayString(), new Number[0]));
        lineChart.render();
    }

    private void updateChart() {
        if (currentPlayer == null || currentPlayer.getHistory().isEmpty()) {
            resetChartData();
            return;
        }

        MetricType selectedMetric = metricSelect.getValue();
        List<Map.Entry<HistoryTimestamp, HistoryStatistics>> sortedHistory = currentPlayer.getHistory().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList();

        List<Object> singleSeries = new ArrayList<>();
        List<Object> doubleSeries = new ArrayList<>();
        List<Object> mixedSeries = new ArrayList<>();

        for (Map.Entry<HistoryTimestamp, HistoryStatistics> entry : sortedHistory) {
            String week = entry.getKey().cwyear();
            HistoryStatistics stats = entry.getValue();

            appendPoint(singleSeries, week, metricValue(stats.getSingleStatistics(), selectedMetric));
            appendPoint(doubleSeries, week, metricValue(stats.getDoubleStatistics(), selectedMetric));
            appendPoint(mixedSeries, week, metricValue(stats.getMixedStatistics(), selectedMetric));
        }

        if (singleSeries.isEmpty() && doubleSeries.isEmpty() && mixedSeries.isEmpty()) {
            resetChartData();
            return;
        }

        lineChart.updateSeries(
                new Series<>(DisciplineType.SINGLE.getDisplayString(), singleSeries.toArray()),
                new Series<>(DisciplineType.DOUBLE.getDisplayString(), doubleSeries.toArray()),
                new Series<>(DisciplineType.MIXED.getDisplayString(), mixedSeries.toArray()));
        log().debug("PlayerHistoryChartComponent :: updateChart to metric: {}", selectedMetric);
    }

    private void appendPoint(List<Object> targetSeries, String week, Integer value) {
        if (value != null) {
            targetSeries.add(new Coordinate<>(week, value));
        }
    }

    private Integer metricValue(RankingSnapshot snapshot, MetricType metricType) {
        if (snapshot == null || metricType == null) {
            return null;
        }
        return switch (metricType) {
            case POINTS -> snapshot.points();
            case TOURNAMENTS -> snapshot.tournaments();
            case AGE_RANKING -> snapshot.ageRanking();
        };
    }

    public Component getComponent() {
        return rootContainer;
    }
}
