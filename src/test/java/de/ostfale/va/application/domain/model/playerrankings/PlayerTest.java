package de.ostfale.va.application.domain.model.playerrankings;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Player Tests")
class PlayerTest {

    @Test
    @DisplayName("Set last updated when currently null")
    void testSetLastUpdatedWhenNull() {
        // given
        Player player = createPlayer();
        HistoryTimestamp newTimestamp = new HistoryTimestamp("Ranking_2024_KW01.xlsx");

        // when
        player.setLastUpdated(newTimestamp);

        // then
        assertEquals(newTimestamp, player.getLastUpdated());
    }

    @Test
    @DisplayName("Overwrite last updated with a newer timestamp")
    void testSetLastUpdatedWithNewerTimestamp() {
        // given
        Player player = createPlayer();
        HistoryTimestamp oldTimestamp = new HistoryTimestamp("Ranking_2024_KW01.xlsx");
        HistoryTimestamp newTimestamp = new HistoryTimestamp("Ranking_2024_KW05.xlsx");
        player.setLastUpdated(oldTimestamp);

        // when
        player.setLastUpdated(newTimestamp);

        // then
        assertEquals(newTimestamp, player.getLastUpdated());
    }

    @Test
    @DisplayName("Do not overwrite last updated with an older timestamp")
    void testSetLastUpdatedWithOlderTimestamp() {
        // given
        Player player = createPlayer();
        HistoryTimestamp currentTimestamp = new HistoryTimestamp("Ranking_2024_KW05.xlsx");
        HistoryTimestamp olderTimestamp = new HistoryTimestamp("Ranking_2024_KW01.xlsx");
        player.setLastUpdated(currentTimestamp);

        // when
        player.setLastUpdated(olderTimestamp);

        // then
        assertEquals(currentTimestamp, player.getLastUpdated());
    }

    @Test
    @DisplayName("Do not change last updated if new timestamp is the same")
    void testSetLastUpdatedWithSameTimestamp() {
        // given
        Player player = createPlayer();
        HistoryTimestamp timestamp1 = new HistoryTimestamp("Ranking_2024_KW05.xlsx");
        HistoryTimestamp timestamp2 = new HistoryTimestamp("Ranking_2024_KW05.xlsx");
        player.setLastUpdated(timestamp1);

        // when
        player.setLastUpdated(timestamp2);

        // then
        assertEquals(timestamp1, player.getLastUpdated());
    }

    @Test
    @DisplayName("Ignore null timestamp")
    void testSetLastUpdatedWithNull() {
        // given
        Player player = createPlayer();
        HistoryTimestamp currentTimestamp = new HistoryTimestamp("Ranking_2024_KW05.xlsx");
        player.setLastUpdated(currentTimestamp);

        // when
        player.setLastUpdated(null);

        // then
        assertEquals(currentTimestamp, player.getLastUpdated());
    }

    @Test
    @DisplayName("Initial last updated should be null")
    void testInitialLastUpdatedIsNull() {
        // given
        Player player = createPlayer();

        // then
        assertNull(player.getLastUpdated());
    }

    private Player createPlayer() {
        return new Player("123", "John", "Doe", GenderType.MALE, 1990, "O19", "O19", "Club", "District", "State", Group.NORTH);
    }
}
