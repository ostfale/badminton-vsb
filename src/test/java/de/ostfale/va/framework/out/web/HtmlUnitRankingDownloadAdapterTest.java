package de.ostfale.va.framework.out.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName( "HtmlUnit Ranking Download Adapter Tests")
class HtmlUnitRankingDownloadAdapterTest {

    private HtmlUnitRankingDownloadAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new HtmlUnitRankingDownloadAdapter(null, null);
    }

    @Test
    @DisplayName("Should format filename with correct year and calendar week for early January")
    void testPrepareDownloadFileNameEarlyYear() {
        // given
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 8, 10, 0);

        // when
        String result = sut.prepareDownloadFileName(dateTime);

        // then
        assertEquals("Ranking_2024_KW2.xlsx", result);
    }

    @Test
    @DisplayName("Should format filename with correct year and calendar week for late December")
    void testPrepareDownloadFileNameLateYear() {
        // given
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 23, 10, 0);

        // when
        String result = sut.prepareDownloadFileName(dateTime);

        // then
        assertEquals("Ranking_2024_KW52.xlsx", result);
    }

    @Test
    @DisplayName("Should format filename with correct year and calendar week for mid-year date")
    void testPrepareDownloadFileNameMidYear() {
        // given
        LocalDateTime dateTime = LocalDateTime.of(2024, 6, 15, 10, 0);

        // when
        String result = sut.prepareDownloadFileName(dateTime);

        // then
        assertEquals("Ranking_2024_KW24.xlsx", result);
    }

    @Test
    @DisplayName("Should format filename correctly for leap year date")
    void testPrepareDownloadFileNameLeapYear() {
        // given
        LocalDateTime dateTime = LocalDateTime.of(2024, 2, 29, 10, 0);

        // when
        String result = sut.prepareDownloadFileName(dateTime);

        // then
        assertEquals("Ranking_2024_KW9.xlsx", result);
    }


}
