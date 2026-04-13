package de.ostfale.va.framework.in.ui.playerranking;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.ChartBuilder;
import com.github.appreciated.apexcharts.config.builder.XAxisBuilder;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.xaxis.TickPlacement;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.ostfale.va.application.domain.model.playerrankings.DisciplineType;
import de.ostfale.va.common.UseLogging;

public class PlayerHistoryChartComponent implements UseLogging {

    private final VerticalLayout rootContainer;
    private final ApexCharts lineChart;
    private final ComboBox<DisciplineType> disciplineSelect;
    private static final String SERIES_NAME = "Demo";


    public PlayerHistoryChartComponent() {
        this.lineChart = initChart();
        this.disciplineSelect = initDisciplineSelect();
        this.rootContainer = initLayout(disciplineSelect, lineChart);
    }

    private ApexCharts initChart() {
        var chart =  ApexChartsBuilder.get()
                .withChart(ChartBuilder.get().withType(Type.LINE).withHeight("340").build())
                .withXaxis(XAxisBuilder.get()
                        .withCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep")
                        .withTickPlacement(TickPlacement.BETWEEN)
                        .build())
                .withSeries(new Series<>(SERIES_NAME, new Number[]{12, 18, 15, 22, 19, 25, 21, 24, 20}))
                .build();

        chart.setWidthFull();
        chart.setHeight("340px");
        chart.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)");
        chart.getStyle().set("border-radius", "8px");
        return chart;
    }

    private VerticalLayout initLayout(ComboBox<DisciplineType> disciplineSelect, ApexCharts lineChart) {
        HorizontalLayout controls = new HorizontalLayout(disciplineSelect);
        controls.setPadding(false);
        controls.setSpacing(true);
        controls.setWidthFull();

        var verticalLayout = new VerticalLayout();
        verticalLayout.setPadding(false);
        verticalLayout.setSpacing(false);
        verticalLayout.add(controls, lineChart);
        return verticalLayout;
    }

    private ComboBox<DisciplineType> initDisciplineSelect() {
        var comboBox = new ComboBox<DisciplineType>("Discipline");
        comboBox.setItems(DisciplineType.SINGLE, DisciplineType.DOUBLE, DisciplineType.MIXED);
        comboBox.setValue(DisciplineType.SINGLE);
        comboBox.setWidth("220px");
        comboBox.addValueChangeListener(event -> updateChart());
        return comboBox;
    }

    private void updateChart() {
        DisciplineType selectedDiscipline = disciplineSelect.getValue();
        Number[] data = switch (selectedDiscipline) {
            case SINGLE -> new Number[]{12, 18, 15, 22, 19, 25, 21, 24, 20};
            case DOUBLE -> new Number[]{8, 11, 10, 13, 12, 14, 16, 15, 17};
            case MIXED -> new Number[]{5, 7, 6, 9, 8, 10, 9, 11, 10};
            default -> new Number[]{12, 18, 15, 22, 19, 25, 21, 24, 20};
        };
        lineChart.updateSeries(new Series<>(SERIES_NAME, data));
        log().debug("PlayerHistoryChartComponent :: updateChart to discipline: {}", selectedDiscipline);
    }

    public Component getComponent() {
        return rootContainer;
    }
}
