package de.ostfale.va.framework.in.ui.playerinfo;

import de.ostfale.va.application.domain.model.playerrankings.DisciplineType;
import de.ostfale.va.application.domain.model.playerrankings.GenderType;
import de.ostfale.va.application.domain.model.playerrankings.Group;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.RankingSnapshot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("HistoryChartDataMapper Tests")
class HistoryChartDataMapperTest {

    private final HistoryChartDataMapper mapper = new HistoryChartDataMapper(Locale.US);

    @Test
    @DisplayName("Should return empty chart data when player is null")
    void shouldReturnEmptyDataForNullPlayer() {
        HistoryChartData result = mapper.map(null);

        assertTrue(result.isEmpty());
        assertTrue(result.categories().isEmpty());
    }

    @Test
    @DisplayName("Should return empty chart data when player has no history")
    void shouldReturnEmptyDataForPlayerWithoutHistory() {
        Player player = createPlayer();

        HistoryChartData result = mapper.map(player);

        assertTrue(result.isEmpty());
        assertTrue(result.categories().isEmpty());
    }

    @Test
    @DisplayName("Should map weekly history into monthly labels and discipline series")
    void shouldMapHistoryToChartData() {
        Player player = createPlayer();
        player.addHistoryEntry("Ranking_2026_KW5.xlsx", DisciplineType.SINGLE, new RankingSnapshot(100, 1, 11, 4));
        player.addHistoryEntry("Ranking_2026_KW6.xlsx", DisciplineType.DOUBLE, new RankingSnapshot(200, 2, 22, 5));
        player.addHistoryEntry("Ranking_2026_KW7.xlsx", DisciplineType.MIXED, new RankingSnapshot(300, 3, 33, 6));

        HistoryChartData result = mapper.map(player);

        assertEquals(List.of("Jan 26", "Feb 26", ""), result.categories());
        assertEquals(Arrays.asList(11, null, null), result.ranking().single());
        assertEquals(Arrays.asList(null, 22, null), result.ranking().doubles());
        assertEquals(Arrays.asList(null, null, 33), result.ranking().mixed());

        assertEquals(Arrays.asList(100, null, null), result.points().single());
        assertEquals(Arrays.asList(null, 200, null), result.points().doubles());
        assertEquals(Arrays.asList(null, null, 300), result.points().mixed());

        assertEquals(Arrays.asList(4, null, null), result.tournaments().single());
        assertEquals(Arrays.asList(null, 5, null), result.tournaments().doubles());
        assertEquals(Arrays.asList(null, null, 6), result.tournaments().mixed());
    }

    private Player createPlayer() {
        return new Player(
                "1001",
                "Max",
                "Mustermann",
                GenderType.MALE,
                2008,
                "U19",
                "U19-A",
                "Club",
                "District",
                "State",
                Group.NORTH
        );
    }
}
