package de.ostfale.va.framework.in.ui.playerinfo;

import com.github.appreciated.apexcharts.ApexCharts;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.in.ui.playerinfo.signal.PlayerSelectionState;

import java.util.Locale;

@UIScope
@SpringComponent
public class PlayerHistoryTabComponent extends VerticalLayout implements UseLogging {

    private static final String CHART_NO_DATA_TEXT = "Keine Verlaufsdaten verfuegbar";
    private static final String RANKING_AXIS_LABEL = "Altersrang";
    private static final String POINTS_AXIS_LABEL = "Punkte";
    private static final String TOURNAMENTS_AXIS_LABEL = "Turniere";

    private final HistoryChartDataMapper chartDataMapper = new HistoryChartDataMapper(Locale.GERMAN);
    private final HistoryChartFactory chartFactory = new HistoryChartFactory(CHART_NO_DATA_TEXT);
    private final ApexCharts rankingChart = chartFactory.createChart(RANKING_AXIS_LABEL);
    private final ApexCharts pointsChart = chartFactory.createChart(POINTS_AXIS_LABEL);
    private final ApexCharts tournamentsChart = chartFactory.createChart(TOURNAMENTS_AXIS_LABEL);

    public PlayerHistoryTabComponent(PlayerSelectionState selectionState) {
        setPadding(false);
        setSpacing(true);
        setSizeFull();
        add(createSection("Altersrang", rankingChart));
        add(createSection("Punkte", pointsChart));
        add(createSection("Turniere", tournamentsChart));
        Signal.effect(this, () -> refreshContent(selectionState.getSelectedPlayer().get()));
        addAttachListener(event -> refreshContent(selectionState.getSelectedPlayer().peek()));
    }

    private VerticalLayout createSection(String title, ApexCharts chart) {
        H4 heading = new H4(title);
        heading.getStyle().set("margin", "0");
        VerticalLayout section = new VerticalLayout(heading, chart);
        section.setPadding(false);
        section.setSpacing(false);
        section.setWidthFull();
        return section;
    }

    private void refreshContent(Player player) {
        if (player == null) {
            clearCharts();
            return;
        }

        HistoryChartData chartData = chartDataMapper.map(player);
        if (chartData.isEmpty()) {
            clearCharts();
            return;
        }
        chartFactory.apply(rankingChart, RANKING_AXIS_LABEL, chartData.categories(), chartData.ranking());
        chartFactory.apply(pointsChart, POINTS_AXIS_LABEL, chartData.categories(), chartData.points());
        chartFactory.apply(tournamentsChart, TOURNAMENTS_AXIS_LABEL, chartData.categories(), chartData.tournaments());
    }

    private void clearCharts() {
        chartFactory.clear(rankingChart, RANKING_AXIS_LABEL);
        chartFactory.clear(pointsChart, POINTS_AXIS_LABEL);
        chartFactory.clear(tournamentsChart, TOURNAMENTS_AXIS_LABEL);
    }
}
