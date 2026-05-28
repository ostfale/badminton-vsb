package de.ostfale.va.framework.in.ui.playerinfo.history;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.*;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.xaxis.TickPlacement;
import com.github.appreciated.apexcharts.config.xaxis.builder.TitleBuilder;
import com.github.appreciated.apexcharts.helper.Series;
import de.ostfale.va.application.domain.model.playerrankings.DisciplineType;

import java.util.List;

class HistoryChartFactory {

    private final String noDataText;

    HistoryChartFactory(String noDataText) {
        this.noDataText = noDataText;
    }

    ApexCharts createChart(String yAxisLabel) {
        return createChart(yAxisLabel, false);
    }

    ApexCharts createChart(String yAxisLabel, boolean reversed) {
        ApexChartsBuilder builder = ApexChartsBuilder.get()
                .withChart(ChartBuilder.get().withType(Type.LINE).withHeight("340").build())
                .withStroke(StrokeBuilder.get().withCurve(Curve.STEPLINE).withWidth(1.5).build())
                .withColors("#2563eb", "#16a34a", "#f59e0b")
                .withMarkers(MarkersBuilder.get().withSize(3.0, 3.0).build())
                .withNoData(NoDataBuilder.get().withText(noDataText).build())
                .withXaxis(XAxisBuilder.get()
                        .withTitle(TitleBuilder.get().withText("Monat (" + yAxisLabel + ")").build())
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
                        new Series<>(DisciplineType.MIXED.getDisplayString(), new Number[0]));

        if (reversed) {
            builder.withYaxis(YAxisBuilder.get()
                    .withReversed(true)
                    .withMin(1.0)
                    .build());
        }

        ApexCharts chart = builder.build();
        chart.setWidthFull();
        chart.setHeight("340px");
        chart.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)");
        chart.getStyle().set("border-radius", "4px");
        chart.getStyle().set("background", "var(--lumo-contrast-5pct)");
        return chart;
    }

    void clear(ApexCharts chart, String yAxisLabel) {
        chart.setXaxis(XAxisBuilder.get()
                .withTitle(TitleBuilder.get().withText("Monat (" + yAxisLabel + ")").build())
                .withCategories(List.of())
                .withTickPlacement(TickPlacement.BETWEEN)
                .build());
        chart.updateConfig();
        
        chart.updateSeries(
                new Series<>(DisciplineType.SINGLE.getDisplayString(), new Number[0]),
                new Series<>(DisciplineType.DOUBLE.getDisplayString(), new Number[0]),
                new Series<>(DisciplineType.MIXED.getDisplayString(), new Number[0]));
    }

    void apply(ApexCharts chart, String yAxisLabel, List<String> categories, HistoryChartData.DisciplineSeries series) {
        chart.setXaxis(XAxisBuilder.get()
                .withTitle(TitleBuilder.get().withText("Monat (" + yAxisLabel + ")").build())
                .withCategories(categories)
                .withTickPlacement(TickPlacement.BETWEEN)
                .build());
        chart.updateConfig();

        chart.updateSeries(
                new Series<>(DisciplineType.SINGLE.getDisplayString(), series.single().toArray()),
                new Series<>(DisciplineType.DOUBLE.getDisplayString(), series.doubles().toArray()),
                new Series<>(DisciplineType.MIXED.getDisplayString(), series.mixed().toArray()));
    }
}
