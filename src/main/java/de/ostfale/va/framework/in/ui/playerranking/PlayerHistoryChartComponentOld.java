package de.ostfale.va.framework.in.ui.playerranking;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.*;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.yaxis.builder.TitleBuilder;
import com.github.appreciated.apexcharts.helper.Coordinate;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.ostfale.va.application.domain.model.playerrankings.DisciplineType;
import de.ostfale.va.application.domain.model.playerrankings.HistoryTimestamp;
import de.ostfale.va.application.domain.model.playerrankings.HistoryStatistics;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.RankingSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerHistoryChartComponentOld {

    private static final String SERIES_POINTS = "Punkte";
    private static final String SERIES_TOURNAMENTS = "Turniere";
    private static final String SERIES_RANK = "Rang";

    private final VerticalLayout root;
    private final ApexCharts chart;
    private final ComboBox<DisciplineType> disciplineSelect;

    private Player currentPlayer;

    public PlayerHistoryChartComponentOld() {
        this.chart = ApexChartsBuilder.get()
                .withChart(ChartBuilder.get()
                        .withType(Type.LINE)
                        .withHeight("340")
                        .build())
                .withStroke(StrokeBuilder.get()
                        .withCurve(Curve.SMOOTH)
                        .withWidth(2.0)
                        .build())
                .withXaxis(XAxisBuilder.get()
                        .withCategories(List.of())
                        .build())
                .withYaxis(
                        YAxisBuilder.get()
                                .withTitle(TitleBuilder.get().withText("Punkte").build())
                                .build(),
                        YAxisBuilder.get()
                                .withOpposite(true)
                                .withTitle(TitleBuilder.get().withText("Rang").build())
                                .build())
                .withNoData(NoDataBuilder.get()
                        .withText("Keine Verlaufsdaten verfugbar")
                        .build())
                .withSeries(
                        new Series<>(SERIES_POINTS, new Number[0]),
                        new Series<>(SERIES_TOURNAMENTS, new Number[0]),
                        new Series<>(SERIES_RANK, new Number[0])
                )
                .build();
        chart.setWidthFull();
        chart.setHeight("340px");
        chart.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)");
        chart.getStyle().set("border-radius", "8px");

        this.disciplineSelect = new ComboBox<>("Disziplin");
        disciplineSelect.setItems(DisciplineType.SINGLE, DisciplineType.DOUBLE, DisciplineType.MIXED);
        disciplineSelect.setItemLabelGenerator(DisciplineType::getDisplayString);
        disciplineSelect.setValue(DisciplineType.SINGLE);
        disciplineSelect.addValueChangeListener(event -> updateChart());
        disciplineSelect.setWidth("220px");

        HorizontalLayout controls = new HorizontalLayout(disciplineSelect);
        controls.setPadding(false);
        controls.setSpacing(true);
        controls.setWidthFull();

        this.root = new VerticalLayout(controls, chart);
        root.setPadding(false);
        root.setSpacing(false);
        root.setWidthFull();
    }

    public Component getComponent() {
        return root;
    }

    public void updatePlayer(Player player) {
        this.currentPlayer = player;
        selectFirstDisciplineWithData();
        updateChart();
    }

    public void clear() {
        this.currentPlayer = null;
        resetChartData();
    }

    private void resetChartData() {
        chart.setXaxis(XAxisBuilder.get().withCategories(List.of()).build());
        chart.setSeries(
                new Series<>(SERIES_POINTS, new Number[0]),
                new Series<>(SERIES_TOURNAMENTS, new Number[0]),
                new Series<>(SERIES_RANK, new Number[0])
        );
        chart.render();
    }

    private void updateChart() {
        if (currentPlayer == null || currentPlayer.getHistory().isEmpty()) {
            resetChartData();
            return;
        }

        DisciplineType selectedDiscipline = disciplineSelect.getValue();

        List<Map.Entry<HistoryTimestamp, HistoryStatistics>> sortedHistory = currentPlayer.getHistory().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList();

        // Wir nutzen Object-Listen, um Koordinaten oder Zahlen zu speichern
        List<Object> pointsData = new ArrayList<>();
        List<Object> tournamentsData = new ArrayList<>();
        List<Object> rankData = new ArrayList<>();

        for (var entry : sortedHistory) {
            String weekLabel = entry.getKey().cwyear(); // z.B. "07_26"
            RankingSnapshot snapshot = toSnapshot(entry.getValue(), selectedDiscipline);

            if (snapshot != null) {
                // Wir binden das Label direkt an den Datenpunkt (x/y Format)
                pointsData.add(new Coordinate<>(weekLabel, snapshot.points()));
                tournamentsData.add(new Coordinate<>(weekLabel, snapshot.tournaments()));
                rankData.add(new Coordinate<>(weekLabel, snapshot.ranking()));
            }
        }

        if (pointsData.isEmpty()) {
            resetChartData();
            return;
        }

        // TECHNISCHER FIX: Nutze updateSeries statt setSeries + render
        chart.updateSeries(
                new Series<>(SERIES_POINTS, pointsData.toArray()),
                new Series<>(SERIES_TOURNAMENTS, tournamentsData.toArray()),
                new Series<>(SERIES_RANK, rankData.toArray())
        );
    }

    private RankingSnapshot getSnapshotForDiscipline(HistoryStatistics stats, DisciplineType type) {
        if (stats == null || type == null) return null;
        return switch (type) {
            case SINGLE -> stats.getSingleStatistics();
            case DOUBLE -> stats.getDoubleStatistics();
            case MIXED -> stats.getMixedStatistics();
            default -> null;
        };
    }

    private void selectFirstDisciplineWithData() {
        if (currentPlayer == null || currentPlayer.getHistory().isEmpty()) {
            return;
        }

        if (hasDataFor(DisciplineType.SINGLE)) {
            disciplineSelect.setValue(DisciplineType.SINGLE);
            return;
        }
        if (hasDataFor(DisciplineType.DOUBLE)) {
            disciplineSelect.setValue(DisciplineType.DOUBLE);
            return;
        }
        if (hasDataFor(DisciplineType.MIXED)) {
            disciplineSelect.setValue(DisciplineType.MIXED);
        }
    }

    private boolean hasDataFor(DisciplineType disciplineType) {
        return currentPlayer.getHistory().values().stream()
                .map(history -> toSnapshot(history, disciplineType))
                .anyMatch(snapshot -> snapshot != null);
    }

    private RankingSnapshot toSnapshot(HistoryStatistics statistics, DisciplineType selectedDiscipline) {
        // Nur die gewählte Disziplin zurückgeben, kein Fallback auf SINGLE!
        return switch (selectedDiscipline) {
            case SINGLE -> statistics.getSingleStatistics();
            case DOUBLE -> statistics.getDoubleStatistics();
            case MIXED -> statistics.getMixedStatistics();
            default -> null;
        };
    }
}
