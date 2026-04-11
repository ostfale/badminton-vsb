package de.ostfale.va.application.domain.model.playerrankings;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("HistoryTimestamp Tests")
class HistoryTimestampTest {

    @Test
    @DisplayName("Should convert Ranking_YYYY_KWdd.xlsx to dd_yy")
    void shouldConvertInputToCwyear() {
        HistoryTimestamp result = new HistoryTimestamp("Ranking_2026_KW15.xlsx");

        assertEquals("15_26", result.cwyear());
    }

    @Test
    @DisplayName("Should convert one-digit calendar week to dd_yy")
    void shouldConvertOneDigitWeekToCwyear() {
        HistoryTimestamp result = new HistoryTimestamp("Ranking_2026_KW5.xlsx");

        assertEquals("05_26", result.cwyear());
    }

    @Test
    @DisplayName("Should sort by year and then calendar week")
    void shouldSortByYearThenWeek() {
        List<HistoryTimestamp> input = new ArrayList<>(List.of(
                new HistoryTimestamp("Ranking_2025_KW05.xlsx"),
                new HistoryTimestamp("Ranking_2024_KW12.xlsx"),
                new HistoryTimestamp("Ranking_2025_KW01.xlsx"),
                new HistoryTimestamp("Ranking_2024_KW53.xlsx")
        ));

        input.sort(null);

        assertEquals(
                List.of(
                        new HistoryTimestamp("Ranking_2024_KW12.xlsx"),
                        new HistoryTimestamp("Ranking_2024_KW53.xlsx"),
                        new HistoryTimestamp("Ranking_2025_KW01.xlsx"),
                        new HistoryTimestamp("Ranking_2025_KW05.xlsx")
                ),
                input
        );
    }

    @Test
    @DisplayName("Should reject week 00")
    void shouldRejectWeekZero() {
        assertThrows(IllegalArgumentException.class, () -> new HistoryTimestamp("Ranking_2025_KW00.xlsx"));
    }

    @Test
    @DisplayName("Should reject one-digit week 0")
    void shouldRejectSingleDigitWeekZero() {
        assertThrows(IllegalArgumentException.class, () -> new HistoryTimestamp("Ranking_2025_KW0.xlsx"));
    }

    @Test
    @DisplayName("Should reject week above 53")
    void shouldRejectWeekAbove53() {
        assertThrows(IllegalArgumentException.class, () -> new HistoryTimestamp("Ranking_2025_KW54.xlsx"));
    }

    @Test
    @DisplayName("Should reject wrong input format")
    void shouldRejectWrongFormat() {
        assertThrows(IllegalArgumentException.class, () -> new HistoryTimestamp("Ranking_2026_KW15"));
    }
}
